package au.com.adtec.realtime.webservice.messaging

import au.com.adtec.realtime.webservice.AbstractController
import au.com.adtec.realtime.webservice.security.token.RestToken
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

class MessageController extends AbstractController {

    MessagingService messagingService

    @Secured(["ROLE_MESSAGING_USER"])
    def list() {
        respond Message.list()
    }

    @Secured(["ROLE_MESSAGING_ADMIN"])
    def addCanned(CannedMessage cannedMessage, String memberIdCsv, Integer accessCount, Integer responseCount, String response) {
        accessCount = accessCount ?: 1
        responseCount = responseCount ?: 1
        def responseMap = [:]
        if (cannedMessage.validate()) {
            responseMap.tokens = messagingService.createCannedMessage(cannedMessage, memberIdCsv, accessCount, responseCount, JSON.parse(response))
            responseMap.success = true
            responseMap.id = cannedMessage?.id
        } else {
            responseMap.success = false
            responseMap.errors = cannedMessage?.errors
        }
        render(responseMap as JSON)
    }

    @Secured(["permitAll"])
    def cannedResponse(int id, int cannedResponse, String token) {
        RestToken restToken = RestToken.findByToken(token)
        CannedMessage cannedMessage = messagingService.getCannedMessage(id, restToken)
        if ((restToken || messagingService?.isAdmin) && cannedMessage) {
            messagingService.respondToCannedMessage(cannedMessage, restToken, cannedResponse)
            CannedMessageResponse selectedResponse = cannedMessage.responses.find { it.messageResponsesId == cannedResponse }
            def jsonMessage = JSON.parse(cannedMessage.messageContent)
            render(view: "/message/cannedResponse", model: [message: jsonMessage?.txt, responseText: selectedResponse?.value])
            return
        }
        render(view: "/message/cannedResponse", model: [hasResponded: true])
    }
}
