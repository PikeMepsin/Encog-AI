package javaai.aann;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static javaai.aann.Measure.*;

public class Aann1 extends Main {
    public static void main(String[] args) {
        load();

        HashMap<Measure,Species> cam = new HashMap<>();

        /*
        List<Measure> tests =
            new ArrayList<>(Arrays.asList(new Measure(5.0, 3.5, 1.6, 0.6)));
        */

        for (int k=0; k<5; k++) {
            cam.put(measures.get(k), flowers.get(k));
        }

        List<Measure> tests = new ArrayList<>(Arrays.asList(
            measures.get(0),
            measures.get(1),
            measures.get(2),
            measures.get(3),
            measures.get(4)));

        measures.get(0).values[SEP_LENGTH] = 6.8;
        measures.get(0).values[SEP_WIDTH] = 2.8;
        measures.get(0).values[PET_LENGTH] = 4.8;
        measures.get(0).values[PET_WIDTH] = 1.4;

        for (Measure measure: tests) {
            System.out.println(cam.get(measure) + " " + measure);
        }
    }
}
