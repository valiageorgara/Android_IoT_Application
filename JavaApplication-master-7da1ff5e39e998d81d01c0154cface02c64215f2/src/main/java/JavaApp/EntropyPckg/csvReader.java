package JavaApp.EntropyPckg;

import com.opencsv.CSVReader;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static JavaApp.EntropyPckg.Entropy.calculateEntropy;
import static JavaApp.EntropyPckg.FeatureVectorClass.FeautureVector;


public class csvReader {

    public ArrayList<ExperimentsList> CsvReader() throws IOException {
        File dir = new File("experiments/");
        ArrayList<ExperimentsList> arrList = new ArrayList<ExperimentsList>();
        int listItems = 0;
        for (File file: dir.listFiles()) {

            // Read all
            CSVReader csvReader = new CSVReader(new FileReader(file));
            List<String[]> list = csvReader.readAll();

            // Convert to 2D array
            String[][] dataArr = new String[list.size()][];
            dataArr = list.toArray(dataArr);

            //store number of lines of csv file
            int numOfLines = dataArr.length;
            double[][] Channels = new double[14][numOfLines];


            for (int i = 0; i < 14; i++) {
                for (int j = 1; j < numOfLines ; j++) {
                    Channels[i][j-1] = Double.valueOf(dataArr[j][i]);
                }

            }


            double[] Entropies = new double[14];

            for (int j = 0; j < 14; j++)
                Entropies[j] = calculateEntropy(Channels[j]);


            Vector FeatVec = FeautureVector(Entropies);


            //store experiment name in a string
            String ExperimentName = (file.getName()).replaceAll("[^a-zA-Z]+", "");
            String[] parts = ExperimentName.split("csv");
            String part2 = parts[0];
            ExperimentName = part2;


            //store experiment values to arraylist
            ExperimentsList el = new ExperimentsList(listItems,ExperimentName,FeatVec);
            arrList.add(el);
            listItems++;

            csvReader.close();
        }


        System.out.println("Entropies were calculated successfully!");
        return (arrList);
    }

}


