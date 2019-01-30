package JavaApp.EntropyPckg;

import java.util.Enumeration;
import java.util.Vector;

public class FeatureVectorClass {
    public static Vector FeautureVector(double[] entropies){

        Vector <Double> feature_vec = new Vector<Double>(14);

        for (int i = 0; i < entropies.length ; i++) {
            feature_vec.addElement(entropies[i]);
        }
        //DisplayVectorElements(feature_vec);

        return feature_vec;

    }

    public static void DisplayVectorElements(Vector vector){
        Enumeration en = vector.elements();
        System.out.println("\n Elements are: ");
        while (en.hasMoreElements())
            System.out.println(en.nextElement() + " ");
    }
}
