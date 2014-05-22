package au.com.adtec.realtime.webservice.messaging

class CannedMessageResponse {

    int messageResponsesId
    String value

    static belongsTo = [CannedMessage]

    static constraints = {}
}
