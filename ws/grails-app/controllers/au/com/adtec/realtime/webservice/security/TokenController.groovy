package au.com.adtec.realtime.webservice.security

import au.com.adtec.realtime.webservice.repo.RepoService
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

@Secured(["ROLE_ADMIN"])
class TokenController {

    def grailsApplication
    TokenService tokenService
    RepoService repoService

    static allowedMethods = [list: 'GET', clear: ['GET', 'DELETE'], delete: ['GET', 'DELETE'], request: ['GET', 'POST'], password: ['GET', 'POST'], revoke: ['GET', 'POST']]

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

    def revoke() {
        def tokenList = request?.JSON as String[]
        def tokens = RestToken.where { token in tokenList }.list()
        RestToken.deleteAll(tokens)
        render([success: true] as JSON)
    }

    def delete(int id) {
        RestToken.get(id)?.delete()
        render([success: true] as JSON)
    }

    def password(String password) {
        if (password) {
            def adminUser = User.findByUsername("admin")
            if (adminUser) {
                adminUser.password = password
                adminUser.save(flush: true)
                render([success: true, message: 'Admin password has been updated successfully'] as JSON)
                return
            }
        }
        render(status: 404)
    }

    def request() {
        if (grailsApplication.config.au.com.adtec.security.localTokenGenerationOnly && request.remoteAddr != "127.0.0.1") {
            render(status: 401)
            return
        }

        def json = request?.JSON as Map
        if (json.isEmpty()) {
            json = [authority: params?.authority,
                    amount: (params?.amount ?: '1') as int,
                    accessCount: (params?.accessCount ?: '0') as int,
                    fileCount: (params?.fileCount ?: '0') as int]
            if (params?.id?.number) json.put("id", params?.id as int)
        }
        def tokenList
        switch (json?.authority) {
            case RepoService.ROLE_REPO_READ: // CREATE DOWNLOAD TOKEN
                def files = repoService.getFiles(json?.id)
                if (!files || files.empty) {
                    render(status: 404, text: "Cannot find resources with id: $json.id")
                    return
                }
                int amount = json?.amount ?: 1
                int accessCount = json?.accessCount ?: 0
                tokenList = tokenService.generateDownloadToken(files, amount, accessCount)
                break
            case RepoService.ROLE_REPO_UPLOAD: // CREATE UPLOAD TOKEN
                int fileCount = (json?.fileCount ?: 0 as int) ?: 0
                tokenList = tokenService.generateUploadToken(fileCount)
                break
            default:
                render(status: 404, text: "Unsupported authority: $json.authority")
                return
        }
        render(tokenList as JSON)
    }
}
