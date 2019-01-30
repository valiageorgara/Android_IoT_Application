package JavaApp;
import java.io.IOException;
import java.util.*;
import JavaApp.EntropyPckg.ExperimentsList;
import JavaApp.EntropyPckg.csvReader;
import JavaApp.ProducerConsumerPckg.ProducerConsumerThreads;
import JavaApp.kNN.Results;
import JavaApp.kNN.kNNAlgorithm;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import static JavaApp.PrintHelp.printHelp;
import static JavaApp.logClass.log;

public class JavaApp {

    public static void main(String[] args) throws MqttException, IOException, InterruptedException {


        // Parse the arguments -
        for (int i=0; i<args.length; i++) {
            // Check this is a valid argument
            if (args[i].length() == 2 && args[i].startsWith("-")) {
                char arg = args[i].charAt(1);
                // Handle arguments that take no-value
                switch(arg) {
                    case 'h': case '?':	printHelp(); return;
                    case 'q': quietMode = true;	continue;
                }
                // Now handle the arguments that take a value and
                // ensure one is specified
                if (i == args.length -1 || args[i+1].charAt(0) == '-') {
                    System.out.println("Missing value for argument: "+args[i]);
                    printHelp();
                    return;
                }
                switch(arg) {
                    case 'a': action = args[++i];                 break;
                    case 't': topic = args[++i];                  break;
                    case 'm': message = args[++i];                break;
                    case 's': qos = Integer.parseInt(args[++i]);  break;
                    case 'i': clientId = args[++i];				  break;
                    case 'c': cleanSession = Boolean.valueOf(args[++i]).booleanValue();  break;
                    default:
                        System.out.println("Unrecognised argument: "+args[i]);
                        printHelp();
                        return;
                }
            } else {
                System.out.println("Unrecognised argument: "+args[i]);
                printHelp();
                return;
            }
        }
        // Validate the provided arguments
        if (!action.equals("publish") && !action.equals("subscribe")) {
            System.out.println("Invalid action: "+action);
            printHelp();
            return;
        }
        if (qos < 0 || qos > 2) {
            System.out.println("Invalid QoS: "+qos);
            printHelp();
            return;
        }
        if (topic.equals("")) {
            // Set the default topic according to the specified action
            if (action.equals("publish")) {
                topic = pubTopic;
            } else {
                topic = subTopic;
            }
        }
        if (clientId == null || clientId.equals("")) {
            clientId = "SampleJavaV3_"+action;
        }
        // With a valid set of arguments, the real work of driving the client API can begin ///////////////////////////////////////
        try {
            // Create an instance of this class
            JavaApp JA = new JavaApp(broker, clientId, cleanSession, quietMode);
            //action = "subscribe";
            Subscriber sub = new Subscriber();
            sub.subscribe(subTopic,qos);

        } catch(MqttException me) {
            // Display full details of any exception that occurs
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
    }   /////TELOS MAIN//////

    // public instance variables //////////////////////////////////////////////////////////////////
    public static MqttClient client;
    public static String brokerUrl;
    public static MqttConnectOptions conOpt;
    public static boolean clean;
    public static  boolean quietMode 	= false; //whether debug should be printed to standard out
    public static String action 		= "publish";
    public static  String topic 		= "";
    public static String message 		= "default message";
    public static int qos 			= 0;
    public static String broker       ="tcp://localhost:1883"; //the url of the server to connect to
    public static String clientId 	= null; //the client id to connect with
    public static String subTopic   = "ServerSubscribe_ClientPublish";
    public static String pubTopic   = "ServerPublish_ClientSubscribe";
    public static boolean cleanSession = true;  // Non durable subscriptions
    /////////////////////////////////////////////////////////////////////////////////////////////

    public JavaApp(String brokerUrl, String clientId, boolean cleanSession, boolean quietMode) throws MqttException {
        this.brokerUrl = brokerUrl;
        this.quietMode = quietMode;
        this.clean 	   = cleanSession;

        String tmpDir = System.getProperty("java.io.tmpdir");
        MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(tmpDir);
        try {
            // Construct the connection options object that contains connection parameters such as cleanSession and LWT
            conOpt = new MqttConnectOptions();
            conOpt.setCleanSession(clean);

            // Construct an MQTT blocking mode client
            client = new MqttClient(this.brokerUrl,clientId, dataStore);

            // Set this wrapper as the callback handler
            //client.setCallback(this);

        } catch (MqttException e) {
            e.printStackTrace();
            log("Unable to set up client: "+e.toString());
            System.exit(1);
        }
    }

}