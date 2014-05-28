package au.com.adtec.realtime.webservice.messaging

import au.com.adtec.realtime.webservice.security.RestToken
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

@Secured(["ROLE_ADMIN", "ROLE_MESSAGING_ADMIN"])
class MessageController {

    MessagingService messagingService

    def addCanned(CannedMessage cannedMessage, Integer tokenCount, Integer accessCount, String response) {
        tokenCount = tokenCount ?: 1
        accessCount = accessCount ?: 1
        def responseMap = [:]
        if (cannedMessage.validate()) {
            responseMap.tokens = messagingService.createCannedMessage(cannedMessage, tokenCount, accessCount, JSON.parse(response))
            responseMap.success = true
            responseMap.id = cannedMessage?.id
        } else {
            responseMap.success = false
            responseMap.errors = cannedMessage?.errors
        }
        render(responseMap as JSON)
    }

    @Secured(["ROLE_ADMIN", "ROLE_MESSAGING_ADMIN", "ROLE_MESSAGING_USER"])
    def cannedResponse(int id, int fromMemberId, int cannedResponse) {
        RestToken restToken = RestToken.findByToken(token)
        CannedMessage cannedMessage = messagingService.getCannedMessage(id, restToken)
        if ((restToken || messagingService.isAdmin) && cannedMessage) {
            messagingService.respondToCannedMessage(cannedMessage, restToken, fromMemberId, cannedResponse)
            CannedMessageResponse selectedResponse = cannedMessage.responses.find { it.messageResponsesId == cannedResponse }
            def jsonMessage = JSON.parse(cannedMessage.messageContent)
            render(view: "/message/cannedResponse", model: [message: jsonMessage?.txt, responseText: selectedResponse?.value])
        } else {
            render(status: 404, text: "Canned Message with id: $id is not found")
        }
    }

    private getToken() {
        request.getHeader('X-Auth-Token') ?: request.getParameter('token')
    }
}
