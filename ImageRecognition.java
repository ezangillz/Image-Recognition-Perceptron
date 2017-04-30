import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class ImageRecognition {

    private static final int NUMBER_OF_INPUTS = 7;

    public static void main(String[] args) {
        ImageRecognition imageRecognition = new ImageRecognition();
        if (args.length == 0) {
            System.out.println("ERROR: no options passed, use -help for more info");
            System.exit(0);
        }
        else if (args[0].equals("-help")) {
            System.out.println("The commands are:" +
                    "\n\n-help\t\t\t\t\t| shows this thing" +
                    "\n\n\n[path_to_image]\t\t\t\t| tells you what's displayed on your image" +
                    "\n\n\n-learn [OBJECT_NUMBER (A OR B)]\t\t|\n\t\t[path_to_img_1]\t\t|\n\t\t[path_to_img_2] ...\t|\n\t\t[path_to_img_N]\t\t| teaches your dumb network to see the object from files from 1 to N");
            System.exit(0);
        }
        else if (args[0].equals("-learn")) {
            int object = 0;
            if (args[1].equals("A") || args[1].equals("a"))
                object = -1;
            else if (args[1].equals("B") || args[1].equals("b"))
                object = 1;
            else {
                System.out.println("Choose object between A and B please");
                System.exit(0);
            }
            String[] images = new String[args.length-2];
            for (int i = 2; i < args.length; i++) {
                images[i-2] = args[i];
            }
            imageRecognition.learn(object, images);
        }
        else {
            PerceptronClient perceptronClient = new PerceptronClient();
            double[] inputs = imageRecognition.openImage(args[0]);
            int assumedOutput = perceptronClient.calculate(inputs, NUMBER_OF_INPUTS);
            Scanner reader = new Scanner(System.in);

            if (assumedOutput == -1) {
                System.out.println("Is it an OBJECT A? Y\\N");
                String answer = reader.nextLine();
                if (answer.equals("N") || answer.equals("n"))
                    perceptronClient.relearn(1, assumedOutput, inputs, NUMBER_OF_INPUTS);
            }
            else {
                System.out.println("Is it an OBJECT B? Y\\N");
                String answer = reader.nextLine();
                if (answer.equals("N") || answer.equals("n"))
                    perceptronClient.relearn(-1, assumedOutput, inputs, NUMBER_OF_INPUTS);
            }
        }

    }

    /**
     * The network learns that the object, that is shown in the set of passed images, is represented by -1 or 1
     * @param object takes -1 or 1 as argument to represent one of two objects
     * @param images set of images in which the object is shown that is bind to the first argument
     */
    private void learn(int object, String[] images) {
        PerceptronClient perceptronClient = new PerceptronClient();
        for (int i = 0; i < images.length; i++) {
            double[] inputs = openImage(images[i]);
            perceptronClient.calculate(inputs, NUMBER_OF_INPUTS, object);
        }
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
            //System.out.println(normalized[i]);
        }
    }

    /* returns bin of the pixel that was passed to this func */
    private int getBin(int value) {
        int bin = 1;
        if (value < 32)
            bin = 1;
        else if (value < 64)
            bin = 2;
        else if (value < 96)
            bin = 3;
        else if (value < 128)
            bin = 4;
        else if (value < 160)
            bin = 5;
        else if (value < 192)
            bin = 6;
        else if (value <= 225)
            bin = 7;
        return bin;
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
}
