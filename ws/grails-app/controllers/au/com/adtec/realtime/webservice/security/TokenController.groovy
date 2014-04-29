package au.com.adtec.realtime.webservice.security

import au.com.adtec.realtime.webservice.repo.RepoService
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

@Secured(["ROLE_ADMIN"])
class TokenController {

    TokenService tokenService
    RepoService repoService

    static allowedMethods = [list: 'GET', clear: 'DELETE', delete: 'DELETE', request: 'POST']

    def index() {
        redirect(action: 'list')
    }

    def list() {
        def tokenList = RestToken.list();
        render(tokenList as JSON)
    }

    def clear() {
        RestToken.deleteAll(RestToken.list())
        render([success: true] as JSON)
    }

    def delete(int id) {
        RestToken.get(id)?.delete()
        render([success: true] as JSON)
    }

    def request() {

        /*if (request.localName != "localhost" || request.remoteAddr != "127.0.0.1") {
            render(status: 401)
            return
        }*/

        def json = request?.JSON
        def tokenList = []
        switch (json?.authority) {
            case RepoService.ROLE_REPO_READ: // CREATE DOWNLOAD TOKEN
                def files = repoService.getFiles(json?.id)
                if (!files || files.empty) {
                    render(status: 404, text: "Cannot find resources with id: $json.id")
                    return
                }
                int amount = (json?.amount ?: '1' as int) ?: 1
                int accessCount = (json?.accessCount ?: '0' as int) ?: 0
                tokenList = tokenService.generateDownloadToken(files, amount, accessCount)
                break
            case RepoService.ROLE_REPO_UPLOAD: // CREATE UPLOAD TOKEN
                int fileCount = (json?.fileCount ?: 0 as int) ?: 0
                tokenList = tokenService.generateUploadToken(fileCount)
                break
        }

        render(tokenList as JSON)
    }
}
