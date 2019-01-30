package JavaApp.kNN;

import java.util.Arrays;

import static java.util.Arrays.copyOf;

public class ShortestDistance {
    public static int[] calSetWithkMinValues(double[] array , int k){
        double[] copy = copyOf(array, array.length);

        Arrays.sort(copy);


        int [] indices = new int[k];
        int counter =0;


        for (int j = 0; j <copy.length ; j++) {
            for (int m = 0; m <array.length ; m++) {
                if(array[m]==copy[j] && counter<k){
                    indices[counter] = m;
                    counter++;
                    break;
                }

            }
        }

        return indices;
        //for (int i = 0; i <k ; i++) {
        //    System.out.println(indices[i]);
        //}

    }
}
