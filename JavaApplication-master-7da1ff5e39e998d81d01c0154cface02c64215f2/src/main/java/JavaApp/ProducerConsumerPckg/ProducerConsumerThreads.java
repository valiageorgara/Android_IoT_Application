package JavaApp.ProducerConsumerPckg;
import JavaApp.kNN.Results;
import java.util.ArrayList;

public class ProducerConsumerThreads {
    /**
     * Java program to solve Producer Consumer problem using wait and notify
     * method in Java. Producer Consumer is also a popular concurrency design pattern.
     */
    public static void ProducerConsumerSolution(ArrayList<Results> ResultsArray, int frequency, String pubTopic) {
        ArrayList<String> buffer = new ArrayList<String>();
        int size = 10;
        Thread prodThread = new Thread(new Producer(ResultsArray, size , buffer), "Producer");
        Thread consThread = new Thread(new Consumer(buffer, size, frequency, pubTopic), "Consumer");
        prodThread.start();
        consThread.start();
    }
}