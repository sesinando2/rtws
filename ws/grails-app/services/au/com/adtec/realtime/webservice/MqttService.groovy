package au.com.adtec.realtime.webservice

import grails.transaction.Transactional
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

@Transactional
class MqttService {

    private final static int KEEP_ALIVE_INTERVAL = 30
    private final static int CONNECTION_TIMEOUT = 15

    def grailsApplication

    private MqttClient client
    private boolean initialized = false

    def init() {
        if (!initialized) {
            client = new MqttClient((String) grailsApplication.config.au.com.adtec.mqtt.url, "RealTimeWebService", new MemoryPersistence())
            MqttConnectOptions options = new MqttConnectOptions();
            options.setKeepAliveInterval(KEEP_ALIVE_INTERVAL)
            options.setConnectionTimeout(CONNECTION_TIMEOUT)
            client.callback = new MqttCallback() {
                @Override
                void connectionLost(Throwable throwable) {
                    log.error(throwable)
                }

                @Override
                void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    log.debug("String: $s, MqttMessage: $mqttMessage")
                }

                @Override
                void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                    log.debug("IMqttDeliveryToken: $iMqttDeliveryToken")
                }
            }
            client.connect(options)
            initialized = true
        }
    }

    def publish(String topic, String message) {
        init()
        MqttMessage mqttMessage = new MqttMessage(message.bytes)
        mqttMessage.setQos(1)
        client.publish(topic, mqttMessage)
    }
}
