package JavaApp;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.sql.Timestamp;

import static JavaApp.JavaApp.brokerUrl;
import static JavaApp.JavaApp.client;
import static JavaApp.JavaApp.conOpt;
import static JavaApp.logClass.log;

/**
 * Publish / send a message to an MQTT server
 * topicName=  the name of the topic to publish to
 * qos =the quality of service to delivery the message at (0,1,2)
 * payload =the set of bytes to send to the MQTT server
 * @throws MqttException
 */

public class Publisher {
    public static void publish(String topicName, int qos, String payload) throws MqttException {
        //Connect to the MQTT server
        log("Connecting to "+brokerUrl + " with client ID "+client.getClientId());
        client.connect(conOpt);
        log("Connected");
        String time = new Timestamp(System.currentTimeMillis()).toString();
        System.out.println("Publishing at: " + time + " to topic \""+topicName+ "\" qos " +qos);
        // Create and configure a message
        MqttMessage message = new MqttMessage(payload.getBytes()); //////////////EDW MALLOON
        System.out.println(" with the message: " + payload + "\n");
        message.setQos(qos);
        //Send the message to the server, control is not returned until
        //it has been delivered to the server meeting the specified
        // quality of service.
        client.publish(topicName, message);
        //Disconnect the client
        client.disconnect();
        log("Disconnected");
    }
}