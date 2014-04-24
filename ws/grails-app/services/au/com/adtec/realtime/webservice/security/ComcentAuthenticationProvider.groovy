package au.com.adtec.realtime.webservice.security

import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.core.Authentication

class ComcentAuthenticationProvider extends DaoAuthenticationProvider  {

    @Override
    Authentication authenticate(Authentication authentication) {

    }
}
