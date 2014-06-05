package au.com.adtec.realtime.webservice.repo

import au.com.adtec.realtime.webservice.AbstractService
import au.com.adtec.realtime.webservice.security.token.restriction.DownloadTokenRestriction
import au.com.adtec.realtime.webservice.security.token.RestToken
import au.com.adtec.realtime.webservice.security.Role
import au.com.adtec.realtime.webservice.security.User
import com.odobo.grails.plugin.springsecurity.rest.RestAuthenticationToken
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.transaction.Transactional
import org.grails.plugins.imagetools.ImageTool
import org.springframework.core.io.Resource
import org.springframework.web.multipart.commons.CommonsMultipartFile

@Transactional
class RepoService extends AbstractService {

    //region Constants
    static final String ROLE_REPO_ADMIN = "ROLE_REPO_ADMIN"
    static final String ROLE_REPO_READ = "ROLE_REPO_READ"
    static final String ROLE_REPO_UPLOAD = "ROLE_REPO_UPLOAD"

    static final String USER_REPO_ADMIN = "repo_admin"
    static final String USER_REPO_READ = "repo_read"
    static final String USER_REPO_UPLOAD = "repo_upload"
    //endregion

    def videoService
    def grailsResourceLocator

    def initializeRoles() {
        Roles.REPO_ADMIN = createRole(ROLE_REPO_ADMIN)
        Roles.REPO_READ = createRole(ROLE_REPO_READ)
        Roles.REPO_UPLOAD = createRole(ROLE_REPO_UPLOAD)
    }

    def initializeUsers() {
        Users.REPO_ADMIN = createUser(USER_REPO_ADMIN, "admin:)", Roles.REPO_ADMIN)
        Users.REPO_READ = createUser(USER_REPO_READ, "admin:)", Roles.REPO_READ)
        Users.REPO_UPLOAD = createUser(USER_REPO_UPLOAD, "admin:)", Roles.REPO_UPLOAD, Roles.REPO_READ)
    }

    FileData createFile(CommonsMultipartFile file) {
        new FileData(filename: file.originalFilename, data: file.bytes, contentType: file.contentType).save()
    }

    FileData createFile(CommonsMultipartFile file, RestToken restToken, Map params) {
        FileData fileData
        switch (getFileType(file.contentType)) {
            case FileType.IMAGE:
                fileData = createImageFile(file, params)
                break
            case FileType.VIDEO:
                fileData = createVideoFileData(file)
                videoService.createVideoThumbnail(fileData as VideoFileData)
                break
            default:
                fileData = createFile(file)
                break
            }
        createUploadLog(fileData, restToken)
        return fileData
    }

    FileData createImageFile(CommonsMultipartFile file, Map params) {
        new ImageFileData(
                filename: file?.originalFilename,
                data: file.bytes,
                contentType: file.contentType,
                timestamp: params?.timestamp,
                location: params?.location
        ).save();
    }

    VideoFileData createVideoFileData(CommonsMultipartFile file) {
        new VideoFileData(filename: file.originalFilename, data: file.bytes, contentType: file.contentType).save()
    }

    FileType getFileType(String contentType) {
        switch (contentType) {
            case ~/image\/(.+)/: return FileType.IMAGE
            case ~/audio\/(.+)/: return FileType.AUDIO
            case ~/video\/(.+)/: return FileType.VIDEO
            default:             return FileType.OTHER
        }
    }

    FileData getFile(int id, RestToken restToken, Map params) {
        FileData fileData = null
        if (restToken) {
            fileData = getFileFromToken(restToken, id)
        } else if (isAdmin) {
            fileData = FileData.get(id)
        }

        if (params?.thumb && params?.thumb?.toString()?.number) {
            fileData = resizeImage(fileData, params)
        }

        return fileData
    }

