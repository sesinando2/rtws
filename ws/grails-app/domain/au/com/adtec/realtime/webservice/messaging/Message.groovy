package au.com.adtec.realtime.webservice.messaging

import au.com.adtec.realtime.webservice.security.token.restriction.MessageTokenRestriction

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

class MessageType {
    def static final TEXT = 1
    def static final PHOTO = 2
    def static final VIDEO = 3
    def static final VOICE = 4
    def static final CANNED = 5
    def static final LOCATION = 6
    def static final MIXED = 7
}
