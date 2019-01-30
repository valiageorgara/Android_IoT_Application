package JavaApp.EntropyPckg;

import java.util.Vector;

public class ExperimentsList {
    public int id;
    public String name;
    public Vector vector;

    public ExperimentsList(int index, String exp_name, Vector vec){
        id = index;
        name= exp_name;
        vector = vec;



    }
}
