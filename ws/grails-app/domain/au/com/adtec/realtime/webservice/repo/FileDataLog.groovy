package au.com.adtec.realtime.webservice.repo

import au.com.adtec.realtime.webservice.security.RestToken

class FileDataLog {

    RestToken token
    String tokenValue
    FileData fileData
    FileDataAction action
    Date dateCreated

    static constraints = {
        token nullable: true
    }
}

enum FileDataAction { UPLOAD, DOWNLOAD }
