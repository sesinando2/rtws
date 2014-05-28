package au.com.adtec.realtime.webservice.repo

import au.com.adtec.realtime.webservice.security.RestToken
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import org.grails.plugins.imagetools.ImageTool
import org.springframework.web.multipart.MultipartHttpServletRequest
import org.springframework.web.multipart.commons.CommonsMultipartFile

class RepoController {

    static allowedMethods = [list: 'GET', delete: ['GET', 'DELETE'], upload: 'POST', download: 'GET']

    def beforeInterceptor = [action: this.&authorizeDownloadToken, except: ['index', 'list', 'delete', 'purge', 'upload', 'thumb', 'square', 'rect', 'height']]

    def repoService
    def tokenService

    //region Actions
    def index() { }

    def list() { render FileData.list().collect { it.id } as JSON }

    @Secured(["ROLE_ADMIN", "ROLE_REPO_ADMIN", "ROLE_REPO_UPLOAD"])
    def upload() {
        if (request instanceof MultipartHttpServletRequest) {
            RestToken restToken = RestToken.findByToken(token)
            if ((restToken && restToken?.isAllowedForFileCount(request.fileNames.size())) || repoService.isAdmin) {
                log.debug("Authorized. Saving files...")
                def items = []
                for (def fileName : request.fileNames) {
                    if (restToken && restToken?.isValid || repoService.isAdmin) {
                        CommonsMultipartFile file = request.getFile(fileName)
                        FileData fileData = repoService.createFile(file, restToken, params)
                        if (fileData) {
                            items.add(fileData.id)
                            if (restToken) tokenService.createDownloadRestriction(restToken.token, fileData, 0)
                        }
                    }
                }
                if (restToken && !restToken?.isValid) restToken.delete()
                render([base: g.createLink(action: 'download', absolute: true), items: items] as JSON);
                return
            } else {
                render(status: 401, text: "Token not allowed for " + request.fileNames.size() + " more uploads")
                return
            }
        }
        render(stauts: 404)
    }

    @Secured(["ROLE_ADMIN", "ROLE_REPO_ADMIN", "ROLE_REPO_READ"])
    def download(int id) {
        RestToken token = RestToken.findByToken(token)
        FileData file = repoService.getFile(id, token, params)
        doActionForFile(file) {
            if (token && !token?.isValid) token.delete()
            def data = file.data
            response.setHeader("Content-disposition", "filename=$file.filename")
            response.setHeader("Content-type", "$file.contentType")
            response.setHeader("Content-length", "$data.length")
            response.outputStream << data
        }
    }

    @Secured(["permitAll"])
    def thumb(String token, int id, int width, int height) {
        RestToken restToken = RestToken.findByToken(token);
        if (!(restToken && restToken.isAllowedForFile(idAsArray))) {
            render(status: 401, text: "Token not allowed for file with id/s: $params.id")
            return
        }
        FileData file = repoService.getFile(id, restToken, [:])
        doActionForFile(file) {
            if (token && !restToken?.isValid) restToken.delete()
            ImageTool imageTool = repoService.loadImage(file)
            imageTool.thumbnailSpecial(width, height, 1, 1)
            file = new FileData(file.properties)
            file.data = imageTool.getBytes("JPEG")
            response.setHeader("Content-disposition", "filename=$file.filename")
            response.setHeader("Content-length", "$file.data.length")
            response.setContentType("image/jpeg")
            response.setContentLength(file.data.length)
            response.outputStream << file.data
        }
    }

    @Secured(["permitAll"])
    def square(String token, int id, int thumb) {
        RestToken restToken = RestToken.findByToken(token);
        if (!(restToken && restToken.isAllowedForFile(idAsArray))) {
            render(status: 401, text: "Token not allowed for file with id/s: $params.id")
            return
        }
        FileData file = repoService.getFile(id, restToken, [:])
        doActionForFile(file) {
            if (token && !restToken?.isValid) restToken.delete()
            ImageTool imageTool = repoService.loadImage(file)
            imageTool.square()
            imageTool.swapSource()
            imageTool.thumbnail(thumb)
            file = new FileData(file.properties)
            file.data = imageTool.getBytes("JPEG")
            response.setHeader("Content-disposition", "filename=$file.filename")
            response.setHeader("Content-length", "$file.data.length")
            response.setContentType("image/jpeg")
            response.setContentLength(file.data.length)
            response.outputStream << file.data
        }
    }

