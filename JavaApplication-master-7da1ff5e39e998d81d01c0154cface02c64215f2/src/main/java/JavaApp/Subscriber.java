package JavaApp;

import JavaApp.EntropyPckg.ExperimentsList;
import JavaApp.EntropyPckg.csvReader;
import JavaApp.ProducerConsumerPckg.ProducerConsumerThreads;
import JavaApp.kNN.Results;
import JavaApp.kNN.kNNAlgorithm;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;

import static JavaApp.JavaApp.*;
import static JavaApp.logClass.log;
import static java.lang.Thread.sleep;

public class Subscriber {
    /**
     * Subscribe to a topic on an MQTT server
     * Once subscribed this method waits for the messages to arrive from the server
     * that match the subscription. It continues listening for messages until the enter key is
     * pressed.
     * @param topicName to subscribe to (can be wild carded)
     * @param qos the maximum quality of service to receive messages at for this subscription
     * @throws MqttException
     */
    public static void subscribe(String topicName, int qos) throws MqttException, InterruptedException {
        // Connect to the MQTT server
        client.connect(conOpt);
        log("Connected to " + brokerUrl + " with client ID " + client.getClientId());

        // Subscribe to the requested topic
        // The QoS specified is the maximum level that messages will be sent to the client at.
        // For instance if QoS 1 is specified, any messages originally published at QoS 2 will
        // be downgraded to 1 when delivering to the client but messages published at 1 and 0
        // will be received at the same level they were published at.
        log("Subscribing to topic \"" + topicName + "\" qos " + qos);
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {
                // Called when the connection to the server has been lost.
                // An application may choose to implement reconnection
                // logic at this point. This sample simply exits.
                log("Connection to " + brokerUrl + " lost!" + throwable);
                System.exit(1);
            }
            @Override
            public void messageArrived(String topic, MqttMessage message) throws MqttException, IOException {
                // Called when a message arrives from the server that matches any
                // subscription made by the client
                String time = new Timestamp(System.currentTimeMillis()).toString();
                System.out.println("Time:\t" +time +
                        "  Topic:\t" + topic +
                        "  Message:\t" + String.valueOf(message) +
                        "  QoS:\t" + message.getQos());
                int frequency = Integer.parseInt(String.valueOf(message));
                System.out.println("Frequency given from the Android terminal is " + frequency);

                csvReader csv= new csvReader();
                ArrayList<ExperimentsList> arr =csv.CsvReader();
                kNNAlgorithm kNN  = new kNNAlgorithm();
                ArrayList<Results> resultsArray = kNN.kNN(arr);
                ProducerConsumerThreads te = new ProducerConsumerThreads();
                te.ProducerConsumerSolution(resultsArray,frequency, pubTopic);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                // Called when a message has been delivered to the
                // server. The token passed in here is the same one
                // that was passed to or returned from the original call to publish.
                // This allows applications to perform asynchronous
                // delivery without blocking until delivery completes.
                //
                // This sample demonstrates asynchronous deliver and
                // uses the token.waitForCompletion() call in the main thread which
                // blocks until the delivery has completed.
                // Additionally the deliveryComplete method will be called if
                // the callback is set on the client
                //
                // If the connection to the server breaks before delivery has completed
                // delivery of a message will complete after the client has re-connected.
                // The getPendingTokens method will provide tokens for any messages
                // that are still to be delivered.

                // Disconnect the client
            }
        });
        client.subscribe(topicName, qos);
        Thread.sleep(15000);

        //Disconnect the client from the server
        client.disconnect();
        log("Disconnected");

    }
}
