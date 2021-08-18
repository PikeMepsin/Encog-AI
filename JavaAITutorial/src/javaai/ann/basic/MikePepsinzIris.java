package javaai.ann.basic;

import javaai.util.Helper;
import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.mathutil.Equilateral;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.arrayutil.NormalizedField;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import static javaai.ann.input.Normalize1.HI;
import static javaai.ann.input.Normalize1.LO;

import static javaai.util.Helper.headers;
import static javaai.ann.output.Ontology.parsers;

public class MikePepsinzIris {
    /** Error tolerance */
    public final static double TOLERANCE = 0.01;
    public static final double TRAINING_PERCENT = 0.8;


    public static double IRIS_TRAINING_INPUTS[][];
    public static double IRIS_TRAINING_IDEALS[][];

    public static double IRIS_TESTING_INPUTS[][];
    public static double IRIS_TESTING_IDEALS[][];

    public static Equilateral eq;
    public static List<String> subtypes;

    /**
     * The input necessary for XOR.
     */
    public static double XOR_INPUTS[][] = {
            {0.0, 0.0},
            {0.0, 1.0},
            {1.0, 0.0},
            {1.0, 1.0}
    };

    /**
     * The ideal data necessary for XOR.
     */
    public static double XOR_IDEALS[][] = {
            {0.0},
            {1.0},
            {1.0},
            {0.0}};

    public static int getTrainingStart() {
        return 0;
    }

    public static int getTrainingEnd() {
        return (int)(Helper.getRowCount() * TRAINING_PERCENT + .5 - 1.0);
    }

    public static int getTestingStart() {
        return (int)(Helper.getRowCount() * TRAINING_PERCENT + .5);
    }

    public static int getTestingEnd() {
        return Helper.getRowCount() - 1;
    }


    /**
     * Gets the hi-lo range for a list.
     * @param list List
     * @return 2-tuple of doubles for hi and low
     */
    protected static double[] getRange(List<Double> list) {
        // Current high and low values so far
        double[] range = {-Double.MAX_VALUE, Double.MAX_VALUE};

        // Go through each value in the list
        for(Double value: list) {
            // TODO: if value above current high, update range[HI]
            if (value > range[HI]) {
                range[HI] = value;
            }
            // TODO: if value below current low, update range[LO].
            if (value < range[LO]) {
                range[LO] = value;
            }
        }

        return range;
    }

