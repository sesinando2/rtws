package au.com.adtec.realtime.webservice.security

import au.com.adtec.realtime.webservice.repo.FileData
import au.com.adtec.realtime.webservice.repo.RepoService
import com.odobo.grails.plugin.springsecurity.rest.token.generation.TokenGenerator
import com.odobo.grails.plugin.springsecurity.rest.token.storage.TokenStorageService
import grails.plugin.springsecurity.SpringSecurityService
import grails.transaction.Transactional
import org.apache.commons.logging.Log

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
                expiredTokens.each {
                    if (!it.isValid) it.delete()
                }
            }
        }
    }

    List<String> generateDownloadToken(List<FileData> files, int amount, int accessCount) {
        List<String> tokenList = []
        amount.times { tokenList.add(generateDownloadToken(files, accessCount)) }
        return tokenList
    }

    String generateDownloadToken(List<FileData> files, int accessCount) {
        String tokenValue = generateToken(RepoService.Users.REPO_READ)
        createDownloadTokenRestrictions(tokenValue, files, accessCount)
        return tokenValue
    }

    List<String> generateUploadToken(int fileCount) {
        String tokenValue = generateToken(RepoService.Users.REPO_UPLOAD)
        createUploadRestriction(tokenValue, fileCount)
        return [tokenValue]
    }

    def createDownloadTokenRestriction(String token, FileData fileData, int accessCount) {
        RestToken restToken = RestToken.findByToken(token)
        if (restToken) new DownloadTokenRestriction(token: restToken, fileData: fileData, numberOfAccess: accessCount).save(flush: true)
    }

    private String generateToken(User user) {
        springSecurityService.reauthenticate(user?.username, "admin:)")
        String token = tokenGenerator.generateToken()
        tokenStorageService.storeToken(token, springSecurityService.principal)
        return token
    }

    private createDownloadTokenRestrictions(String token, List<FileData> files, int accessCount) {
        files.each {
            createDownloadTokenRestriction(token, it, accessCount)
        }
    }

    private createUploadRestriction(String token, int fileCount) {
        RestToken restToken = RestToken.findByToken(token)
        if (restToken) new UploadTokeRestriction(token: restToken, numberOfFiles: fileCount).save(flush: true)
    }
}
