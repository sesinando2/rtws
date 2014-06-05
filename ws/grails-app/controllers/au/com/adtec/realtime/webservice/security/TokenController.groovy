package au.com.adtec.realtime.webservice.security

import au.com.adtec.realtime.webservice.messaging.Message
import au.com.adtec.realtime.webservice.messaging.MessagingService
import au.com.adtec.realtime.webservice.repo.FileData
import au.com.adtec.realtime.webservice.repo.RepoService
import au.com.adtec.realtime.webservice.security.token.RestToken
import au.com.adtec.realtime.webservice.security.token.restriction.TokenRestriction
import com.odobo.grails.plugin.springsecurity.rest.RestAuthenticationProvider
import com.odobo.grails.plugin.springsecurity.rest.RestAuthenticationToken
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder

class TokenController {

    def grailsApplication
    TokenService tokenService
    RepoService repoService
    MessagingService messagingService

    RestAuthenticationProvider restAuthenticationProvider

    static allowedMethods = [list: 'GET', clear: ['GET', 'DELETE'], delete: ['GET', 'DELETE'], request: ['GET', 'POST'], password: ['GET', 'POST'], revoke: ['GET', 'POST']]

    def beforeInterceptor = [action: this.&validateAccess, only:['request', 'requestTracked']]

    @Secured(["permitAll"])
    def login(String token) {
        if (token) {
            try {
                log.debug "Trying to authenticate the token"
                RestAuthenticationToken authenticationRequest = new RestAuthenticationToken(token)
                RestAuthenticationToken authenticationResult = restAuthenticationProvider.authenticate(authenticationRequest)

                if (authenticationResult.authenticated) {
                    log.debug "Token authenticated. Storing the authentication result in the security context"
                    log.debug "Authentication result: ${authenticationResult}"
                    SecurityContextHolder.context.setAuthentication(authenticationResult)

                }
                redirect(controller: "home")
                return
            } catch (AuthenticationException ae) {
                log.debug "Authentication failed: ${ae.message}"
            }
        } else {
            log.debug "Token not found"
        }
        flash.message = "The token you have entered is invalid."
        redirect(uri: "/login/token")
    }

