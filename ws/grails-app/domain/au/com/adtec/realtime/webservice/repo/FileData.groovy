package au.com.adtec.realtime.webservice.repo

import au.com.adtec.realtime.webservice.security.DownloadTokenRestriction

class FileData {

    String filename
    byte[] data
    String contentType
    Integer incidentId

    RepoService repoService

    static constraints = {
        filename blank: false, nullable: false
        data nullable: false, maxSize: 1024 * 1024
        contentType blank: true, nullable: true
        incidentId nullable: true
    }

    static transients = ['repoService', 'fileType', 'isImage', 'isVideo' ,'isAudio']

    static hasMany = [restrictions: DownloadTokenRestriction]

    static mappedBy = [restrictions: "fileData"]

    FileType getFileType() {
        repoService.getFileType(contentType)
    }

    boolean getIsImage() {
        fileType == FileType.IMAGE
    }

    boolean getIsVideo() {
        fileType == FileType.VIDEO
    }

    boolean getIsAudio() {
        fileType == FileType.AUDIO
    }


    def beforeDelete() {
        FileDataLog.where { fileData == this }.deleteAll()
    }
}

enum FileType { IMAGE, VIDEO, AUDIO, OTHER }
