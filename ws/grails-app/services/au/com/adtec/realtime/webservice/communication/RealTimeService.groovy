package au.com.adtec.realtime.webservice.communication

import grails.transaction.Transactional
import org.atmosphere.cpr.Broadcaster
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import rt_com.COMmanager
import rt_do.DOerror
import rt_do.DOrequest
import rt_do.DOresponse
import rt_talk.TALKrequestManager
import rt_talk.TALKresponseListener
import rt_talk.TALKstatusManager

@Transactional
class RealTimeService {

    def connections = [:]

    def grailsApplication;

    def host = 'ictest.adtec.com.au'
    // def host = 'online2.adtec.com.au'
    def port = 8642
    // def port = 8654

    boolean connect(Broadcaster broadcaster) {
        try {
            def message = "Creating socket to $grailsApplication.config.au.com.adtec.rt.ip on port $grailsApplication.config.au.com.adtec.rt.port"
            log.info(message)
            broadcaster.broadcast(message)

            Socket socket = new Socket();
            socket.keepAlive = true
            socket.receiveBufferSize = 8192
            socket.reuseAddress = false
            socket.sendBufferSize = 8192
            socket.setSoLinger(false, 1)
            socket.tcpNoDelay = true
            socket.connect(new InetSocketAddress(host, port), 3600000)
            startSocketListener(socket, broadcaster)
            connections[broadcaster] = socket

            message = "Socket connection successful."
            log.info(message)
            broadcaster.broadcast(message)
            return true
        } catch (Exception e) {
            log.error(e)
            broadcaster.broadcast(e.toString())
            return false
        }
    }

    def push(String packet, Broadcaster broadcaster) {
        def message = "Pushing: $packet..."
        log.info(message)
        broadcaster.broadcast(message)
        try {
            Socket socket = connections[broadcaster]
            if (socket && socket.connected && !socket.closed) {
                def writer = new BufferedWriter(new OutputStreamWriter(socket.outputStream))
                writer.write("$packet\r\n")
                writer.flush()
            }
        } catch (Exception e) {
            log.error(e)
            broadcaster.broadcast(e.toString())
        }
    }

    private Thread startSocketListener(Socket socket, broadcaster) {
        Thread.start {
            try {
                Thread.sleep(1000)
                log.info("Started Listening...")
                while (socket.connected && !socket.closed) {
                    def reader = new BufferedReader(new InputStreamReader(socket.inputStream))
                    String line
                    while ((line = reader.readLine()) != null) {
                        broadcaster.broadcast(line);
                    }
                }
                println("Finished listening")
            } catch (Exception e) {
                log.error(e)
                broadcaster.broadcast(e.toString())
            }
        }
    }

    def disconnect(Broadcaster broadcaster) {
        def message = "Disconnecting from RT..."
        log.info(message)
        broadcaster.broadcast(message)
        Socket socket = connections[broadcaster]
        if (socket ) {
            try {
                socket.close()
                message = "Disconnected from RT"
                log.info(message)
                broadcaster.broadcast(message)
            } catch (Exception e) {
                log.error(e)
                broadcaster.broadcast(e.toString())
            }
            connections.remove(broadcaster)
        }
    }

    //region Connect & Login using TALKstatusListener
    boolean connect(String host, String port) {
        TALKstatusManager.instance.cleanup()
        TALKrequestManager trm = TALKrequestManager.instance
        /* Call getInstance to initialize TALKrequestListener */
        COMmanager.instance
        return trm.StartTALK(true, host, port)
    }

    def login(String username, String password, Closure onSuccess, Closure onError) {
        DOrequest dor = new DOrequest()
        dor.put(DOrequest.ARG_COMMAND, DOrequest.COM_LOGIN)
        dor.put(DOrequest.ARG_USER_NAME, username)
        dor.put(DOrequest.ARG_PASSWORD, password)
        dor.setResponseListener(new TALKresponseListener() {
            @Override
            void requestResponse(DOresponse response) {
                if (onSuccess) onSuccess(response)
            }

            @Override
            void requestError(DOerror error) {
                if (onError) onError(error)
            }
        })
        TALKrequestManager.instance.sendRequest(dor)
    }
    //endregion
}
