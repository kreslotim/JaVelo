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

    /* Edge's attributes are distributed over 80 bits in total = 4 Bytes + 3 * 2 Bytes */
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

        // todo : les variables pour type2 et type3 :  //from here
        // "Les différences (d'altitude par rapport à leur prédécesseur) sont ensuite empaquetées dans des valeurs de 16 bits, contenant chacune 2 (type 2) ou 4 (type 3) différences."
        int altitudeDifference = (type == ProfileType.COMPRESSED_PROFILE_Q0_4) ? 2 : 4;
        // todo : le code marche, mais c'est moche. J'utilise lequel ? :
//        int extractSignedLength = (type == ProfileType.COMPRESSED_PROFILE_Q0_4) ? (int) OFFSET_OSM_ATTRIBUTES : OFFSET_LENGTH; // 8 : 4
        int extractSignedLength = (type == ProfileType.COMPRESSED_PROFILE_Q0_4) ? Byte.SIZE : Byte.SIZE / 2; // 8 : 4
        // raison : "les échantillons suivants sont représentés par la différence d'altitude par rapport à leur prédécesseur, représentée soit par une valeur de 8 bits au format Q4.4 (type 2), soit par une valeur de 4 bits au format Q0.4 (type 3)."
        //todo : to here

        switch (type) {
            case NONEXISTENT_PROFILE:
                return new float[]{};

            case UNCOMPRESSED_PROFILE:
                for (int i = 0; i < sampleNb; ++i) {
                    samples[i] = Q28_4.asFloat(Short.toUnsignedInt(elevations.get(firstIndex + i)));
                }
                break;

            case COMPRESSED_PROFILE_Q0_4, COMPRESSED_PROFILE_Q4_4:
                samples[0] = Q28_4.asFloat(elevations.get(firstIndex));
                currentAltitude = samples[0];
                for (int i = 1; i <= Math.ceil((sampleNb - 1) / (double) altitudeDifference); ++i) {
                    for (int j = altitudeDifference - 1; j >= 0; --j) {
                        if ((altitudeDifference * i - j) < sampleNb) {
                            currentAltitude += Q28_4.asFloat(Bits.extractSigned(elevations.get(firstIndex + i),
                                    extractSignedLength * j, extractSignedLength));
                            samples[altitudeDifference * i - j] = currentAltitude;
                        }
                    }
                }
                break;
        }
        if (isInverted(edgeId)) flip(samples);

        return samples;
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
     * @param profileType the array before flipping the order of elements
     */
    private void flip(float[] profileType) {
        int tabInverted = profileType.length;
        for (int i = 0; i < tabInverted / 2; ++i) {
            float temp = profileType[i];
            profileType[i] = profileType[tabInverted - i - 1];
            profileType[tabInverted - i - 1] = temp;
        }
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