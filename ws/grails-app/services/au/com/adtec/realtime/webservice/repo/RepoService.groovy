package au.com.adtec.realtime.webservice.repo

import au.com.adtec.realtime.webservice.AbstractService
import au.com.adtec.realtime.webservice.security.RestToken
import au.com.adtec.realtime.webservice.security.Role
import au.com.adtec.realtime.webservice.security.TokenRestriction
import au.com.adtec.realtime.webservice.security.User
import grails.transaction.Transactional
import org.grails.plugins.imagetools.ImageTool
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

    def initializeRoles() {
        Roles.REPO_ADMIN = createRole(ROLE_REPO_ADMIN)
        Roles.REPO_READ = createRole(ROLE_REPO_READ)
        Roles.REPO_UPLOAD = createRole(ROLE_REPO_UPLOAD)
    }

    def initializeUsers() {
        Users.REPO_ADMIN = createUser(USER_REPO_ADMIN, "admin:)", Roles.REPO_ADMIN)
        Users.REPO_READ = createUser(USER_REPO_READ, "admin:)", Roles.REPO_READ)
        Users.REPO_UPLOAD = createUser(USER_REPO_UPLOAD, "admin:)", Roles.REPO_UPLOAD)
    }

    FileData createFile(CommonsMultipartFile file, Map params) {
        switch (getFileType(file.contentType)) {
            case FileType.IMAGE: return createImageFile(file, params)
            default:             return createFile(file)
        }
    }

    FileData createFile(CommonsMultipartFile file) {
        new FileData(filename: file.originalFilename, data: file.bytes, contentType: file.contentType).save()
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

    FileType getFileType(String contentType) {
        switch (contentType) {
            case ~/image\/(.+)/: return FileType.IMAGE
            case ~/audio\/(.+)/: return FileType.AUDIO
            case ~/video\/(.+)/: return FileType.VIDEO
            default:             return FileType.OTHER
        }
    }

    FileData getFile(int id, String token, Map params) {
        if (!token && !currentUser.authorities.collect { it.authority }.contains('ROLE_ADMIN')) return
        if (token) {
            RestToken restToken = RestToken.findByToken(token)
            def restrictions = TokenRestriction.where { token == restToken && fileData?.id == id }.list()
            if (restrictions.empty) return
        }
        def file = FileData.get(id)
        if (file.isImage && params?.thumb && params.thumb.toString().number) {
            ImageTool imageTool = new ImageTool()
            imageTool.load(file.data)
            imageTool.thumbnail(params?.thumb as int)
            file.data = imageTool.getBytes("JPEG")
        }
        return FileData.get(id)
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
    }
    //endregion
}
