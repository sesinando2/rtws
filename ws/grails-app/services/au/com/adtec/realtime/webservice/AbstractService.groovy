package au.com.adtec.realtime.webservice

import au.com.adtec.realtime.webservice.security.Role
import au.com.adtec.realtime.webservice.security.User
import au.com.adtec.realtime.webservice.security.UserRole
import grails.plugin.springsecurity.SpringSecurityService
import grails.transaction.Transactional

@Transactional
abstract class AbstractService {

    SpringSecurityService springSecurityService

    Role createRole(String authority) {
        Role.findByAuthority(authority) ?: new Role(authority: authority).save()
    }

    User createUser(String username, String password, Role roles) {
        User user = User.findByUsername(username) ?: new User(username: username, password: password).save()
        for (Role role : roles)
            if (!user.authorities.contains(role))
                UserRole.create(user, role, true)
        return user
    }

    User getCurrentUser() {
        springSecurityService?.currentUser
    }
}
