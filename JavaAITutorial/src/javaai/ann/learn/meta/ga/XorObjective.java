package javaai.ann.learn.meta.ga;

import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;
import org.encog.ml.genetic.genome.IntegerArrayGenome;
import org.encog.ml.genetic.genome.DoubleArrayGenome;
import java.util.Random;

import static javaai.util.Helper.asInt;

/**
 * This class calculates the fitness of an individual chromosome or phenotype.
 */
class XorObjective implements CalculateScore {
    public final static boolean DEBUGGING = false;
    public final static String TEAM = "MichaelPepsin";
    public final static int NUM_WEIGHTS = 10;
    public final static double RANGE_MAX = 10.0;
    public final static double RANGE_MIN = -10.0;
    protected static Random ran = null;
    static {
        long seed = System.nanoTime();
        if (DEBUGGING) {
            seed = TEAM.hashCode();
        }
        ran = new Random(seed);
    }

    public final static double XOR_INPUTS[][] = {
            {0.0, 0.0, 0.0},
            {0.0, 0.0, 1.0},
            {0.0, 1.0, 0.0},
            {0.0, 1.0, 1.0},
            {1.0, 0.0, 0.0},
            {1.0, 0.0, 1.0},
            {1.0, 1.0, 0.0},
            {1.0, 1.0, 1.0}
    };

    public final static double XOR_IDEALS[][] = {
            {1.0},
            {1.0},
            {0.0},
            {1.0},
            {0.0},
            {0.0},
            {0.0},
            {1.0}
    };

    /**
     * @param z
     * @return result of activation function
     */
    protected static double sigmoid(double z) {
        return 1/(1 + Math.pow(Math.E, -z));
    }

    /**
     * @param weights
     * @return activated y1
     */
    protected static double feedforward(double x1, double x2, double x3, double[] weights) {
        double w1 = weights[0];
        double w2 = weights[1];
        double w3 = weights[2];
        double w4 = weights[3];
        double w5 = weights[4];
        double w6 = weights[5];
        double w7 = weights[6];
        double w8 = weights[7];
        double b1 = weights[8];
        double b2 = weights[9];

        double h1 = ((x1 * w1) + (x2 * w3) + (x3 * w7) + b1);
        double h2 = ((x1 * w2) + (x2 * w4) + (x3 * w8) + b1);

        h1 = sigmoid(h1);
        h2 = sigmoid(h2);

        double y1 = ((h1 * w5) + (h2 * w6) + b2);
        return sigmoid(y1);
    }

    /**
     * @param wS weights
     * @return RMSE for the batch
     */
    protected static double getFitness(double[] wS) {
        double sumSqrErr = 0;
        for (int i=0; i<XOR_INPUTS.length; i++) {
            double y1 = feedforward(XOR_INPUTS[i][0], XOR_INPUTS[i][1], XOR_INPUTS[i][2], wS);
            double sqrErr = Math.pow((y1-XOR_IDEALS[i][0]), 2);
            sumSqrErr += sqrErr;
        }
        return Math.sqrt(sumSqrErr/XOR_INPUTS.length);
    }

    /**
     * Generate random weight in range [-10, 10]
     * @return double
     */
    public static double getRandomWeight() {
        double wt = ran.nextDouble() * (RANGE_MAX-RANGE_MIN) + RANGE_MIN;
        return wt;
    }

    /**
     * Calculates the fitness.
     * @param phenotype Individual
     * @return Objective
     */
    @Override
    public double calculateScore(MLMethod phenotype) {
        DoubleArrayGenome genome = (DoubleArrayGenome) phenotype;

        double[] weights = genome.getData();

        return getFitness(weights);
    }

    /**
     * Specifies the objective
     * @return True to minimize, false to maximize.
     */
    @Override
    public boolean shouldMinimize() {
        return true;
    }

    /**
     * Specifies the threading approach.
     * @return True to use single thread, false for multiple threads
     */
    @Override
    public boolean requireSingleThreaded() {
        return true;
    }

    /**
     * Objective function
     * @param x Domain parameter.
     * @return y
     */
    protected int f(int x) {
        return (x - 3)*(x - 3);
    }

    /**
     * Main function, randomizes interneuron weights and outputs weights with values
     * @return null
     */
    public static void main(String[] args) {
        double[] ws = new double[NUM_WEIGHTS];
        String[] values = {"w1", "w2", "w3", "w4", "w5", "w6", "w7", "w8", "b1", "b2"};

        for (int i=0; i<ws.length; i++) {
            ws[i] = getRandomWeight();
        }

        System.out.println(TEAM);

        for (int z=0; z<NUM_WEIGHTS; z++) {
            System.out.printf("%3s : %6.3f", values[z], ws[z]);
            System.out.println();
        }

        XorObjective objective = new XorObjective();
        int n = 1;
        System.out.println(" # x1     x2     x3     t1     y1");
        for (int y=0; y<XOR_INPUTS.length; y++) {
            double x1 = XOR_INPUTS[y][0];
            double x2 = XOR_INPUTS[y][1];
            double x3 = XOR_INPUTS[y][2];
            double y1 = feedforward(x1, x2, x3, ws);

            System.out.printf("%2d %1.4f %1.4f %1.4f %1.4f %1.4f \n", n, x1, x2, x3, XOR_IDEALS[y][0], y1);
            n++;
        }

        double fit = getFitness(ws);
        System.out.println("Fitness: " + fit);
    }
}
