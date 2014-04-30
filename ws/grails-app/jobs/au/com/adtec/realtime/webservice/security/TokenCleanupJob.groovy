package au.com.adtec.realtime.webservice.security

class TokenCleanupJob {

    TokenService tokenService

    static triggers = {
      simple repeatInterval: 5000l
    }

    def execute() {
        log.debug("Running token cleanup...")
        tokenService.cleanUpToken()
    }
}