    @Secured(["ROLE_ADMIN"])
    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond RestToken.list(params), model:[restTokenInstanceCount: RestToken.count()]
    }

    @Secured(["ROLE_ADMIN"])
    def addToken(String type) {
        try {
            String tokenValue
            switch (type) {
                case "upload":
                    tokenValue = tokenService.generateToken(RepoService.Users.REPO_UPLOAD)
                    break
                case "download":
                    tokenValue = tokenService.generateToken(RepoService.Users.REPO_READ)
                    break
                case "tracked":
                    tokenValue = tokenService.generateToken(MessagingService.Users.MESSAGING_REPO_READ_USER)
                    break
                case "message":
                    tokenValue = tokenService.generateToken(MessagingService.Users.MESSAGING_USER)
                    break
            }
            def restToken = RestToken.findByToken(tokenValue)
            redirect(action: "view", id: restToken.id)
            return
        } catch (all) {
            flash.message = "An error has occurred while attempting to generate a token."
            redirect(action: "index")
        }
    }

    @Secured(["ROLE_ADMIN"])
    def view(RestToken restToken) {
        if (restToken) {
            def roles = User.findByUsername(restToken?.login).authorities.collect { it.authority }.join(", ")
            def restrictions = TokenRestriction.findAllByToken(restToken)
            respond restToken, model: [roles: roles, restrictions: restrictions]
            return
        } else {
            flash.message = "Cannot find Token with ID: ${params?.id}"
            redirect(action: "index")
        }
    }

    @Secured(["ROLE_ADMIN"])
    def addTokenRestriction(RestToken token, String type) {
        if (token) {
            withForm {
                switch (type) {
                    case "UPLOAD":
                        addUploadRestriction(token)
                        break
                    case "DOWNLOAD":
                        addDownloadRestriction(token)
                        break
                    case "MESSAGE":
                        addMessageRestriction(token)
                        break
                }
            }.invalidToken {
                flash.tokenRestrictionMessage = "The server has detected that the form has been submitted multiple times. Please submit the from only once."
            }
        } else {
            flash.tokenRestrictionMessage = "Cannot find Token with ID: ${params?.id}"
        }
        redirect(action: "view", id: token.id)
    }

    //region Add Token Restriction Helper Methods
    private void addUploadRestriction(RestToken token) {
        if (token.login == RepoService.USER_REPO_UPLOAD) {
            int fileCount = params?.fileCount ?: 0
            tokenService.createUploadRestriction(token.token, fileCount)
        }
    }

    private void addDownloadRestriction(RestToken token) {
        if (token.login == RepoService.USER_REPO_UPLOAD ||
            token.login == RepoService.USER_REPO_READ   ||
            token.login == MessagingService.USER_MESSAGING_REPO_READ) {

            int downloadCount = params?.downloadCount ?: 0
            def file = FileData.get(params?.file as Serializable)
            if (file) {
                tokenService.createDownloadRestriction(token.token, file, downloadCount)
            }
        }
    }

    private void addMessageRestriction(RestToken token) {
        if (token.login == MessagingService.USER_MESSAGING_USER ||
            token.login == MessagingService.USER_MESSAGING_REPO_READ) {

            int readCount = params?.readCount ?: 0
            int responseCount = params?.responseCount ?: 0
            def message = Message.get(params?.message as Serializable)
            println message
            if (message) {
                tokenService.createMessageRestriction(message, token.token, readCount, responseCount)
            }
        }
    }
    //endregion

    @Secured(["ROLE_ADMIN"])
    def deleteTokenRestriction(TokenRestriction restriction) {
        if (restriction) {
            if (restriction) {
                restriction.delete()
            }
        } else {
            flash.authorityMessage = "Cannot find Token Restriction with ID: ${params?.id}"
        }
        redirect(action: "view", id: params?.token)
    }

    @Secured(["ROLE_ADMIN"])
    def clear() {
        RestToken.deleteAll(RestToken.list())
        render([success: true] as JSON)
    }

    @Secured(["ROLE_ADMIN"])
    def revoke() {
        def tokenList = request?.JSON as String[]
        def tokens = RestToken.where { token in tokenList }.list()
        RestToken.deleteAll(tokens)
        render([success: true] as JSON)
    }

    @Secured(["ROLE_ADMIN"])
    def delete(RestToken restToken) {
        restToken?.delete()
        redirect(action: "index")
    }

    @Secured(["ROLE_ADMIN"])
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

    @Secured(["ROLE_ADMIN"])
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

    @Secured(["ROLE_ADMIN"])
    def requestTrackedDownloadToken(Message message, int downloadCount, int readCount, int responseCount, String membersIdCsv, String fileIdCsv) {
        def response = [:]
        if (message.validate()) {
            def tokenMember = messagingService.createMessage(message, membersIdCsv, fileIdCsv, downloadCount, readCount, responseCount)
            response.success = true
            response.tokens = tokenMember
        } else {
            response.success = false
            response.errors = message.errors
        }
        render response as JSON
    }

    @Secured(["permitAll"])
    def trackToken(String token, String tokenAction) {
        RestToken restToken = RestToken.findByToken(token)
        if (restToken) {
            messagingService.processMessageProgress(restToken, tokenAction)
            def image = repoService.getImageResource("images/tracker.png")
            render file: image.inputStream, contentType: "image/png"
            return
        } else {
            render(status: 404, text: "Token $token not found.");
        }
    }

    @Secured(["permitAll"])
    private validateAccess() {
        if (grailsApplication.config.au.com.adtec.security.localTokenGenerationOnly && request.remoteAddr != "127.0.0.1") {
            render(status: 401)
            return false;
        }
    }
}
