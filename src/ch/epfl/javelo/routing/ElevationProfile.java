package ch.epfl.javelo.routing;


import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;

import java.util.DoubleSummaryStatistics;
import java.util.function.DoubleUnaryOperator;

/**
 * The profile along a single route or multiple routes
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public final class ElevationProfile {
    private final double length;
    private final float[] elevationSamples;
    private final double minElevation;
    private final double maxElevation; // total ascent decent
    private final double totalAscent;
    private final double totalDescent;
    private final DoubleUnaryOperator profile;

    /**
     * Construct the profile of a route of length (in meters) and whose elevation samples, uniformly distributed along
     * the route, are contained in elevationSamples
     *
     * @param length           length in meters
     * @param elevationSamples the elevation samples, evenly distributed along the route, are contained in
     *                         elevationSamples
     * @throws IllegalArgumentException ((length <= 0) || (elevationSamples.length < 2))
     */
    public ElevationProfile(double length, float[] elevationSamples) {
        Preconditions.checkArgument((length > 0) && (elevationSamples.length >= 2));

        this.length = length;
        this.elevationSamples = elevationSamples;

        DoubleSummaryStatistics s = new DoubleSummaryStatistics();
        for (float elevationSample : elevationSamples) {
            s.accept(elevationSample);
        }
        minElevation = s.getMin();
        maxElevation = s.getMax(); //initialize in the constructor

        double tmpTotalAscent = 0.0;
        double tmpTotalDescent = 0.0;

        for (int i = 1; i < elevationSamples.length; ++i) {// elevationSamples = 0 1 2 3 4 length = 5
            if (elevationSamples[i] > elevationSamples[i - 1]) {
                tmpTotalAscent += (elevationSamples[i] - elevationSamples[i - 1]);
            } else {
                tmpTotalDescent += Math.abs(elevationSamples[i] - elevationSamples[i - 1]);
            }
        }
        totalAscent = tmpTotalAscent;
        totalDescent = tmpTotalDescent;
        // Explication : https://piazza.com/class/kzifjghz6po4se?cid=405
        // 10 20 30 => totalAscent = 20 car de 10 à 20, j'ai +10, et de 20 à 30, j'ai +10. Donc
        // 10 20 30 => totalDescent = 0
        // 30 20 40 => totalAscent = 20 car 40-20 = 20
        // 30 20 40 => totalDescent = 10 car valeur absolue (20 - 30) = 10

        profile = Functions.sampled(elevationSamples, length());

    }


    // les methodes publiques :

    /**
     * Return the profile length (in meters)
     *
     * @return the profile length (in meters)
     */
    public double length() {
        return length;
    }

    public double minElevation() {
        return minElevation;
    }

    public double maxElevation() {
        return maxElevation;
    }

    public double totalAscent() {
        return totalAscent;
    }

    public double totalDescent() {
        return totalDescent;
    }

    /**
     * @param position
     * @return profile altitude at the given position
     */
    public double elevationAt(double position) {
        return profile.applyAsDouble(position);// return the corresponding y
    }

}