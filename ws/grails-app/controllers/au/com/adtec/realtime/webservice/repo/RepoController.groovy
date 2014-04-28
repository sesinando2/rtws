package au.com.adtec.realtime.webservice.repo

import grails.converters.JSON
import org.grails.plugins.imagetools.ImageTool
import org.springframework.web.multipart.MultipartHttpServletRequest
import org.springframework.web.multipart.commons.CommonsMultipartFile

class RepoController {

    static allowedMethods = [list: 'GET', upload: 'POST', download: 'GET', delete: 'DELETE']

    def repoService

    def index() { }

    def list() {
        render FileData.list().collect { it.id } as JSON
    }

    def upload() {
        def urls = []
        if (request instanceof MultipartHttpServletRequest) {
            for (def fileName : request.fileNames) {
                CommonsMultipartFile file = request.getFile(fileName);
                FileData fileData = repoService.createFile(file, params);
                urls.add(g.createLink(action: 'download', id: fileData?.id, absolute: true))
            }
        }
        render urls as JSON;
    }

    def download(int id) {
        FileData file = repoService.getFile(id, request.getHeader('X-Auth-Token'), params)
        if (file) {
            response.contentType = file.contentType
            response.outputStream << file.data
            response.outputStream.flush()
        } else {
            response.status = 404;
            render([error: "Cannot find file with id '$id'"] as JSON)
        }
    }

    def delete(int id) {
        FileData file = FileData.get(id);
        if (file) {
            def response = [success: true];
            try { file.delete() } catch (all) {
                response = [success: false, message: "An error occurred while trying to delete file with id '$id'"]
            }
            render response as JSON
        } else {
            response.status = 404;
            render([error: "Cannot find file with id '$id'"] as JSON)
        }
    }
}
