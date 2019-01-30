package JavaApp.kNN;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Classifier {
    /*  Classify according to weights
    //////////////////////////////////
        After the K nearest neighbors are found, a HashMap is created to keep the pairs
        <label, weight> by going through all the neighbors. If the HashMap happens to
        meet a new label, <label, 1 / distance> is directly added into the map. Otherwise,
        an updated version which adds the original weight with 1 / distance is put into the
        map. After the HashMap is correctly constructed, this program will go through the
        whole HashMap and find the label associated with the largest weight. Below are
        the Java codes which demonstrate the implementation:*/

    // Get the class label by using neighbors
    static String classify(Neighbors[] neighbors){
        //construct a HashMap to store <classLabel, weight>
        HashMap<String, Double> map = new HashMap<String, Double>();
        int num = neighbors.length;

        for(int index = 0;index < num; index ++){
            Neighbors temp = neighbors[index];
            String key = temp.classlabel;

            //if this classLabel does not exist in the HashMap, put <key, 1/(temp.distance)> into the HashMap
            if(!map.containsKey(key))
                map.put(key, 1 / temp.distance);

                //else, update the HashMap by adding the weight associating with that key
            else{
                double value = map.get(key);
                value += 1 / temp.distance;
                map.put(key, value);
            }
        }

        //Find the most likely label
        double maxSimilarity = 0;
        String returnLabel = "none";
        Set<String> labelSet = map.keySet();
        Iterator<String> it = labelSet.iterator();

        //go through the HashMap by using keys
        //and find the key with the highest weights
        while(it.hasNext()){
            String label = it.next();
            double value = map.get(label);
            if(value > maxSimilarity){
                maxSimilarity = value;
                returnLabel = label;
            }
        }

        return returnLabel;
    }
}
