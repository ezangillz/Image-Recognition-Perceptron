import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class ImageRecognition {

    private static final int NUMBER_OF_INPUTS = 28;
    private static final int NUMBER_OF_ITERATIONS = 10;

    public static void main(String[] args) {
        ImageRecognition imageRecognition = new ImageRecognition();
        if (args.length == 0) {
            System.out.println("No options passed, use -help for more info");
            System.exit(0);
        }
        else if (args[0].equals("-help")) {
            imageRecognition.showHelp();
            System.exit(0);
        }
        else if (args[0].equals("-learn")) {
            ArrayList<String> imagesOne = new ArrayList<>();
            ArrayList<String> imagesTwo = new ArrayList<>();

            int i = 2;
            for (; args[i+2].charAt(0) != '-'; i++)
                imagesOne.add(args[i + 2]);

            i += 2;
            String objOne = args[1];
            String objTwo = args[i++];

            for (; i + 2 < args.length; i++)
                imagesTwo.add(args[i + 2]);

            imageRecognition.learn(objOne, objTwo, imagesOne, imagesTwo, NUMBER_OF_ITERATIONS);
        }
        else if (args[0].equals("-reset")) {
            Perceptron.getInit(NUMBER_OF_INPUTS).reset();
            System.out.println("Done");
        }
        else {
            imageRecognition.feedPicture(args[0]);
        }

    }

    /* takes just one picture to determine what object is displayed */
    private void feedPicture(String imagePath) {
        double[] inputs = openImage(imagePath);
        int assumedOutput = calculate(inputs, NUMBER_OF_INPUTS);
        Scanner reader = new Scanner(System.in);

        if (assumedOutput == -1) {
            System.out.println(Perceptron.getInit(NUMBER_OF_INPUTS).getObjOne() + "? Y\\N");
            String answer = reader.nextLine();
            if (answer.equals("N") || answer.equals("n"))
                relearn(1, assumedOutput, inputs, NUMBER_OF_INPUTS);
        }
        else {
            System.out.println(Perceptron.getInit(NUMBER_OF_INPUTS).getObjTwo() + "? Y\\N");
            String answer = reader.nextLine();
            if (answer.equals("N") || answer.equals("n"))
                relearn(-1, assumedOutput, inputs, NUMBER_OF_INPUTS);
        }
    }

    /**
     * The network learns that the object, that is shown in the set of passed images, is represented by -1 or 1
     */
    private void learn(String objOne, String objTwo, ArrayList<String> imagesOne, ArrayList<String> imagesTwo, int numberOfIterations) {
        Perceptron.getInit(NUMBER_OF_INPUTS).setObjOne(objOne);
        Perceptron.getInit(NUMBER_OF_INPUTS).setObjTwo(objTwo);
        System.out.println("\n!!!!!!" + objOne);
        for (int i = 0; i < imagesOne.size(); i++) {
            double[] inputs = openImage(imagesOne.get(i));
            calculate(inputs, NUMBER_OF_INPUTS, -1);
        }
        System.out.println("\n!!!!!!" + objTwo);
        for (int i = 0; i < imagesTwo.size(); i++) {
            double[] inputs = openImage(imagesTwo.get(i));
            calculate(inputs, NUMBER_OF_INPUTS, 1);
        }
        if (numberOfIterations > 0)
            learn(objOne, objTwo, imagesOne, imagesTwo, numberOfIterations - 1);
    }

    /* opens an image with passed path and returns a normalized array of that image */
    private double[] openImage(String image_path) {
        try {
            //System.out.println(image_path);
            BufferedImage img = ImageIO.read(new File(image_path));
            int width = img.getWidth();
            int height = img.getHeight();
            int[][] grayImg = new int[width][height];
            convertToGrayscale(img, width, height, grayImg);
            double[] normalized = new double[NUMBER_OF_INPUTS];
            normalize(width, height, grayImg, normalized);
            return normalized;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /* normalizes the pic to make it suitable for passing to the perceptron */
    private void normalize(int width, int height, int[][] grayImg, double[] normalized) {
        int[] bins = new int[NUMBER_OF_INPUTS];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (0 != getBin(grayImg[x][y])) {
                    int bin = getBin(grayImg[x][y]);
                    bins[bin-1] += 1;
                }
            }
        }

        double all = 0;
        for (int i = 0; i < NUMBER_OF_INPUTS; i++) {
            all += bins[i];
        }

        for (int i = 0; i < NUMBER_OF_INPUTS; i++) {
            normalized[i] = bins[i] / all;
        }
    }

    /* converts to grayscale lol */
    private void convertToGrayscale(BufferedImage img, int width, int height, int[][] grayImage) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = img.getRGB(x, y);
                int r = (p>>16)&0xff;
                int g = (p>>8)&0xff;
                int b = p&0xff;
                int avg = (r + g + b) / 3;
                grayImage[x][y] = avg;
            }
        }
    }

    /** this function is dedicated for "automatic" learning
     * cause it gets normalized inputs, number of inputs and the right answer
     * @param inputs
     * @param capacity number of inputs
     * @param answer the right answer that net should choose, otherwise will be relearned
     */
    public void calculate(double[] inputs, int capacity, int answer) {
        //System.out.println("\n\n\n" + learningRate + "\n\n\n");
        int assumedOutput = Perceptron.getInit(capacity).determine(Perceptron.getInit(capacity).sumUp(inputs));
        if (answer != assumedOutput) {
            Perceptron.getInit(capacity).relearn(answer, inputs, capacity);
        }
    }

    /* this func is dedicated just for a single image */
    public int calculate(double[] inputs, int capacity) {
        return Perceptron.getInit(capacity).determine(Perceptron.getInit(capacity).sumUp(inputs));
    }

    public void relearn(int answer, int assumedOutput, double[] inputs, int capacity) {
        Perceptron.getInit(capacity).relearn(answer, inputs, capacity);
    }

    private void showHelp() {
        System.out.println("___________________________________________________________________________" +
                "\n-help\t\t\t\t\t\t | shows this thing" +
                "\n___________________________________________________________________________" +
                "\n\n[path_to_image]\t\t\t\t\t | recognizes single image" +
                "\n___________________________________________________________________________" +
                "\n\n-learn -[OBJECT_A_NAME] [FOLDER_WITH_IMAGES_A/*] |" +
                "\n\t-[OBJECT_B_NAME] [FOLDER_WITH_IMAGES_B/*]| teaches the net to recognize certain objects" +
                "\t\t\t\t\t\t\t" +
                "\n\n\t\tEXAMPLE: -learn -Apple apples/* -Banana bananas/*" +
                "\n___________________________________________________________________________" +
                "\n\n-reset\t\t\t\t\t\t | resets the net");
    }

    /* returns bin of the pixel that was passed to this func */
    private int getBin(int value) {
        int bin = 1;
        if (value < 8)
            bin = 1;
        else if (value < 16)
            bin = 2;
        else if (value < 24)
            bin = 3;
        else if (value < 32)
            bin = 4;
        else if (value < 40)
            bin = 5;
        else if (value < 48)
            bin = 6;
        else if (value < 56)
            bin = 7;
        else if (value < 64)
            bin = 8;
        else if (value < 72)
            bin = 9;
        else if (value < 80)
            bin = 10;
        else if (value < 88)
            bin = 11;
        else if (value < 96)
            bin = 12;
        else if (value < 104)
            bin = 13;
        else if (value < 112)
            bin = 14;
        else if (value < 120)
            bin = 15;
        else if (value < 128)
            bin = 16;
        else if (value < 136)
            bin = 17;
        else if (value < 144)
            bin = 18;
        else if (value < 152)
            bin = 19;
        else if (value < 160)
            bin = 20;
        else if (value < 168)
            bin = 21;
        else if (value < 176)
            bin = 22;
        else if (value < 184)
            bin = 23;
        else if (value < 192)
            bin = 24;
        else if (value < 200)
            bin = 25;
        else if (value < 208)
            bin = 26;
        else if (value < 216)
            bin = 27;
        else if (value <= 225)
            bin = 28;
        return bin;
    }

}
