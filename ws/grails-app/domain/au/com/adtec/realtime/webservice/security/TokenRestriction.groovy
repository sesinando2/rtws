package au.com.adtec.realtime.webservice.security

import au.com.adtec.realtime.webservice.repo.FileData

class TokenRestriction {

    FileData fileData
    int numberOfAccess

    static constraints = {
        fileData nullable: true
        numberOfAccess min: 0, max: 1024
    }

    static belongsTo = [token:RestToken]
}