    protected static double[][] getInputs() {
        HashMap<String, List<Double>> normals = new HashMap<>();

        for (String title : headers) {
            List list_ = Helper.data.get(title);

            if (list_ == null || list_.isEmpty() || !(list_.get(0) instanceof Double)) {
                continue;
            }
            List<Double> list = (List<Double>) list_;

            double[] range = getRange((List<Double>) list);

            NormalizedField norm = new NormalizedField(NormalizationAction.Normalize, null, range[HI], range[LO], 1, -1);

            List<Double> normalized = new ArrayList<>();

            for (Double value : list) {
                Double normed = norm.normalize(value);
                normalized.add(normed);
            }

            normals.put(title, normalized);
        }


        // keySet returns an Object array, not a String array
        Object[] objs = normals.keySet().toArray();
        // Transfer header names to String array--we use it later
        String[] cols = new String[objs.length];
        System.arraycopy(objs, 0, cols, 0, objs.length);
        // Allocate the storage
        int numRows = Helper.getRowCount();
        int numCols = cols.length;
        double[][] inputs = new double[numRows][numCols];

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                String title = cols[col];
                inputs[row][col] = normals.get(title).get(row);
            }
        }

        return inputs;
    }

    protected static void init() {
        try {
            Helper.loadCsv("iris.csv", parsers);

            MikePepsinzIris mi = new MikePepsinzIris();

            double inputs[][] = getInputs();
            double ideals[][] = getIdeals();

            int numCols = inputs[0].length;
            int numRows = getTrainingEnd() - getTrainingStart() + 1;
            // System.out.println("numCols=" + numCols + " numRows=" + numRows);

            IRIS_TRAINING_INPUTS = new double[numRows][numCols];

            for (int r=0; r<numRows; r++) {
                for (int c = 0; c < numCols; c++) {
                    IRIS_TRAINING_INPUTS[r][c] = inputs[r][c];
                }
            }

            numCols = ideals[0].length;

            // System.out.println("numCols=" + numCols + " numRows=" + numRows);
            IRIS_TRAINING_IDEALS = new double[numRows][numCols];

            for (int r=0; r<numRows; r++) {
                for (int c=0; c<numCols; c++) {
                    IRIS_TRAINING_IDEALS[r][c] = ideals[r][c];
                }
            }

            numRows = getTestingEnd() - getTestingStart() + 1;
            numCols = inputs[0].length;
            IRIS_TESTING_INPUTS = new double[numRows][numCols];

            for (int r=0; r<numRows; r++) {
                for (int c=0; c<numCols; c++) {
                    IRIS_TESTING_INPUTS[r][c] = inputs[r+numRows-1][c];
                }
            }

            numCols = ideals[0].length;
            IRIS_TESTING_IDEALS = new double[numRows][numCols];

            for (int r=0; r<numRows; r++) {
                for (int c=0; c<numCols; c++) {
                    IRIS_TESTING_IDEALS[r][c] = ideals[r+numRows-1][c];
                }
            }

            // System.out.println();


            /*
            System.out.println("Iris Normalized Data Inputs");
            System.out.println("_________________________________");
            System.out.println("PWidth, PLength, SWidth, SLength");

            for (double[] row: inputs) {
                for (double cols: row) {
                    System.out.printf("%6.2f  ", cols);
                }
                System.out.println();5
            }
            System.out.println("Iris Encoded Data Outputs");
            System.out.println("___________________________");
            System.out.println("    x         y");

            for (double[] row: ideals) {
                for (double cols: row) {
                    System.out.printf("%7.3f   ", cols);
                }
                System.out.println();
            }*/
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static double[][] getIdeals() {
        final int SPECIES = 4;
        subtypes = Helper.getNominalSubtypes(SPECIES);

        double[][] ideals = new double[Helper.getRowCount()][];

        Integer number = 0;
        HashMap<String,Integer> subtypeToNumber = new HashMap<String,Integer>();
        for (String subtype: subtypes) {
            subtypeToNumber.put(subtype, number);
            number++;
        }

        eq = new Equilateral(subtypes.size(), 1.0, -1.0);

        String col = Helper.headers.get(SPECIES);

        for (int row=0; row<Helper.getRowCount(); row++) {
            //Get the nominal as a string
            String nominal = (String)Helper.data.get(col).get(row);

            //Convert the name string to a subspecies index number
            number = subtypeToNumber.get(nominal);
            if (number == null) {
                try {
                    throw new Exception("bad nominal: " + nominal);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //Encode the number as a 2D vertex
            double[] encoding = eq.encode(number);

            //Save the vertex encoding for the row
            ideals[row] = encoding;
        }

        return ideals;
    }

    /**
     * The main method.
     *
     * @param args No arguments are used.
     */
    public static void main(final String args[]) {
        init();

        int missed = 0;
        double[] output = new double[2];

        // Create a neural network, without using a factory
        BasicNetwork network = new BasicNetwork();

        // Add input layer with no activation function, bias enabled, and two neurons
        network.addLayer(new BasicLayer(null, true, 4));

        // Add hidden layer with ramped activation, bias enabled, and five neurons
        // NOTE: ActivationReLU is not in javadoc but can be found here http://bit.ly/2zyxk7A.
        // network.addLayer(new BasicLayer(new ActivationReLU(), true, 5));

        // Add hidden layer with sigmoid activation, bias enabled, and two neurons
        network.addLayer(new BasicLayer(new ActivationTANH(), true, 4));

        // Add output layer with sigmoid activation, bias disable, and one neuron
        network.addLayer(new BasicLayer(new ActivationTANH(), false, 2));

        // No more layers to be added
        network.getStructure().finalizeStructure();

        // Randomize the weights
        network.reset();

        // Create training data
        MLDataSet trainingSet = new BasicMLDataSet(IRIS_TRAINING_INPUTS, IRIS_TRAINING_IDEALS);

        // Train the neural network.
        // Use a training object to train the network, in this case, an improved
        // back propagation. For details on what this does see the javadoc.
        final ResilientPropagation train = new ResilientPropagation(network, trainingSet);

        int epoch = 1;

        // Never train on a specific error rate but an acceptable tolerance and
        // if the error drops below that tolerance, the network has converged.
        do {
            long then = System.nanoTime();

            train.iteration();

            long now = System.nanoTime();

            long dt = now - then;

            System.out.println("dt: "+dt+ " epoch #" + epoch + " error: " + train.getError());

            epoch++;
        } while (train.getError() > TOLERANCE);

        train.finishTraining();

        // test the neural network
        System.out.println("Neural Network Results:");

        for (MLDataPair pair : trainingSet) {

            final MLData outputs = network.compute(pair.getInput());
            /*
            System.out.println(pair.getInput().getData(0) + "," + pair.getInput().getData(1)
                    + ", actual=" + outputs.getData(0) + ",ideal=" + pair.getIdeal().getData(0));
            */
        }

        System.out.println(" #       Ideal      Actual");
        for (int k=0; k<IRIS_TESTING_INPUTS.length; k++) {
            double[] input = IRIS_TESTING_INPUTS[k];

            network.compute(input, output);

            //Decode the output to get its subtype index
            int actual = eq.decode(output);

            //Get and decode ideal to get its subtype index
            double[] ideals = IRIS_TESTING_IDEALS[k];
            int ideal = eq.decode(ideals);

            //Convert output and ideal to string species name
            String idealS = subtypes.get(ideal);
            String actualS = subtypes.get(actual);

            //If output and ideal aren't equal, increment missed
            System.out.printf("%2d %11s %11s", k+1, idealS, actualS);

            if (!idealS.equals(actualS)) {
                System.out.print(" MISSED");
                missed++;
            }
            System.out.print("\n");
        }
        int attempts = getTestingEnd() - getTestingStart() + 1;
        double successRate = (attempts -missed) / attempts * 100.0;
        System.out.println("success rate = " + (attempts-missed) + "/" + attempts + " (" + successRate + "%)");

        Encog.getInstance().shutdown();
    }
}
