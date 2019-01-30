package JavaApp.ProducerConsumerPckg;

import JavaApp.Publisher;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static JavaApp.JavaApp.qos;
import static JavaApp.JavaApp.topic;

public class Consumer implements Runnable {
    private final ArrayList<String> buffer;
    private final int SIZE;
    private final int frequency;

    public Consumer(ArrayList<String> buffer, int size,  int f, String topic) {
        this.buffer = buffer;
        this.SIZE = size;
        this.frequency = f;
    }

    @Override
    public void run() {
        while (true) {
            try {
                System.out.println("Consumed: " + consume());
                Thread.sleep(frequency*1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }
    private String consume() throws InterruptedException, MqttException {
        synchronized (buffer) {
            //wait if queue is empty
            while (buffer.isEmpty()) {
                System.out.println("Queue is empty " + Thread.currentThread().getName() + " is waiting , size: " + buffer.size());
                buffer.wait();
            }
            //Otherwise consume element and notify waiting producer
            String s = buffer.remove(0);
            System.out.println("/////////////////////////");
            Publisher pub = new Publisher();
            pub.publish(topic,qos,s);
            System.out.println("/////////////////////////");
            buffer.notifyAll();
            return s;
        }
    }
}
