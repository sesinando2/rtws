package au.com.adtec.realtime.webservice.repo

class ImageFileData extends FileData {

    Date timestamp
    String location

    static constraints = {
        timestamp blank: true, nullable: true
        location blank: true, nullable: true
    }

    String toString() {
        "[$id]\tImage : $filename"
    }
}
