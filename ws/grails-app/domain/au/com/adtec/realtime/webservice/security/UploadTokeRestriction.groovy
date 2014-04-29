package au.com.adtec.realtime.webservice.security

import au.com.adtec.realtime.webservice.repo.FileDataLog

class UploadTokeRestriction extends TokenRestriction {

    int numberOfFiles

    static constraints = {}

    static transients = ['isRestricted']

    boolean getIsRestricted() {
        if (numberOfFiles == 0) return false
        def logs = FileDataLog.findAllByToken(token)
        return logs.size() >= numberOfFiles
    }
}
