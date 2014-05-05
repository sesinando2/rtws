package au.com.adtec.realtime.webservice.repo

class VideoFileData extends FileData {

    byte[] thumbData
    String thumbExtension

    static constraints = {
        thumbData nullable: true
        thumbExtension nullable: true
    }
}
