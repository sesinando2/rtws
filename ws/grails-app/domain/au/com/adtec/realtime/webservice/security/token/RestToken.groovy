package au.com.adtec.realtime.webservice.security.token

import au.com.adtec.realtime.webservice.repo.FileDataLog
import au.com.adtec.realtime.webservice.security.token.restriction.DownloadTokenRestriction
import au.com.adtec.realtime.webservice.security.token.restriction.MessageTokenRestriction
import au.com.adtec.realtime.webservice.security.token.restriction.TokenRestriction
import au.com.adtec.realtime.webservice.security.token.restriction.UploadTokenRestriction

class RestToken {

    String login
    String token
    Date dateCreated

    def grailsApplication
    def sessionFactory

    static constraints = { }

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
        def restriction = UploadTokenRestriction.findByToken(this)
        if (restriction.numberOfFiles == 0) return true
        def logs = FileDataLog.findAllByToken(this)
        return (fileCount + logs.size()) <= restriction?.numberOfFiles
    }

    boolean isAllowedForMessage(long... messageIds) {
        def restrictions = MessageTokenRestriction.where { token == this && message.id in messageIds }.findAll()
        return !restrictions.find { it.isRestricted }
    }

    def beforeDelete() {
        unlinkRelatedLogs()
        unlinkMembers()
        TokenRestriction.where { token == this }.deleteAll()
    }

    private void unlinkRelatedLogs() {
        final String sql = "update abstract_log set token_id = null, token_value = :tokenValue where token_id = :tokenId"
        final session = sessionFactory.currentSession
        final sqlQuery = session.createSQLQuery(sql)
        sqlQuery.with {
            setLong('tokenId', id)
            setString('tokenValue', token)
            executeUpdate()
        }
    }

    private void unlinkMembers() {
        final String sql = "delete from member_token_rest_token where rest_token_id = :tokenId"
        final session = sessionFactory.currentSession
        session.createSQLQuery(sql).with {
            setLong('tokenId', id)
            executeUpdate()
        }
    }
}
