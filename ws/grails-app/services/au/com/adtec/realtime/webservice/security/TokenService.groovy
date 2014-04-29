package au.com.adtec.realtime.webservice.security

import au.com.adtec.realtime.webservice.repo.FileData
import au.com.adtec.realtime.webservice.repo.RepoService
import com.odobo.grails.plugin.springsecurity.rest.token.generation.TokenGenerator
import com.odobo.grails.plugin.springsecurity.rest.token.storage.TokenStorageService
import grails.plugin.springsecurity.SpringSecurityService
import grails.transaction.Transactional

@Transactional
class TokenService {

    TokenGenerator tokenGenerator
    TokenStorageService tokenStorageService
    SpringSecurityService springSecurityService

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

    private createDownloadTokenRestriction(String token, FileData fileData, int accessCount) {
        RestToken restToken = RestToken.findByToken(token)
        if (restToken) new DownloadTokenRestriction(token: restToken, fileData: fileData, numberOfAccess: accessCount).save(flush: true)
    }

    private createUploadRestriction(String token, int fileCount) {
        RestToken restToken = RestToken.findByToken(token)
        if (restToken) new UploadTokeRestriction(token: restToken, numberOfFiles: fileCount).save(flush: true)
    }
}
