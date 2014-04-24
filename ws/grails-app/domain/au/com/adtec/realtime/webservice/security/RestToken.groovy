package au.com.adtec.realtime.webservice.security

class RestToken {

    String login
    String token
    Date dateCreated

    static constraints = {

    }

    static mapping = {
        autoTimestamp true
    }
}
