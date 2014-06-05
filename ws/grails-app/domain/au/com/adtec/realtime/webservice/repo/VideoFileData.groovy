package au.com.adtec.realtime.webservice.repo

class VideoFileData extends FileData {

    byte[] thumbData
    String thumbContentType

    static constraints = {
        thumbData nullable: true, maxSize: 1024 * 1024 * 10
        thumbContentType nullable: true
    }

    String toString() {
        "[$id]\tVideo : $filename"
    }
}
