/*
 Copyright (c) Ron Coleman

 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:

 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package javaai.ann.output;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javaai.util.Helper;

/**
 * This class tests equilateral decoding tolerance for the iris data set.
 * @author Ron.Coleman
 */
public class EquilateralDecoding {
    /** Number of tests to run */
    public final static int NUM_TESTS = 100;

    /** Tolerance as a percent, e.g., 1.0 == 1% */
    public final static double TOLERANCE = 50.46;

    /** TODO: Copy in the values from running EquilateralEncoding */
    static double ideals[][] = {
            {-0.866, -0.5},   // Virginica
            {0.866, -0.5},   // Setosa
            {0.0, 1.0}    // Versicolor
    };

    /** Specie names -- order MUST correspond to ideals */
    static final List<String> species =
            new ArrayList<>(Arrays.asList("virginica", "setosa", "versicolor"));

    /**
     * Launch point for program.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        EquilateralEncoding.load();

        Random ran = new Random(0);

        int success = 0;

        for(int n=0; n < NUM_TESTS; n++) {
            // Pick a species randomly
            int actualIndex = ran.nextInt(ideals.length);

            // TODO:
            // 1. Get a random encoding with the actual index.
            // 2. Create a new array of activations perturbed by the tolerance divided by 100.
            // 3. Decode these perturbed activations.
            // 4. If the predicted index equals the actual index, update success count.

            double[] encodings = ideals[actualIndex];
            double[] activations = new double[encodings.length];

            for (int k=0; k<encodings.length; k++) {
                double epsilon = 1 + ran.nextGaussian() * TOLERANCE / 100;
                activations[k] = encodings[k] * epsilon;
            }

            int predictedIndex = EquilateralEncoding.eq.decode(activations);
            if (actualIndex == predictedIndex) {
                System.out.println("Actual: " + species.get(actualIndex) + Helper.asString(ideals[actualIndex]) + " Predicted: " + species.get(predictedIndex) + Helper.asString(ideals[predictedIndex]));
                success++;
            }
            else {
                System.out.println("Actual: " + species.get(actualIndex) + Helper.asString(ideals[actualIndex]) + " Predicted: " + species.get(predictedIndex) + Helper.asString(ideals[predictedIndex]) + " MISSED!");
            }
        }

        double rate = (double)success / NUM_TESTS;

        System.out.printf("%d of %d or %4.2f%% tolerance = %5.2f%%\n",success, NUM_TESTS, rate, TOLERANCE);
    }


}
