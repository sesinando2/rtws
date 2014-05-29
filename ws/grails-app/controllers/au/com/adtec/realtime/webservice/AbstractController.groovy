package au.com.adtec.realtime.webservice

abstract class AbstractController {

    protected getToken() {
        request.getHeader('X-Auth-Token') ?: request.getParameter('token')
    }
}
