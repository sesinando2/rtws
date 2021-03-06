package au.com.adtec.realtime.webservice

import au.com.adtec.realtime.webservice.security.token.RestToken

class AbstractLog {

    RestToken token
    String tokenValue
    Date dateCreated

    static constraints = {
        token nullable: true
    }
}
