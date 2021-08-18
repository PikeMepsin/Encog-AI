package javaai.ann.input;

import javaai.util.Helper;
import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.arrayutil.NormalizedField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * This class normalizes the real-world iris data and gives its report.
 * @author Ron.Coleman
 */
public class Normalize1 extends Main {
    /** The high range index */
    public final static int HI = 0;

    /** The low range index */
    public final static int LO = 1;

    /** Normalized data stored here; reals are instantiated by Main. */
    protected static HashMap<String, List<Double>> normals = new HashMap<>();

    /**
     * Launches the program.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        // Load the iris data into reals
        load();

        // Get the titles of real data
        Set<String> titles = reals.keySet();

        // Normalize reals by title
        for(String title: titles) {
            // Get the reals for this title
            List<Double> list = reals.get(title);

            // Get the range for this title
            double[] range = getRange(list);

            System.out.printf("%-12s: %6.2f - %5.2f\n", title, range[HI], range[LO]);

            // TODO: create a NormalizedField instance using the hi-lo range; see SimpleNormalize.java
            NormalizedField norm = new NormalizedField(NormalizationAction.Normalize,
                    null, range[HI],range[LO],1,-1);

            // Contains normalized data
            List<Double> normalized = new ArrayList<>();

            // TODO: normalize each value in the reals list and add to normalized; see SimpleNormalize.java
            for (int i=0; i<reals.get(title).size(); i++) {
                double normies = norm.normalize(reals.get(title).get(i));
                normalized.add(normies);
            }

            // Add normalized data to the normals for this title
            normals.put(title, normalized);
        }

        // Write rest of the report
        System.out.printf("%3s ","#");
        
        for(String key: titles)
            System.out.printf("%15s ",key);
            
        System.out.println();

        int size =  Helper.getRowCount();

        for(int k=0; k < size; k++) {
            System.out.printf("%3d ",k);
            
            for(String key: titles) {
                Double real = reals.get(key).get(k);
                
                Double normal = normals.get(key).get(k);

                System.out.printf("%6.2f => %5.2f ",real,normal);
            }
            
            System.out.println();
        }

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
}
