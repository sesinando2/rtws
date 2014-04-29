package au.com.adtec.realtime.webservice.repo

import au.com.adtec.realtime.webservice.security.RestToken
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import org.springframework.web.multipart.MultipartHttpServletRequest
import org.springframework.web.multipart.commons.CommonsMultipartFile

@Secured(["ROLE_ADMIN", "ROLE_REPO_ADMIN"])
class RepoController {

    static allowedMethods = [list: 'GET', delete: 'DELETE', upload: 'POST', download: 'GET']

    def repoService

    def index() { }

    def list() {
        render FileData.list().collect { it.id } as JSON
    }

    @Secured(["ROLE_ADMIN", "ROLE_REPO_ADMIN", "ROLE_REPO_UPLOAD"])
    def upload() {
        if (request instanceof MultipartHttpServletRequest) {
            RestToken token = RestToken.findByToken(request.getHeader('X-Auth-Token'))
            if (token?.isAllowedForFileCount(request.fileNames.size()) || repoService.isAdmin) {
                def urls = [:]
                for (def fileName : request.fileNames) {
                    if (token && token?.isValid || repoService.isAdmin) {
                        CommonsMultipartFile file = request.getFile(fileName)
                        FileData fileData = repoService.createFile(file, token, params)
                        if (fileData) urls.put(fileData.id, g.createLink(action: 'download', id: fileData?.id, absolute: true))
                    }
                }
                if (token && !token?.isValid) token.delete()
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
        RestToken token = RestToken.findByToken(request.getHeader('X-Auth-Token'))
        if (repoService.isAdmin || (token && token.isAllowedForFile(id))) {
            FileData file = repoService.getFile(id, token, params)
            if (file) {
                if (token && !token?.isValid) token.delete()
                response.contentType = file.contentType
                response.outputStream << file.data
                response.outputStream.flush()
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
}
