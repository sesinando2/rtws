package au.com.adtec.realtime.webservice.security

import au.com.adtec.realtime.webservice.messaging.Message
import au.com.adtec.realtime.webservice.messaging.MessagingService
import au.com.adtec.realtime.webservice.repo.FileData
import au.com.adtec.realtime.webservice.repo.RepoService
import au.com.adtec.realtime.webservice.security.token.restriction.DownloadTokenRestriction
import au.com.adtec.realtime.webservice.security.token.restriction.MessageTokenRestriction
import au.com.adtec.realtime.webservice.security.token.RestToken
import au.com.adtec.realtime.webservice.security.token.restriction.UploadTokenRestriction
import com.odobo.grails.plugin.springsecurity.rest.token.generation.TokenGenerator
import com.odobo.grails.plugin.springsecurity.rest.token.storage.TokenStorageService
import grails.plugin.springsecurity.SpringSecurityService
import grails.transaction.Transactional

@Transactional
class TokenService {

    def grailsApplication
    TokenGenerator tokenGenerator
    TokenStorageService tokenStorageService
    SpringSecurityService springSecurityService

    def cleanUpToken() {
        def tokenExpiry = grailsApplication.config.au.com.adtec.security.tokenExpiry
        if (tokenExpiry > 0) {
            def expiryDate = Calendar.instance
            expiryDate.add(Calendar.SECOND, -tokenExpiry)
            def expiredTokens = RestToken.where { dateCreated <= expiryDate.time }.list()
            if (!expiredTokens.empty) {
                log.debug("Deleting " + expiredTokens.size() + " tokens...");
                expiredTokens.each { if (!it.isValid) it.delete() }
            }
        }
    }

    List<String> generateDownloadToken(List<FileData> files, int amount, int accessCount) {
        List<String> tokenList = []
        amount.times { tokenList.add(generateDownloadToken(files, accessCount)) }
        return tokenList
    }

    String generateDownloadToken(List<FileData> files, int accessCount) {
        String token = generateToken(RepoService.Users.REPO_READ)
        createDownloadRestrictions(token, files, accessCount)
        return token
    }

    List<String> generateUploadToken(int fileCount) {
        String tokenValue = generateToken(RepoService.Users.REPO_UPLOAD)
        createUploadRestriction(tokenValue, fileCount)
        return [tokenValue]
    }

    def createDownloadRestriction(String token, FileData fileData, int accessCount) {
        RestToken restToken = RestToken.findByToken(token)
        if (restToken) new DownloadTokenRestriction(token: restToken, fileData: fileData, numberOfAccess: accessCount).save(flush: true)
    }

    List<String> generateMessageToken(Message message, int amount, int accessCount, int responseCount) {
        List<String> tokenList = []
        amount.times { tokenList.add(generateMessageToken(message, accessCount, responseCount))  }
        return tokenList
    }

    String generateMessageToken(Message message, int accessCount, int responseCount) {
        String token = generateToken(MessagingService.Users.MESSAGING_USER)
        createMessageRestriction(message, token, accessCount, responseCount)
        return token
    }

    private String generateToken(User user) {
        // TODO: Find a way to re authenticate without hard coding
        springSecurityService.reauthenticate(user?.username, "admin:)")
        String token = tokenGenerator.generateToken()
        tokenStorageService.storeToken(token, springSecurityService.principal)
        springSecurityService.reauthenticate("admin", "admin:)")
        return token
    }

    private createDownloadRestrictions(String token, List<FileData> files, int accessCount) {
        files.each { createDownloadRestriction(token, it, accessCount) }
    }

    private createUploadRestriction(String token, int fileCount) {
        RestToken restToken = RestToken.findByToken(token)
        if (restToken) new UploadTokenRestriction(token: restToken, numberOfFiles: fileCount).save(flush: true)
    }

    private createMessageRestriction(Message message, String token, int accessCount, int responseCount) {
        RestToken restToken = RestToken.findByToken(token)
        if (restToken) new MessageTokenRestriction(token: restToken, message: message, numberOfAccess: accessCount, responseCount: responseCount).save(flush: true)
    }
}
