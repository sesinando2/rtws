package au.com.adtec.realtime.webservice.security.token

class MemberToken {

    int memberId
    static hasMany = [tokens: RestToken]
    static constraints = { memberId unique: true }
}
