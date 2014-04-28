package au.com.adtec.realtime.webservice.security

import au.com.adtec.realtime.webservice.repo.FileData
import au.com.adtec.realtime.webservice.repo.RepoService
import com.odobo.grails.plugin.springsecurity.rest.token.generation.TokenGenerator
import com.odobo.grails.plugin.springsecurity.rest.token.storage.TokenStorageService
import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityService

class TokenController {

    TokenGenerator tokenGenerator
    TokenStorageService tokenStorageService
    SpringSecurityService springSecurityService

    def index() {
        redirect(action: 'list')
    }

    def list() {
        def tokenList = RestToken.list();
        render(tokenList as JSON)
    }

    def request() {

        if (request.localName != "localhost" || request.remoteAddr != "127.0.0.1") {
            render(status: 401)
            return
        }

        def json = request?.JSON

        def files = resourceFiles
        if (!files || files.empty) {
            render(status: 404, text: "Cannot find resources with id: $json.id")
            return
        }

        User user = privilegedUser
        def amount = json?.amount as int ?: 1
        def tokenList = []

        if (user) {
            for (int i : 1..amount) {
                String token = generateToken(user)
                tokenList.add(token)
                createTokenRestrictions(token, files)
            }
        }
        springSecurityService.reauthenticate("admin", "admin:)")
        render(tokenList as JSON)
    }

    //region Helper Methods
    private User getPrivilegedUser() {
        switch (request?.JSON?.authority) {
            case RepoService.ROLE_REPO_READ: return RepoService.Users.REPO_READ
            case RepoService.ROLE_REPO_UPLOAD: return RepoService.Users.REPO_UPLOAD
        }
    }

    private List<FileData> getResourceFiles() {
        def id = request?.JSON?.id
        List<FileData> fileList = []

        if (id instanceof Integer) {
            FileData fileData = FileData.get(id)
            if (fileData) fileList.add(fileData)
        } else if (id instanceof List<Long>) {
            def resourceIDs = id.collect { it as long }
            fileList.addAll(FileData.where { id in resourceIDs }.list())
        }
        fileList
    }

    private String generateToken(User user) {
        springSecurityService.reauthenticate(user?.username, "admin:)")
        String token = tokenGenerator.generateToken()
        tokenStorageService.storeToken(token, springSecurityService.principal)
        token
    }

    private createTokenRestrictions(String token, List<FileData> files) {
        for (FileData file : files) {
            createTokenRestrictions(token, file)
        }
    }

    private createTokenRestrictions(String token, FileData fileData) {
        RestToken restToken = RestToken.findByToken(token)
        def numberOfAccess = params?.access > 0 ? params?.access : 0
        new TokenRestriction(token: restToken, fileData: fileData, numberOfAccess: numberOfAccess).save(flush: true)
    }
    //endregion
}
