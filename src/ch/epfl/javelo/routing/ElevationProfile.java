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
    private final double length;               // length of the profile
    private final double minElevation;         // minimum altitude of the profile
    private final double maxElevation;         // maximum altitude of the profile
    private final double totalAscent;          // total elevation gain of the profile
    private final double totalDescent;         // total elevation loss of the profile
    private final DoubleUnaryOperator profile; // profile

    /**
     * Constructs the profile of a route of length (in meters) and whose elevation samples, uniformly distributed along
     * the route, are contained in the elevation samples elevationSamples
     *
     * @param length           length in meters
     * @param elevationSamples the elevation samples, evenly distributed along the route, are contained in
     *                         the elevation samples elevationSamples
     * @throws IllegalArgumentException if the length (in meters) is less or equal to 0
     *                                  or the length of the the elevation samples is strictly less than 2
     */
    public ElevationProfile(double length, float[] elevationSamples) {
        Preconditions.checkArgument((length > 0) && (elevationSamples.length >= 2));

        this.length = length;

        DoubleSummaryStatistics s = new DoubleSummaryStatistics();
        for (float elevationSample : elevationSamples) {
            s.accept(elevationSample);
        }
        minElevation = s.getMin();
        maxElevation = s.getMax();

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

        profile = Functions.sampled(elevationSamples, length());
    }


    /**
     * Return the profile length along the whole profile (in meters)
     *
     * @return the profile length (in meters)
     */
    public double length() {
        return length;
    }

    /**
     * Return the minimum altitude of the profile along the whole profile (in meters)
     *
     * @return the minimum altitude of the profile, in meters
     */
    public double minElevation() {
        return minElevation;
    }

    /**
     * Return the maximum altitude of the profile along the whole profile (in meters)
     *
     * @return the maximum altitude of the profile, in meters
     */
    public double maxElevation() {
        return maxElevation;
    }

    /**
     * Return the total elevation gain of the profile along the whole profile (in meters)
     *
     * @return the total elevation gain of the profile
     */
    public double totalAscent() {
        return totalAscent;
    }

    /**
     * Return the total elevation loss of the profile along the whole profile (in meters)
     *
     * @return the total elevation loss of the profile
     */
    public double totalDescent() {
        return totalDescent;
    }

    /**
     * Returns the altitude of the profile at the given position,
     * which is not necessarily between 0 and the length of the profile;
     * the first sample is returned when the position is negative, the last when it is greater than the length
     *
     * @param  position the given position on the route
     * @return the profile's altitude at the given position
     */
    public double elevationAt(double position) {
        return profile.applyAsDouble(position);
    }

}