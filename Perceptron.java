import java.io.*;
import java.util.concurrent.ThreadLocalRandom;

public class Perceptron implements Serializable {

    private double[] weights;
    private double theta;
    private double learningRate;

    /* generates new "dumb" perceptron*/
    public void generate(int capacity) {
        weights = new double[capacity];
        for (int i = 0; i < capacity; i++)
            weights[i] = ThreadLocalRandom.current().nextDouble(-10, 10 + 1);
        theta = ThreadLocalRandom.current().nextDouble(-10, 10 + 1);
        learningRate = ThreadLocalRandom.current().nextDouble(0.1, 0.9 + 1);
    }

    public double sumUp(double[] inputs) {
        double sum = 0;
        for (int i = 0; i < weights.length; i++)
            sum += weights[i] * inputs[i];
        sum += theta;
        return sum;
    }

    public int determine(double sum) {
        if (sum <= theta) {
            //System.out.println("OBJECT -1");
            return -1;
        }
        //System.out.println("OBJECT 1");
        return 1;
    }

    public void relearn(int answer, int assumedOutput, double[] inputs, int capacity) {
        theta += answer;
        System.out.print("Theta: " + theta + "\t");
        for (int i = 0; i < capacity; i++) {
            weights[i] += answer * inputs[i];
            System.out.print("w" + i + " " + weights[i] + " ");
        }
        System.out.println("Relearned");
    }

}
