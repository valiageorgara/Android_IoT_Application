package JavaApp.kNN;

public class EuclideanDistance {
    public static double calEuclideanDistance(double[] vectorA, double[] vectorB) {
        double total = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            total += Math.pow(vectorA[i] - vectorB[i], 2.0);
        }
        return Math.sqrt(total);
    }
}