    @Secured(["permitAll"])
    def rect(String token, int id, int width, int height) {
        RestToken restToken = RestToken.findByToken(token);
        if (!(restToken && restToken.isAllowedForFile(idAsArray))) {
            render(status: 401, text: "Token not allowed for file with id/s: $params.id")
            return
        }
        FileData file = repoService.getFile(id, restToken, [:])
        doActionForFile(file) {
            if (token && !restToken?.isValid) restToken.delete()
            ImageTool imageTool = repoService.loadImage(file)
            imageTool.thumbnailMin([width, height].max())
            imageTool.swapSource()
            imageTool.crop(imageTool.width - width, imageTool.height - height)
            file = new FileData(file.properties)
            file.data = imageTool.getBytes("JPEG")
            response.setHeader("Content-disposition", "filename=$file.filename")
            response.setHeader("Content-length", "$file.data.length")
            response.setContentType("image/jpeg")
            response.setContentLength(file.data.length)
            response.outputStream << file.data
        }
    }

    @Secured(["permitAll"])
    def height(String token, int id, int height) {
        RestToken restToken = RestToken.findByToken(token);
        if (!(restToken && restToken.isAllowedForFile(idAsArray))) {
            render(status: 401, text: "Token not allowed for file with id/s: $params.id")
            return
        }
        FileData file = repoService.getFile(id, restToken, [:])
        doActionForFile(file) {
            if (token && !restToken?.isValid) restToken.delete()
            ImageTool imageTool = repoService.loadImage(file)
            imageTool.height = height
            file = new FileData(file.properties)
            file.data = imageTool.getBytes("JPEG")
            response.setHeader("Content-disposition", "filename=$file.filename")
            response.setHeader("Content-length", "$file.data.length")
            response.setContentType("image/jpeg")
            response.setContentLength(file.data.length)
            response.outputStream << file.data
        }
    }

    @Secured(["ROLE_ADMIN", "ROLE_REPO_ADMIN", "ROLE_REPO_READ"])
    def details() {
        def details = [:]
        for (def id : idAsArray) {
            FileData file = FileData.get(id)
            if (file) details.put(id, [fileName: file.filename, contentType: file.contentType, contentLength: file.data.length])
        }
        render(details as JSON)
    }

    @Secured(["ROLE_ADMIN", "ROLE_REPO_ADMIN"])
    def delete(int id) {
        FileData file = FileData.get(id);
        doActionForFile(file) {
            def response = [success: true];
            try { file.delete() } catch (all) {
                response = [success: false, message: "An error occurred while trying to delete file with id '$id'"]
            }
            render response as JSON
        }
    }

    @Secured(["ROLE_ADMIN", "ROLE_REPO_ADMIN"])
    def purge() {
        FileData.deleteAll(FileData.all)
        redirect(action: "index")
    }
    //endregion

    private doActionForFile(FileData file, Closure action) {
        if (file) {
            action()
            return
        } else {
            response.status = 404;
            render([error: "Cannot find file with id '$params.id'"] as JSON)
            return
        }
    }

    private getToken() {
        request.getHeader('X-Auth-Token') ?: request.getParameter('token')
    }

    private getIdAsArray() {
        def ids = params?.id
        if (ids.contains(",")) {
            def intArray = ids.split(",").collect { it as Integer }
            return intArray as int[]
        }
        else return [ids as int] as int[]
    }

    private authorizeDownloadToken() {
        RestToken token = RestToken.findByToken(token)
        if (!repoService.isAdmin && !(token && token.isAllowedForFile(idAsArray))) {
            render(status: 401, text: "Token not allowed for file with id/s: $params.id")
            return false
        }
    }
}
