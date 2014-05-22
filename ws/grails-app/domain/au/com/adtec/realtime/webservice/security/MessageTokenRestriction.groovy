package au.com.adtec.realtime.webservice.security

import au.com.adtec.realtime.webservice.messaging.Message
import au.com.adtec.realtime.webservice.messaging.MessageLog
import au.com.adtec.realtime.webservice.messaging.MessageLogAction

class MessageTokenRestriction extends TokenRestriction {

    Message message
    int numberOfAccess

    static constraints = {
        numberOfAccess min: 0, max: 1024
    }

    static transients = ['isRestricted']

    boolean getIsRestricted() {
        if (numberOfAccess == 0) return false
        def logs = MessageLog.findAllByTokenAndAction(token, MessageLogAction.READ)
        return numberOfAccess <= logs.size()
    }
}
