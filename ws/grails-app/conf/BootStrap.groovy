import au.com.adtec.realtime.webservice.messaging.MessagingService
import au.com.adtec.realtime.webservice.repo.RepoService
import au.com.adtec.realtime.webservice.security.Role
import au.com.adtec.realtime.webservice.security.User
import au.com.adtec.realtime.webservice.security.UserRole

class BootStrap {

    RepoService repoService
    MessagingService messagingService

    def init = { servletContext ->
        Role role = Role.findByAuthority('ROLE_ADMIN') ?: new Role(authority: 'ROLE_ADMIN').save()
        User user = User.findByUsername("admin") ?: new User(username: 'admin', password: 'admin:)', enabled: true).save()
        if (!user.authorities.contains(role)) UserRole.create(user, role, true)

        repoService.initializeRoles()
        repoService.initializeUsers()

        messagingService.initializeRoles()
        messagingService.initializeUsers()
    }
    def destroy = {
    }
}
