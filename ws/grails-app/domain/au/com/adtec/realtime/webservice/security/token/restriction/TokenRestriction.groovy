package au.com.adtec.realtime.webservice.security.token.restriction

import au.com.adtec.realtime.webservice.security.token.RestToken

class TokenRestriction {

    static belongsTo = [ token:RestToken ]

    static transients = ['isRestricted']

    boolean getIsRestricted() { false }
}
