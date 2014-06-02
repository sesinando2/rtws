package au.com.adtec.realtime.webservice.security.token.restriction

import au.com.adtec.realtime.webservice.repo.FileData
import au.com.adtec.realtime.webservice.repo.FileDataLog

class DownloadTokenRestriction extends TokenRestriction {

    FileData fileData
    int numberOfAccess

    static constraints = {
        numberOfAccess min: 0, max: 1024
    }

    static transients = ['isRestricted']

    boolean getIsRestricted() {
        if (numberOfAccess == 0) return false
        def logs = FileDataLog.findAllByTokenAndFileData(token, fileData)
        return logs.size() >= numberOfAccess
    }
}
