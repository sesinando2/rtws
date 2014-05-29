package au.com.adtec.realtime.webservice.messaging

import au.com.adtec.realtime.webservice.AbstractService
import au.com.adtec.realtime.webservice.MqttService
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

    static final int MESSAGE_PROGRESS = 21;
    static final int CANNED_RESPONSE_EVENT = 279;
    //endregion

    def initializeRoles() {
        Roles.MESSAGING_ADMIN = createRole(ROLE_MESSAGING_ADMIN)
        Roles.MESSAGING_USER = createRole(ROLE_MESSAGING_USER)
    }

    def initializeUsers() {
        Users.MESSAGING_ADMIN = createUser(USER_MESSAGING_ADMIN, "admin:)", Roles.MESSAGING_ADMIN)
        Users.MESSAGING_USER = createUser(USER_MESSAGING_USER, "admin:)", Roles.MESSAGING_USER)
    }

    List<String> createCannedMessage(CannedMessage message, int tokenCount, int accessCount, int responseCount, JSONObject response) {
        message.responses = []
        for (String key : response.keySet())
            message.responses.add(new CannedMessageResponse(messageResponsesId: key, value: response.get(key)))
        message.save()
        return tokenService.generateMessageToken(message, tokenCount, accessCount, responseCount)
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

    def respondToCannedMessage(CannedMessage message, RestToken restToken, int fromMemberId, int cannedResponse) {
        if (restToken) {
            createMessageLog(message, restToken, MessageLogAction.RESPOND)
            restToken.delete()
            mqttService.publish("uas", "$MESSAGE_PROGRESS~$val~0~$fromMemberId~$message.incidentId~$message.instanceId~~$CANNED_RESPONSE_EVENT~$cannedResponse"); val++
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
                println "Restrictions: $restrictions"
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
    }
    //endregion
}
