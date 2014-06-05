package au.com.adtec.realtime.webservice

import com.odobo.grails.plugin.springsecurity.rest.RestAuthenticationToken

abstract class AbstractController {

    def springSecurityService

    protected getToken() {
        request.getHeader('X-Auth-Token') ?: request.getParameter('token') ?:
                springSecurityService.authentication instanceof RestAuthenticationToken ?
                        springSecurityService.authentication?.tokenValue : null
    }
}
