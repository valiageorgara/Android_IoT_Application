package JavaApp.kNN;

import JavaApp.EntropyPckg.ExperimentsList;
import com.opencsv.CSVReader;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

import static JavaApp.kNN.Classifier.classify;
import static JavaApp.kNN.EuclideanDistance.calEuclideanDistance;
import static JavaApp.kNN.ShortestDistance.calSetWithkMinValues;

public class kNNAlgorithm {
    private static DecimalFormat df2 = new DecimalFormat(".##"); //keep 2 decimal points 

    public ArrayList<Results> kNN(ArrayList<ExperimentsList> array) throws IOException {
        // Read Training Set
        CSVReader csvReader = new CSVReader(new FileReader("Training_Set.csv"));
        List<String[]> list = csvReader.readAll();

        // Convert Training Set to 2D array
        String[][] trainSet = new String[list.size()][];
        trainSet = list.toArray(trainSet);

        int lines = trainSet.length; // lines = 37;
        double[] vectorA = new double[14];
        double[] vectorB = new double[14];
        double[] EuclideanArray = new double[lines-1]; // store euclidean distances
        String[] LabelArray = new String[lines-1]; // store labels of euclidean distances

        /////////////
        int k = 11;
        ////////////
        int[] shortestIndices = new int[k];
        double[] I = new double[k];
        double all = 264; //all predictions
        double right = 0; // right predictions
        double accuracy; // accuracy percentage
        int counterClosed;
        int counterOpened;
        ArrayList<Results> resultsArrayList = new ArrayList<Results>();
        ////////////////////////////////////////////
        FileWriter fw = new FileWriter("out.txt");
        ////////////////////////////////////////////
        //traverse elements of ArrayList object
        //////////grammh arraylist --> csv dld
        Iterator itr=array.iterator();
        while (itr.hasNext()) { //start csv

            ExperimentsList el = (ExperimentsList) itr.next();
            Enumeration en = (el.vector).elements();
            int i = 0;

            while (en.hasMoreElements()) {
                vectorA[i] = (double) en.nextElement();
                i++;
            }

/////////////////trainingSet
            for (int j = 1; j < lines; j++) {
                LabelArray[j-1] = (trainSet[j][0]).toString();
                for (int m = 1; m < 15; m++) {
                    vectorB[m - 1] = Double.valueOf(trainSet[j][m]);
                }
                EuclideanArray[j-1] = calEuclideanDistance(vectorA,vectorB);
            }
            //System.out.println("\n");
           // System.out.println("\n");

//////////////////////////////////
            shortestIndices = calSetWithkMinValues(EuclideanArray, k);
            counterClosed = 0;
            counterOpened = 0;
            Neighbors[] neighbors = new Neighbors[k];
//////////////////////////////////
            for (int j = 0; j <k ; j++) {
                neighbors[j] = new Neighbors(EuclideanArray[shortestIndices[j]],LabelArray[shortestIndices[j]] );
                if((neighbors[j].classlabel).equals("EyesOpened")){
                    counterOpened++;
                }
                else if((neighbors[j].classlabel).equals("EyesClosed")){
                    counterClosed++;
                }


            }
            String classifiedLabel = classify(neighbors);
            if (counterClosed > counterOpened) {
                if((el.name).equals(classifiedLabel)) right++;
                //System.out.println(right);

            }
            else{
                if((el.name).equals(classifiedLabel)) right++;
                //System.out.println(right);
            }
            //store results to arraylist
            Results re = new Results("Execute " + classifiedLabel , el.name);
            resultsArrayList.add(re);
            fw.write("Execute " + classifiedLabel + " " + el.name + "\n");
        } //end csv
        accuracy = (right*100)/all;
        System.out.println("kNN Algorithm done! Results extracted to an arraylist and a txt file!");
        System.out.println("Accuracy Percentage = " + df2.format(accuracy) + "%");
        fw.close();
        return resultsArrayList;
    }
}
