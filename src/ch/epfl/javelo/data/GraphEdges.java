package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.List;

/**
 * Recorded class representing the array of all edges of the JaVelo graph
 *
 * @author Timofey Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {

    private static final int EDGE_PITCH = Integer.BYTES + 3 * Short.BYTES;
    private static final int OFFSET_LENGTH = Integer.BYTES;
    private static final short OFFSET_ELEVATION_GAIN = OFFSET_LENGTH + Short.BYTES;
    private static final short OFFSET_OSM_ATTRIBUTES = OFFSET_ELEVATION_GAIN + Short.BYTES;
    private static final List<ProfileType> PROFILES = List.of(ProfileType.values());

    /**
     * Determines whether the edge with the given identity goes in the opposite direction of the OSM way,
     * from where it comes from.
     *
     * @param edgeId Edge's Identity
     * @return true iff edgeId goes in the opposite direction of the OSM edge
     */
    public boolean isInverted(int edgeId) {
        return edgesBuffer.getInt(edgeId * EDGE_PITCH) < 0;
    }

    /**
     * Returns the identity of the target node of the given identity edge
     *
     * @param edgeId Edge's Identity
     * @return the identity of the target node of the given identity edge
     */
    public int targetNodeId(int edgeId) {
        return (isInverted(edgeId) ? ~(edgesBuffer.getInt(edgeId * EDGE_PITCH)) :
                edgesBuffer.getInt(edgeId * EDGE_PITCH));
    }

    /**
     * Returns the length, in meters, of the given identity edge
     *
     * @param edgeId Edge's Identity
     * @return the length (in meters) of the given identity edge
     */
    public double length(int edgeId) {
        short s = edgesBuffer.getShort(edgeId * EDGE_PITCH + OFFSET_LENGTH);
        return Q28_4.asDouble(Short.toUnsignedInt(s));
    }

    /**
     * Returns the positive elevation, in meters, of the edge with the given identity
     *
     * @param edgeId Edge's Identity
     * @return the elevation gain, in meters, of the edge with the given identity
     */
    public double elevationGain(int edgeId) {
        short s = edgesBuffer.getShort(edgeId * EDGE_PITCH + OFFSET_ELEVATION_GAIN);
        return Q28_4.asDouble(Short.toUnsignedInt(s));
    }

    /**
     * Determines whether the given identity edge has a profile
     *
     * @param edgeId Edge's Identity
     * @return a boolean value : true if and only if the given identity edge has a profile
     */
    public boolean hasProfile(int edgeId) {
        int slice = profileIds.get(edgeId);
        return Bits.extractUnsigned(slice, 30, 2) > 0; // for format U31
    }

    /**
     * Returns the array of the given identity edge's profile's samples
     *
     * @param edgeId Edge's Identity
     * @return the edge profile samples array with the given identity
     */
    public float[] profileSamples(int edgeId) {
        int profileType = Bits.extractUnsigned(profileIds.get(edgeId), 30, 2);
        ProfileType type = PROFILES.get(profileType);
        int sampleNb = computeSampleNb(edgeId);
        int firstIndex = Bits.extractUnsigned(profileIds.get(edgeId), 0, 30);
        float currentAltitude;
        float[] samples = new float[sampleNb];

        switch (type) {
            case NONEXISTENT_PROFILE:
                return new float[]{};

            case UNCOMPRESSED_PROFILE:
                for (int i = 0; i < sampleNb; ++i) {
                    samples[i] = Q28_4.asFloat(Short.toUnsignedInt(elevations.get(firstIndex + i)));
                }
                break;

            case COMPRESSED_PROFILE_Q0_4:
                samples[0] = Q28_4.asFloat(elevations.get(firstIndex));
                currentAltitude = samples[0];
                for (int i = 1; i <= Math.ceil((sampleNb - 1) / 2.0); ++i) {
                    for (int j = 1; j >= 0; --j) {
                        if (2 * i - j < sampleNb) {
                            currentAltitude += Q28_4.asFloat(Bits.extractSigned(elevations.get(firstIndex + i),
                                    8 * j, 8));
                            samples[2 * i - j] = currentAltitude;
                        }
                    }
                }
                break;

            case COMPRESSED_PROFILE_Q4_4:
                samples[0] = Q28_4.asFloat(elevations.get(firstIndex));
                currentAltitude = samples[0];
                for (int i = 1; i <= Math.ceil((sampleNb - 1) / 4.0); ++i) {
                    for (int j = 3; j >= 0; --j) {
                        if ((4 * i - j) < sampleNb) {
                            currentAltitude += Q28_4.asFloat(Bits.extractSigned(elevations.get(firstIndex + i),
                                    4 * j, 4));
                            samples[4 * i - j] = currentAltitude;
                        }
                    }
                }
                break;
        }
        return (isInverted(edgeId) ? flip(sampleNb, samples) : samples);
    }

    /**
     * Computes the number of samples of the edge profile according to its length
     *
     * @param edgeId Edge's Identity
     * @return the number of samples
     */
    private int computeSampleNb(int edgeId) {
        int l = Short.toUnsignedInt(edgesBuffer.getShort(edgeId * EDGE_PITCH + OFFSET_LENGTH));
        return 1 + Math2.ceilDiv(l, Q28_4.ofInt(2)); // the formula to compute the sample number
    }

    /**
     * Auxiliary (private) method that flips the order of elements inside given array of profile types,
     * if the edge is inverted.
     *
     * @param sampleNb    the number of samples
     * @param profileType the array before flipping the order of elements
     * @return the array after flipping the order of elements of the original array (tab)
     */
    private float[] flip(int sampleNb, float[] profileType) {
        // to flip the array :

        for (int i = 0; i < sampleNb / 2; ++i) {
            float tmp = profileType[i];
            profileType[i] = profileType[profileType.length - i - 1];
            profileType[profileType.length - i - 1] = tmp;
        }

        float[] tabInverted = new float[profileType.length];
        System.arraycopy(profileType, 0, tabInverted, 0, profileType.length);
        return tabInverted;
    }

    /**
     * Returns the index of an attribute set attached to the given edge's identity
     *
     * @param edgeId Edge's Identity
     * @return the identity of the attribute set attached to the given edge's identity
     */
    public int attributesIndex(int edgeId) {
        short s = edgesBuffer.getShort(edgeId * EDGE_PITCH + OFFSET_OSM_ATTRIBUTES);
        return Short.toUnsignedInt(s);
    }


    /**
     * Four profile types that can be used for the given edge's identity profile samples
     */
    private enum ProfileType {NONEXISTENT_PROFILE, UNCOMPRESSED_PROFILE, COMPRESSED_PROFILE_Q0_4, COMPRESSED_PROFILE_Q4_4}
}