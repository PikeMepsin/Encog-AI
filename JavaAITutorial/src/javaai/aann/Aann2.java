package javaai.aann;

import java.lang.ref.PhantomReference;
import java.util.*;

import static javaai.aann.Measure.*;

public class Aann2 extends Aann1 {
    public static void main(String[] args) {
        load();

        HashMap<Measure,Species> cam = new HashMap<>();

        List<Measure> tests = new ArrayList<>(Arrays.asList(
                new Measure(5.0, 3.5, 1.6, .6),
                new Measure(5.1, 3.6, 1.5, .4),
                new Measure(0.0, 0.0, 0.0, 0.0),
                new Measure(100.0, 100.0, 100.0, 100.0),
                new Measure(-1.0, -1.0, -1.0, -1.0)
        ));

        /* List<Measure> tests = new ArrayList<>(Arrays.asList(
            measures.get(0),
            measures.get(1),
            measures.get(2),
            measures.get(3),
            measures.get(4)));
        */

        for (int k=0; k<measures.size(); k++) {
            cam.put(measures.get(k), flowers.get(k));
        }

        for (Measure measure: tests) {
            System.out.println(get(measure, cam) + " " + measure);
        }
    }

    public static Species get(Measure target, HashMap<Measure, Species> cam) {
        double minDist = Double.MAX_VALUE;
        Measure minMeasure = measures.get(0);

        for (Measure measure: measures) {
            double distance = target.getDistanceTo(measure);

            if (distance <= minDist) {
                minDist = distance;
                minMeasure = measure;
            }
        }

        return cam.get(minMeasure);
    }
}
