package au.com.adtec.realtime.webservice

import grails.plugin.springsecurity.SpringSecurityUtils

class HomeController {

    def index() {
        if (SpringSecurityUtils.ifAnyGranted("ROLE_ADMIN, ROLE_REPO_ADMIN")) {
            render(view: 'admin')
        } else {
            render(view: 'user')
        }
    }
}
