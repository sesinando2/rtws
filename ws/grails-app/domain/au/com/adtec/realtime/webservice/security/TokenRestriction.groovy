package au.com.adtec.realtime.webservice.security

class TokenRestriction {

    static belongsTo = [ token:RestToken ]

    static transients = ['isRestricted']

    boolean getIsRestricted() { false }
}
