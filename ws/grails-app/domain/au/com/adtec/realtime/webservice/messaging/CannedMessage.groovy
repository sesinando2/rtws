package au.com.adtec.realtime.webservice.messaging

class CannedMessage extends Message {

    int responseTypeId

    static hasMany = [responses: CannedMessageResponse]

    static constraints = {}

    public String toString() {
        return "[$id]\tCanned Message"
    }
}
