package au.com.adtec.realtime.webservice.repo

import au.com.adtec.realtime.webservice.AbstractLog

class FileDataLog extends AbstractLog {

    FileData fileData
    FileDataAction action
}

enum FileDataAction { UPLOAD, DOWNLOAD }
