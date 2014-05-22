package au.com.adtec.realtime.webservice.messaging

import au.com.adtec.realtime.webservice.security.MessageTokenRestriction

class Message {

    int fromAgentId
    int fromMemberId
    int incidentId
    int instanceId
    int messageType
    String messageContent
    Date dateCreated
    Date lastUpdated

    static constraints = {
        messageContent blank: true
    }

    static hasMany = [restrictions: MessageTokenRestriction]

    static mappedBy = [restrictions: "message"]

    boolean hasValidToken() { MessageTokenRestriction.findAllByMessage(this).find { !it.isRestricted } }
}
