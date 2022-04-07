package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import java.util.Arrays;

/**
 * A calculator along the profile, i.e. it contains the code to calculate the profile along of a given route
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public final class ElevationProfileComputer {
    /**
     * Default (not instantiable) ElevationProfileComputer constructor
     */
    private ElevationProfileComputer() {}

    /**
     * Return the profile along the route, ensuring that the spacing between profile samples is at most the maximum
     * spacing between samples
     *
     * @param route         the route of a profile
     * @param maxStepLength the maximum spacing between samples
     * @return the profile along the route, ensuring that the spacing between profile samples is at most maxStepLength
     * @throws IllegalArgumentException if this spacing between samples
     *                                  (i.e. the length between each profile sample) is less or equal to 0
     */
    public static ElevationProfile elevationProfile(Route route, double maxStepLength) {
        Preconditions.checkArgument(maxStepLength > 0);
        int samplesNb = (int) (Math.ceil(route.length() / maxStepLength)) + 1;
        double distBetweenTwoProfiles = route.length() / (samplesNb - 1);
        // create the array :
        float[] elevationSamples = new float[samplesNb];
        for (int i = 0; i < samplesNb; ++i) {
            elevationSamples[i] = (float) route.elevationAt(distBetweenTwoProfiles * i);
        }

        //case when there are only NaN values in the array elevationSamples : fill up the array elevationSamples with 0
        for (int i = 1; i < samplesNb; ++i) {
            if (Float.isNaN(elevationSamples[0]) && Float.isNaN(elevationSamples[samplesNb - 1])) {
                Arrays.fill(elevationSamples, 0, samplesNb, 0f);
            }
        }

        for (int i = 1; i < samplesNb; ++i) {
            //case begin : find the first valid (i.e. non-NaN value) sample in the array
            //and use it to replace all the NaN values  at the head of the array by the array elevationSamples
            if (!Float.isNaN(elevationSamples[i]) && Float.isNaN(elevationSamples[0])) {
                Arrays.fill(elevationSamples, 0, i, elevationSamples[i]);
            }
            //case end : from the end of the array elevationSamples
            //and find the first valid (i.e. non-NaN value) sample in the array
            //and use it to replace all the NaN values at the tail end of the array
            //by the array elevationSamples — which must exist.
            if (!Float.isNaN(elevationSamples[samplesNb - 1 - i]) && Float.isNaN(elevationSamples[samplesNb - 1])) {
                Arrays.fill(elevationSamples, samplesNb - 1 - i, samplesNb, elevationSamples[i]);
            }
        }

        //case intermediate : Iterate through the array elevationSamples in ascending index order to find
        //intermediate holes (i.e. NaN value) and fill them by linear interpolation from valid values
        //(i.e. non-NaN value) on both sides of the hole—which must exist.
        int indexJustBeforeNaNBegin;
        int indexNaNFinal;
        for (int i = 0; i < samplesNb; ++i) {
            if (Float.isNaN(elevationSamples[i])) {
                indexJustBeforeNaNBegin = i - 1;
                while (Float.isNaN(elevationSamples[i])) {
                    ++i;
                }
                indexNaNFinal = i - 1;
                for (int k = 1; k <= indexNaNFinal - indexJustBeforeNaNBegin; ++k) {
                    elevationSamples[indexJustBeforeNaNBegin + k] =
                            (float) Math2.interpolate(elevationSamples[indexJustBeforeNaNBegin],
                                    elevationSamples[indexNaNFinal + 1],
                                    (double) k / (double) (indexNaNFinal - indexJustBeforeNaNBegin + 1));
                    //the number of intervals between two non-NaN values = (indexNaNFinal - indexJustBeforeNaNBegin + 1)
                }
            }
        }
        return new ElevationProfile(route.length(), elevationSamples);
    }
}