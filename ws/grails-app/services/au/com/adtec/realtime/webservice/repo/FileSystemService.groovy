package au.com.adtec.realtime.webservice.repo

import grails.transaction.Transactional
import org.springframework.web.multipart.commons.CommonsMultipartFile

@Transactional
class FileSystemService {

    FileData createFile(CommonsMultipartFile file) {
        FileData fileData = new FileData()
        fileData.filename = file.originalFilename
        fileData.data = file.bytes
        fileData.contentType = file.contentType
        fileData.save()
    }
}