    ImageTool loadImage(FileData file, Map params = [:]) {
        ImageTool imageTool = new ImageTool()
        switch (file) {
            case { it instanceof VideoFileData && it.fileType == FileType.VIDEO }:
                imageTool.load(file?.thumbData)
                if (!params.containsKey("nooverlay")) {
                    String alpha = grailsResourceLocator.findResourceForURI("images/play-button-overlay.png").file.absolutePath
                    imageTool.loadMask(alpha)
                    imageTool.applyMask()
                    imageTool.swapSource()
                }
                break
            case { it.fileType == FileType.AUDIO }:
                def audio = grailsResourceLocator.findResourceForURI("images/audio-icon.png").file.absolutePath
                imageTool.load(audio)
                break
            default:
                String alpha = grailsResourceLocator.findResourceForURI("images/file-icon.png").file.absolutePath
                imageTool.load(alpha)
        }
        return imageTool
    }

    List<FileData> getFiles(id) {
        List<FileData> fileList = []
        if (id instanceof Integer) {
            FileData fileData = FileData.get(id)
            if (fileData) fileList.add(fileData)
        } else if (id instanceof List<Long>) {
            def resourceIDs = id.collect { it as long }
            fileList.addAll(FileData.where { id in resourceIDs }.list())
        }
        return fileList
    }

    List<FileData> getFilesForUser(Map params = [:]) {
        if (isAdmin) {
            return FileData.list(params)
        } else if (springSecurityService.authentication instanceof RestAuthenticationToken) {
            def tokenValue = (springSecurityService.authentication as RestAuthenticationToken).tokenValue
            return getFilesFromToken(tokenValue)
        }
        return []
    }

    Integer countForUser() {
        if (isAdmin) {
            return FileData.count()
        } else if (springSecurityService.authentication instanceof RestAuthenticationToken) {
            def tokenValue = (springSecurityService.authentication as RestAuthenticationToken).tokenValue
            return DownloadTokenRestriction.where { token.token == tokenValue }.count()
        }
    }

    Resource getImageResource(String filePath) {
        return grailsResourceLocator.findResourceForURI(filePath)
    }

    Boolean getIsAdmin() {
        SpringSecurityUtils.ifAnyGranted("ROLE_ADMIN, ROLE_REPO_ADMIN")
    }

    void logDownload(FileData fileData, RestToken restToken) {
        if (restToken?.isValid) {
            createFileLog(restToken, fileData, FileDataAction.DOWNLOAD)
        }
    }

    void logThumbnail(FileData fileData, RestToken restToken) {
        if (restToken?.isValid) {
            createFileLog(restToken, fileData, FileDataAction.THUMBNAIL)
        }
    }

    private FileData resizeImage(FileData fileData, Map params) {
        def thumbnail = new FileData(contentType: "image/jpeg", filename: "$fileData.filename-thumb.jpg")
        ImageTool imageTool = loadImage(fileData, params)
        def thumbSize = params?.thumb as int
        imageTool.thumbnail(thumbSize)
        thumbnail.data = imageTool.getBytes("JPEG")
        return thumbnail
    }

    private FileData getFileFromToken(RestToken restToken, int id) {
        if (restToken) {
            if (restToken.isValid) {
                def restrictions = DownloadTokenRestriction.where { token == restToken && fileData.id == id }.list()
                if (!restrictions.empty) return restrictions.first().fileData
            }
        }
        return null
    }

    private List<FileData> getFilesFromToken(String tokenValue, Map params = [:]) {
        return getFilesFromToken(RestToken.findByToken(tokenValue), params)
    }

    private List<FileData> getFilesFromToken(RestToken restToken, Map params = [:]) {
        if (restToken) {
            if (restToken.isValid) {
                def restrictions = DownloadTokenRestriction.where { token == restToken }.list(params)
                return restrictions.collect { it.fileData }
            }
        }
    }

    private void createUploadLog(FileData fileData, RestToken restToken) {
        if (restToken?.isValid) {
            createFileLog(restToken, fileData, FileDataAction.UPLOAD)
        }
    }

    private createFileLog(RestToken token, FileData file, FileDataAction action) {
        if (token && file && action)
            new FileDataLog(token: token, fileData: file, action: action, tokenValue: token?.token).save(flush: true)
    }

    //region Roles & Users
    static class Roles {
        static Role REPO_ADMIN
        static Role REPO_READ
        static Role REPO_UPLOAD
    }

    static class Users {
        static User REPO_ADMIN
        static User REPO_READ
        static User REPO_UPLOAD
        static User MESSAGING_REPO_READ_USER
    }
    //endregion
}
