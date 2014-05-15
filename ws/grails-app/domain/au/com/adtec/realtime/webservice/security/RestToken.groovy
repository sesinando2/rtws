package au.com.adtec.realtime.webservice.security

import au.com.adtec.realtime.webservice.repo.FileDataLog

class RestToken {

    String login
    String token
    Date dateCreated

    def grailsApplication
    def sessionFactory

    static constraints = {}

    static mapping = {
        autoTimestamp true
    }

    static transients = ['isValid', 'isExpired', 'isAllowed', 'grailsApplication', 'sessionFactory']

    boolean getIsValid() {
        return !isExpired && isAllowed
    }

    boolean getIsExpired() {
        int tokenTimeToLive = grailsApplication.config.au.com.adtec.security.tokenExpiry
        if (tokenTimeToLive == 0) return false
        def cal = Calendar.instance
        cal.add(Calendar.SECOND, -tokenTimeToLive)
        return dateCreated <= cal.time
    }

    boolean getIsAllowed() {
        def restrictions = TokenRestriction.findAllByToken(this)
        return restrictions.find { !it.isRestricted }
    }

    boolean isAllowedForFile(int... fileIds) {
        for (int id : fileIds) {
            def restriction = DownloadTokenRestriction.where { token == this && fileData.id == id }.find()
            if (!restriction || restriction.isRestricted) return false
        }
        return true
    }

    boolean isAllowedForFileCount(int fileCount) {
        def restriction = UploadTokeRestriction.findByToken(this)
        if (restriction.numberOfFiles == 0) return true
        def logs = FileDataLog.findAllByToken(this)
        return (fileCount + logs.size()) <= restriction?.numberOfFiles
    }

    def beforeDelete() {
        final String query = "update file_data_log set token_id=null where token_id=:tokenId"
        final session  = sessionFactory.currentSession
        final sqlQuery = session.createSQLQuery(query)
        sqlQuery.with {
            setLong('tokenId', id)
            executeUpdate()
        }
        TokenRestriction.where { token == this }.deleteAll()
    }
}
