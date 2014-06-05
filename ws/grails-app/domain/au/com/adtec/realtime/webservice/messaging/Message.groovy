package au.com.adtec.realtime.webservice.messaging

import au.com.adtec.realtime.webservice.security.token.restriction.MessageTokenRestriction
import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.JSONObject

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

    static transients = ['messageText']

    boolean hasValidToken() { MessageTokenRestriction.findAllByMessage(this).find { !it.isRestricted } }

    String toString() {
        return "[$id]\tMessage"
    }

    String getMessageTypeText() {
        switch (messageType) {
            case MessageType.TEXT: return "Text"
            case MessageType.PHOTO: return "Photo"
            case MessageType.VIDEO: return "Video"
            case MessageType.VOICE: return "Voice"
            case MessageType.CANNED: return "Canned"
            case MessageType.LOCATION: return "Location"
            default: return "Mixed"
        }
    }
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
