package au.com.adtec.realtime.webservice.messaging

import au.com.adtec.realtime.webservice.AbstractService
import au.com.adtec.realtime.webservice.MqttService
import au.com.adtec.realtime.webservice.repo.RepoService
import au.com.adtec.realtime.webservice.security.token.restriction.MessageTokenRestriction
import au.com.adtec.realtime.webservice.security.token.RestToken
import au.com.adtec.realtime.webservice.security.Role
import au.com.adtec.realtime.webservice.security.TokenService
import au.com.adtec.realtime.webservice.security.User
import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.json.JSONObject

@Transactional
class MessagingService extends AbstractService {

    TokenService tokenService
    MqttService mqttService

    private int val = 1;

    //region Constants
    static final String ROLE_MESSAGING_ADMIN = "ROLE_MESSAGING_ADMIN"
    static final String ROLE_MESSAGING_USER = "ROLE_MESSAGING_USER"

    static final String USER_MESSAGING_ADMIN = "messaging_admin"
    static final String USER_MESSAGING_USER = "messaging_user"
    static final String USER_MESSAGING_REPO_READ = "messaging_repo_read_user"

    static final int MESSAGE_PROGRESS = 21
    static final int CANNED_RESPONSE_EVENT = 279
    static final int MESSAGE_READ_EVENT = 276
    //endregion

    def initializeRoles() {
        Roles.MESSAGING_ADMIN = createRole(ROLE_MESSAGING_ADMIN)
        Roles.MESSAGING_USER = createRole(ROLE_MESSAGING_USER)
    }

    def initializeUsers() {
        Users.MESSAGING_ADMIN = createUser(USER_MESSAGING_ADMIN, "admin:)", Roles.MESSAGING_ADMIN)
        Users.MESSAGING_USER = createUser(USER_MESSAGING_USER, "admin:)", Roles.MESSAGING_USER)
        Users.MESSAGING_REPO_READ_USER = RepoService.Users.MESSAGING_REPO_READ_USER = createUser(USER_MESSAGING_REPO_READ, "admin:)", Roles.MESSAGING_USER, RepoService.Roles.REPO_READ)
    }

    Map<Integer, String> createMessage(Message message, String memberIdCsv, String fileIdCsv, int downloadCount, int readCount, int responseCount) {
        message.save()
        def membersIdList = memberIdCsv.split(",").collect { it.toInteger() }
        def fileIdList = fileIdCsv.split(",").collect { if (!it?.empty && it.number) { it.toLong() } }
        def memberToken = tokenService.generateMemberMessageTokensWithFileAccess(message, fileIdList, membersIdList, readCount, responseCount, downloadCount)
        return memberToken
    }

    Map<Integer, String> createCannedMessage(CannedMessage message, String memberIdCsv, int accessCount, int responseCount, JSONObject response) {
        message.responses = []
        for (String key : response.keySet()) {
            message.responses.add(new CannedMessageResponse(messageResponsesId: key, value: response.get(key)))
        }
        message.save()
        def membersId = memberIdCsv?.split(",").collect { if (it?.number) it.toInteger() } ?: []
        return tokenService.generateMessageToken(message, membersId, accessCount, responseCount)
    }

    CannedMessage getCannedMessage(int id, RestToken restToken) {
        CannedMessage message = null
        if (restToken) {
            message = getCannedMessageFromToken(restToken, id)
            createMessageLog(message, restToken, MessageLogAction.READ)
        } else if (isAdmin) {
            message = CannedMessage.get(id)
        }
        return message
    }

    def respondToCannedMessage(CannedMessage message, RestToken restToken, int cannedResponse) {
        if (restToken) {
            def memberToken = tokenService.getMemberToken(restToken)
            createMessageLog(message, restToken, MessageLogAction.RESPOND)
            mqttService.publish("uas", "$MESSAGE_PROGRESS~$val~0~$memberToken.memberId~$message.incidentId~$message.instanceId~~$CANNED_RESPONSE_EVENT~$cannedResponse"); val++
        }
    }

    def processMessageProgress(RestToken restToken, String action) {
        if (restToken) {
            def message = MessageTokenRestriction.findByToken(restToken)?.message
            def memberToken = tokenService.getMemberToken(restToken)
            if (message && memberToken) {
                if (action == "MESSAGE_PROGRESS_READ") {
                    mqttService.publish("uas", "$MESSAGE_PROGRESS~${val}~0~$memberToken.memberId~$message.incidentId~$message.instanceId~~$MESSAGE_READ_EVENT~0"); val++
                }
            }
        }
    }

    public createMessageLog(Message message, RestToken restToken, MessageLogAction action) {
        new MessageLog(message: message, token: restToken, tokenValue: restToken.token, action: action).save()
    }

    boolean getIsAdmin() {
        def authorities = currentUser?.authorities?.collect { it.authority }
        return authorities && (authorities?.contains('ROLE_ADMIN') || authorities?.contains('ROLE_MESSAGING_ADMIN'))
    }

    private CannedMessage getCannedMessageFromToken(RestToken restToken, int id) {
        if (restToken) {
            if (restToken.isValid) {
                def restrictions = MessageTokenRestriction.where { token == restToken && message.id == id }.list()
                if (!restrictions.empty) return restrictions.first().message
            }
        }
        return null
    }

    //region Roles & Users
    static class Roles {
        static Role MESSAGING_ADMIN
        static Role MESSAGING_USER
    }

    static class Users {
        static User MESSAGING_ADMIN
        static User MESSAGING_USER
        static User MESSAGING_REPO_READ_USER
    }
    //endregion
}
