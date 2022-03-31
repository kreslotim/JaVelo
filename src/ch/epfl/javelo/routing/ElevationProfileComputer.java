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
    private ElevationProfileComputer() {
    }

    /**
     * Return the profile along the road route, ensuring that the spacing between profile samples is at most
     *
     * @param route         the route of a profil
     * @param maxStepLength the maximum spacing between samples
     * @return the profile along the road route, ensuring that the spacing between profile samples is at most
     * maxStepLength meters
     * @throws IllegalArgumentException if this spacing between samples(i.e. the length between each profile sample) is less or equal to 0
     */
    public static ElevationProfile elevationProfile(Route route, double maxStepLength) {
        Preconditions.checkArgument(maxStepLength > 0);
        int samplesNb = (int) (Math.ceil(route.length() / maxStepLength)) + 1;
        double distBetweenTwoProfiles = route.length() / (samplesNb - 1);
        float[] elevationSamples = new float[samplesNb];
        for (int i = 0; i < samplesNb; ++i) {
            elevationSamples[i] = (float) route.elevationAt(distBetweenTwoProfiles * i);
        }


        // case when only NaN
        for (int i = 1; i < samplesNb; ++i) {
            if (Float.isNaN(elevationSamples[0]) && Float.isNaN(elevationSamples[samplesNb - 1])) {
                Arrays.fill(elevationSamples, 0, samplesNb, 0f);
            }
        }

        //begin
        for (int i = 1; i < samplesNb; ++i) {
            if (!Float.isNaN(elevationSamples[i]) && Float.isNaN(elevationSamples[0])) {
                Arrays.fill(elevationSamples, 0, i, elevationSamples[i]);
            }
        }

        //end
        for (int i = samplesNb - 2; i > 0; --i) {
            if (!Float.isNaN(elevationSamples[i]) && Float.isNaN(elevationSamples[samplesNb - 1])) {
                Arrays.fill(elevationSamples, i, samplesNb, elevationSamples[i]);
            }
        }

        int indexJustBeforeNaNBegin = 0;
        int indexNaNFinal = 0;
        //interpolate intermediate
        for (int i = 0; i < samplesNb; ++i) {

            if (Float.isNaN(elevationSamples[i])) {
                indexJustBeforeNaNBegin = i - 1;
                while (Float.isNaN(elevationSamples[i])) {
                    ++i;
                }
                indexNaNFinal = i - 1;
                for (int k = 1; k <= indexNaNFinal - indexJustBeforeNaNBegin; ++k) {
                    elevationSamples[indexJustBeforeNaNBegin + k] = (float) Math2.interpolate(elevationSamples[indexJustBeforeNaNBegin], elevationSamples[indexNaNFinal + 1], (double) k / (double) (indexNaNFinal - indexJustBeforeNaNBegin + 1)); // the nb of interval between two non-NaN values = (indexNaNFinal - indexJustBeforeNaNBegin + 1)
                }
            }
        }
        return new ElevationProfile(route.length(), elevationSamples);
    }
}