package au.com.adtec.realtime.websocket

import au.com.adtec.realtime.webservice.communication.RealTimeService
import grails.util.Holders
import org.atmosphere.cpr.*
import org.atmosphere.websocket.WebSocketEventListenerAdapter

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.atmosphere.cpr.AtmosphereResource.TRANSPORT.WEBSOCKET

class DefaultMeteorHandler extends HttpServlet {

    static final def TAG_BASE = "[DefaultMeteorHandler"

    RealTimeService realTimeService = Holders.applicationContext.getBean("realTimeService")

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        def TAG = "$TAG_BASE::doGet]\t"
        String  mapping = request?.pathInfo == null ? "/atmosphere" : "/atmosphere" + request.pathInfo

        Meteor m = Meteor.build(request)
        Broadcaster b = BroadcasterFactory.getDefault().lookup(DefaultBroadcaster.class, mapping, true)

        if (m.transport().equals(WEBSOCKET)) {
            m.addListener(new WebSocketEventListenerAdapter() {

                @Override
                void onDisconnect(AtmosphereResourceEvent event) {
                    DefaultMeteorHandler.this.log(TAG, "Disconnected from RT Daemon")
                    realTimeService.disconnect(b)
                }
            })
        } else {
            m.addListener(new AtmosphereResourceEventListenerAdapter() {
                @Override
                void onDisconnect(AtmosphereResourceEvent event) {
                    DefaultMeteorHandler.this.log(TAG, "Disconnected from RT Daemon")
                    realTimeService.disconnect(b)
                }
            })
        }

        m.setBroadcaster(b)

        realTimeService.connect(b)
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        def TAG = "$TAG_BASE::doGet]\t"
        String mapping = request?.pathInfo == null ? "/atmosphere" : "/atmosphere" + request.pathInfo

        Broadcaster b = BroadcasterFactory.getDefault().lookup(DefaultBroadcaster.class, mapping)
        def rawReqBody = request?.getReader()?.readLine()?.trim();

        /* Debug Log */
        log(TAG, "mapping: $mapping")
        log(TAG, "contentType: $request.contentType")
        log(TAG, "rawRequest: $rawReqBody")
        /* End Debug Log */

        switch (request.pathInfo) {
            case ~/^\/rtm/:
                switch (request.contentType) {
                    case "text/plain":
                        try {
                            realTimeService.push(rawReqBody, b)
                        } catch (Exception e) {
                            println(e)
                        }
                        break
                    default:
                        println("Unsupported contentType: ")
                }
                break
            default: println("Unsupported path: " + request?.pathInfo)
        }
    }

    private def log(String tag, String message) {
        println("$tag$message")
    }
}
