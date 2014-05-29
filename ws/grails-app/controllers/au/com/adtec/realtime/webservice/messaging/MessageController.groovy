package au.com.adtec.realtime.webservice.messaging

import au.com.adtec.realtime.webservice.AbstractController
import au.com.adtec.realtime.webservice.security.token.RestToken
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

class MessageController extends AbstractController {

    MessagingService messagingService

    @Secured(["ROLE_ADMIN", "ROLE_MESSAGING_ADMIN", "ROLE_MESSAGING_USER"])
    def list() {

    }

    @Secured(["ROLE_ADMIN", "ROLE_MESSAGING_ADMIN"])
    def addCanned(CannedMessage cannedMessage, Integer tokenCount, Integer accessCount, Integer responseCount, String response) {
        tokenCount = tokenCount ?: 1
        accessCount = accessCount ?: 1
        responseCount = responseCount ?: 1
        def responseMap = [:]
        if (cannedMessage.validate()) {
            responseMap.tokens = messagingService.createCannedMessage(cannedMessage, tokenCount, accessCount, responseCount, JSON.parse(response))
            responseMap.success = true
            responseMap.id = cannedMessage?.id
        } else {
            responseMap.success = false
            responseMap.errors = cannedMessage?.errors
        }
        render(responseMap as JSON)
    }

    @Secured(["permitAll"])
    def cannedResponse(int id, int fromMemberId, int cannedResponse, String token) {
        RestToken restToken = RestToken.findByToken(token)
        CannedMessage cannedMessage = messagingService.getCannedMessage(id, restToken)
        if ((restToken || messagingService?.isAdmin) && cannedMessage) {
            messagingService.respondToCannedMessage(cannedMessage, restToken, fromMemberId, cannedResponse)
            CannedMessageResponse selectedResponse = cannedMessage.responses.find { it.messageResponsesId == cannedResponse }
            def jsonMessage = JSON.parse(cannedMessage.messageContent)
            render(view: "/message/cannedResponse", model: [message: jsonMessage?.txt, responseText: selectedResponse?.value])
            return
        }
        render(view: "/message/cannedResponse", model: [hasResponded: true])
    }
}
