package au.com.adtec.realtime.webservice.security

import au.com.adtec.realtime.webservice.messaging.Message
import au.com.adtec.realtime.webservice.messaging.MessagingService
import au.com.adtec.realtime.webservice.repo.FileData
import au.com.adtec.realtime.webservice.repo.RepoService
import au.com.adtec.realtime.webservice.security.token.MemberToken
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
        int tokenExpiry = grailsApplication.config.au.com.adtec.security.tokenExpiry
        if (tokenExpiry > 0) {
            def expiryDate = Calendar.instance
            expiryDate.add(Calendar.SECOND, -tokenExpiry)
            RestToken.where { dateCreated <= expiryDate.time }.list().each {
                it.delete()
                log.debug("Token: $it deleted.")
            }
        }
    }

    List<String> generateUploadToken(int fileCount) {
        String tokenValue = generateToken(RepoService.Users.REPO_UPLOAD)
        createUploadRestriction(tokenValue, fileCount)
        return [tokenValue]
    }

    private createUploadRestriction(String token, int fileCount) {
        RestToken restToken = RestToken.findByToken(token)
        if (restToken) new UploadTokenRestriction(token: restToken, numberOfFiles: fileCount).save(flush: true)
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

    void createDownloadRestrictions(String token, List<FileData> files, int accessCount) {
        files.each { createDownloadRestriction(token, it, accessCount) }
    }

    void createDownloadRestriction(String token, FileData fileData, int accessCount) {
        RestToken restToken = RestToken.findByToken(token)
        if (restToken) new DownloadTokenRestriction(token: restToken, fileData: fileData, numberOfAccess: accessCount).save(flush: true)
    }

    Map<Integer, String> generateMessageToken(Message message, List<Integer> membersId, int accessCount, int responseCount) {
        def tokenMap = [:]
        membersId.each { tokenMap.put(it, generateMessageToken(message, it, accessCount, responseCount)) }
        return tokenMap
    }

    String generateMessageToken(Message message, int memberId, int accessCount, int responseCount) {
        String token = generateToken(MessagingService.Users.MESSAGING_USER)
        updateMemberToken(memberId, token)
        createMessageRestriction(message, token, accessCount, responseCount)
        return token
    }

    private createMessageRestriction(Message message, String token, int readCount, int responseCount) {
        RestToken restToken = RestToken.findByToken(token)
        if (restToken) new MessageTokenRestriction(token: restToken, message: message, numberOfAccess: readCount, responseCount: responseCount).save(flush: true)
    }

    Map<Integer, String> generateMemberMessageTokensWithFileAccess(Message message, List<Long> fileIds, List<Integer> memberIds,int readCount, int responseCount, int downloadCount) {
        Map<Integer, String> memberToken = [:]
        def files = FileData.where { id in fileIds }.list()
        memberIds.each { memberToken.put(it, generateMemberMessageTokenWithFileAccess(message, files, it, readCount, responseCount, downloadCount))}
        return memberToken
    }

    private String generateMessageTokenWithFileAccess(Message message, List<FileData> files, int accessCount, int responseCount, int downloadCount) {
        String token = generateToken(MessagingService.Users.MESSAGING_REPO_READ_USER)
        createMessageRestriction(message, token, accessCount, responseCount)
        createDownloadRestrictions(token, files, downloadCount)
        return token
    }

    private String generateMemberMessageTokenWithFileAccess(Message message, List<FileData> files, int memberId, int readCount, int responseCount, int downloadCount) {
        RestToken restToken = RestToken.findByToken(generateMessageTokenWithFileAccess(message, files, readCount, responseCount, downloadCount))
        updateMemberToken(memberId, restToken)
        return restToken.token
    }

    private void updateMemberToken(int memberId, String token) {
        RestToken restToken = RestToken.findByToken(token)
        updateMemberToken(memberId, restToken)
    }

    private void updateMemberToken(int memberId, RestToken token) {
        MemberToken memberToken = MemberToken.findByMemberId(memberId) ?: new MemberToken(memberId: memberId, tokens: []).save()
        memberToken.tokens.add(token)
        memberToken.save(flush: true)
    }

    private String generateToken(User user) {
        // TODO: Find a way to re authenticate without hard coding
        springSecurityService.reauthenticate(user?.username, "admin:)")
        String token = tokenGenerator.generateToken()
        tokenStorageService.storeToken(token, springSecurityService.principal)
        springSecurityService.reauthenticate("admin", "admin:)")
        return token
    }

    MemberToken getMemberToken(RestToken token) {
        return MemberToken.find("from MemberToken where :token in elements(tokens)", [token: token])
    }
}
