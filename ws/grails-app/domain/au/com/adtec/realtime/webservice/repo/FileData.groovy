package au.com.adtec.realtime.webservice.repo

class FileData {

    String filename
    byte[] data
    String contentType
    String instanceId

    static constraints = {
        filename blank: false, nullable: false
        data nullable: false, maxSize: 1024 * 1024
        contentType blank: true, nullable: true
    }
}
