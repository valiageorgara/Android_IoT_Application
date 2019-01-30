package JavaApp.ProducerConsumerPckg;

import JavaApp.kNN.Results;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Producer implements Runnable {
    private final ArrayList<Results> ResultsArray;
    private final int SIZE;
    ArrayList<String> buffer;
    public Producer(ArrayList<Results> ResultsArray, int size ,ArrayList<String> buffer) {
        this.ResultsArray = ResultsArray;
        this.SIZE = size;
        this.buffer = buffer;
    }

    @Override
    public void run() {
        for (int i = 0; i < ResultsArray.size(); i++) {
            System.out.println("Produced: " + ResultsArray.get(i).classifiedLabel);
            try {
                produce(ResultsArray.get(i).classifiedLabel);
            } catch (InterruptedException ex) {
                Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void produce(String i) throws InterruptedException {
        synchronized (buffer) {
            //wait if queue is full
            while (buffer.size() == SIZE) {
                System.out.println("Queue is full " + Thread.currentThread().getName() + " is waiting , size: " + buffer.size());
                buffer.wait();
            }
            //producing element and notify consumers
            buffer.add(i);
            buffer.notifyAll();
        }
    }
}