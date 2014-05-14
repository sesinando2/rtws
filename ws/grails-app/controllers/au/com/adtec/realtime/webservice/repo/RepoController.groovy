package au.com.adtec.realtime.webservice.repo

import au.com.adtec.realtime.webservice.security.RestToken
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import org.springframework.web.multipart.MultipartHttpServletRequest
import org.springframework.web.multipart.commons.CommonsMultipartFile

@Secured(["ROLE_ADMIN", "ROLE_REPO_ADMIN"])
class RepoController {

    static allowedMethods = [list: 'GET', delete: ['GET', 'DELETE'], upload: 'POST', download: 'GET']

    def repoService
    def tokenService

    def index() { }

    def list() {
        render FileData.list().collect { it.id } as JSON
    }

    @Secured(["ROLE_ADMIN", "ROLE_REPO_ADMIN", "ROLE_REPO_UPLOAD"])
    def upload() {
        if (request instanceof MultipartHttpServletRequest) {
            RestToken restToken = RestToken.findByToken(token)
            if ((restToken && restToken?.isAllowedForFileCount(request.fileNames.size())) || repoService.isAdmin) {
                log.debug("Authorized. Saving files...")
                def urls = [:]
                for (def fileName : request.fileNames) {
                    if (restToken && restToken?.isValid || repoService.isAdmin) {
                        CommonsMultipartFile file = request.getFile(fileName)
                        FileData fileData = repoService.createFile(file, restToken, params)
                        if (fileData) {
                            urls.put(fileData.id.toString(), g.createLink(action: 'download', id: fileData?.id, absolute: true))
                            if (restToken) tokenService.createDownloadTokenRestriction(restToken.token, fileData, 0)
                        }
                    }
                }
                if (restToken && !restToken?.isValid) restToken.delete()
                render(urls as JSON);
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
        if (repoService.isAdmin || (token && token.isAllowedForFile(id))) {
            FileData file = repoService.getFile(id, token, params)
            if (file) {
                if (token && !token?.isValid) token.delete()
                response.setHeader("Content-disposition", "filename=$file.filename")
                response.outputStream << file.data
                return
            } else {
                response.status = 404;
                render([error: "Cannot find file with id '$id'"] as JSON)
                return
            }
        } else {
            render(status: 401, text: "Token not allowed for file with id: $id")
        }
    }

    def delete(int id) {
        FileData file = FileData.get(id);
        if (file) {
            def response = [success: true];
            try { file.delete() } catch (all) {
                response = [success: false, message: "An error occurred while trying to delete file with id '$id'"]
            }
            render response as JSON
        } else {
            response.status = 404;
            render([error: "Cannot find file with id '$id'"] as JSON)
        }
    }

    def purge() {
        FileData.deleteAll(FileData.all)
        redirect(action: "index")
    }

    private def getToken() {
        RestToken.findByToken(request.getHeader('X-Auth-Token')) ?: request.getParameter('token')
    }
}
