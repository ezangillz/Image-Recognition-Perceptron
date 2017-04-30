import java.io.*;

public class PerceptronClient {

    private static final String FILE_NAME = "perceptron.ser";

    /** this function is dedicated for "automatic" learning
     * cause it gets normalized inputs, number of inputs and the right answer
     * @param inputs
     * @param capacity number of inputs
     * @param answer the right answer that net should choose, otherwise will be relearned
     */
    public void calculate(double[] inputs, int capacity, int answer) {
        Perceptron perceptron = open();
        if (perceptron == null)
            perceptron = newPerceptron(perceptron, capacity);
        int assumedOutput = perceptron.determine(perceptron.sumUp(inputs));
        if (answer != assumedOutput) {
            perceptron.relearn(answer, assumedOutput, inputs, capacity);
            save(perceptron);
        }
    }

    /* this func is dedicated just for a single image */
    public int calculate(double[] inputs, int capacity) {
        Perceptron perceptron = open();
        if (perceptron == null)
            perceptron = newPerceptron(perceptron, capacity);
        return perceptron.determine(perceptron.sumUp(inputs));
    }

    public void relearn(int answer, int assumedOutput, double[] inputs, int capacity) {
        Perceptron perceptron = open();
        if (perceptron == null)
            perceptron = newPerceptron(perceptron, capacity);
        perceptron.relearn(answer, assumedOutput, inputs, capacity);
        save(perceptron);
    }

    private Perceptron newPerceptron(Perceptron perceptron, int capacity) {
        System.out.println("Perceptron was null");
        perceptron = new Perceptron();
        perceptron.generate(capacity);
        save(perceptron);
        return perceptron;
    }

    /**
     opens a saved perceptron with the saved weights and theta, and returns TRUE
     though if there's no saved perceptron it returns FALSE
     */
    private Perceptron open() {
        try {
            ObjectInputStream os = new ObjectInputStream(new FileInputStream(FILE_NAME));
            Perceptron tmp = (Perceptron) os.readObject();
            os.close();
            return tmp;
        } catch (IOException | ClassNotFoundException e) {
            //e.printStackTrace();
            return null;
        }
    }

    private void save(Perceptron perceptron) {
        try {
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(FILE_NAME));
            os.writeObject(perceptron);
            os.close();
            System.out.println("Saved");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
