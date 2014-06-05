package au.com.adtec.realtime.webservice.security.token.restriction

import au.com.adtec.realtime.webservice.messaging.Message
import au.com.adtec.realtime.webservice.messaging.MessageLog
import au.com.adtec.realtime.webservice.messaging.MessageLogAction

class MessageTokenRestriction extends TokenRestriction {

    Message message
    int numberOfAccess
    int numberOfResponse

    static constraints = {
        numberOfAccess min: 0, max: 1024
        numberOfResponse min: 0, max: 1024
    }

    static transients = ['isRestricted', 'restrictedResource']

    boolean getIsRestricted() {
        def isRestricted = false

        if (numberOfAccess != 0) {
            def readLogs = MessageLog.findAllByTokenAndAction(token, MessageLogAction.READ)
            if (readLogs.size() >= numberOfAccess) isRestricted = true
        }

        if (numberOfResponse != 0) {
            def respondLogs = MessageLog.findAllByTokenAndAction(token, MessageLogAction.RESPOND)
            if (respondLogs.size() >= numberOfResponse) isRestricted = true
        }

        return isRestricted
    }

    def getRestrictedResource() {
        return message
    }

    def getRestrictionDetails() {
        "Number of Read Access: $numberOfAccess, Number of Response: $numberOfResponse"
    }
}
