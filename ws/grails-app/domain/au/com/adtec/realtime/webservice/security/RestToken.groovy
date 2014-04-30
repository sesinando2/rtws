package au.com.adtec.realtime.webservice.security

import au.com.adtec.realtime.webservice.repo.FileDataLog

class RestToken {

    String login
    String token
    Date dateCreated

    def grailsApplication

    static constraints = {}

    static mapping = {
        autoTimestamp true
    }

    static transients = ['isValid', 'isExpired', 'isAllowed']

    boolean getIsValid() {
        return !isExpired && isAllowed
    }

    boolean getIsExpired() {
        int tokenTimeToLive = grailsApplication.config.au.com.adtec.security.tokenExpiry
        if (tokenTimeToLive == 0) return false
        def cal = Calendar.instance
        cal.setTime(dateCreated)
        cal.add(Calendar.SECOND, tokenTimeToLive)
        def now = Calendar.instance
        return now.time < cal.time
    }

    boolean getIsAllowed() {
        def restrictions = TokenRestriction.findAllByToken(this)
        return restrictions.find { !it.isRestricted }
    }

    boolean isAllowedForFile(int fileId) {
        def restriction = DownloadTokenRestriction.where { token == this && fileData.id == fileId }.find()
        return !restriction?.isRestricted
    }

    boolean isAllowedForFileCount(int fileCount) {
        def restriction = UploadTokeRestriction.findByToken(this)
        if (restriction.numberOfFiles == 0) return true
        def logs = FileDataLog.findAllByToken(this)
        return (fileCount + logs.size()) <= restriction?.numberOfFiles
    }

    def beforeDelete() {
        FileDataLog.where { token == this }.deleteAll()
        TokenRestriction.where { token == this }.deleteAll()
    }
}
