package au.com.adtec.realtime.webservice.repo

class FileData {

    String filename
    byte[] data
    String contentType
    String incidentId

    RepoService repoService

    static constraints = {
        filename blank: false, nullable: false
        data nullable: false, maxSize: 1024 * 1024
        contentType blank: true, nullable: true
        incidentId blank: true, nullable: true
    }

    static transients = ['repoService', 'fileType', 'isImage', 'isVideo' ,'isAudio']

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
}

enum FileType { IMAGE, VIDEO, AUDIO, OTHER }
