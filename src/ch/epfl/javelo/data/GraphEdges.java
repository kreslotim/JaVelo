package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * Recorded class representing the array of all edges of the JaVelo graph
 *
 * @author Timofey Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {

    private static final int EDGEBUFFER_INITIATION = 10;
    private static final int OFFSET_INIT = 4;
    private static final short OFFSET_LENGTH = 2;
    private static final short OFFSET_ELEVATIONGAIN = 2;

    /* four kinds of profileType in the switch */
    private static final int NONEXISTENT_PROFILE = 0;
    private static final int UNCOMPRESSED_PROFILE = 1;
    private static final int COMPRESSED_PROFILE_Q4_4 = 2;
    private static final int COMPRESSED_PROFILE_Q0_4 = 3;


    /**
     * Determine whether the edge with the given identity goes in the opposite direction to the OSM channel it comes from
     *
     * @param edgeId Edge's Identity
     * @return true iff edgeId goes in the opposite direction to the OSM edge it comes from
     */
    public boolean isInverted(int edgeId) { //todo : longueur est UQ... donc Short.toUnsignedInt(s) ?
        return edgesBuffer.getInt(edgeId * EDGEBUFFER_INITIATION) < 0;
    }

    /**
     * Return the identity of the destination node of the given identity edge
     *
     * @param edgeId Edge's Identity
     * @return the identity of the destination node of the given identity edge
     */
    public int targetNodeId(int edgeId) {//todo : longueur est UQ... donc Short.toUnsignedInt(s) ?
        return (isInverted(edgeId) ? ~(edgesBuffer.getInt(edgeId * EDGEBUFFER_INITIATION)) :
                edgesBuffer.getInt(edgeId * EDGEBUFFER_INITIATION));
    }

    /**
     * Return the length, in meters, of the given identity edge
     *
     * @param edgeId Edge's Identity
     * @return the length (in meters) of the given identity edge
     */
    public double length(int edgeId) {
        short s = edgesBuffer.getShort(edgeId * EDGEBUFFER_INITIATION + OFFSET_INIT);
        return Q28_4.asDouble(Short.toUnsignedInt(s));
    }

    /**
     * Return the positive elevation, in meters, of the edge with the given identity
     *
     * @param edgeId Edge's Identity
     * @return the elevation gain, in meters, of the edge with the given identity
     */
    //todo : return 0 if negative value ? idk donc cree une Preconditions pour l'instant
    public double elevationGain(int edgeId) {
        Preconditions.checkArgument(edgeId >= 0);
        short s = edgesBuffer.getShort(edgeId * EDGEBUFFER_INITIATION + (OFFSET_INIT + OFFSET_LENGTH));
        return Q28_4.asDouble(Short.toUnsignedInt(s));
    }

    /**
     * Determine whether the given identity edge has a profile
     *
     * @param edgeId Edge's Identity
     * @return true iff the given identity edge has a profile
     */
    public boolean hasProfile(int edgeId) {
        int slice = profileIds.get(edgeId);
        return Bits.extractUnsigned(slice, 30, 2) > 0; // for format U31
    }

    /**
     * Return the array of samples of the profile of the given identity edge
     *
     * @param edgeId Edge's Identity
     * @return the array of samples of the profile of the edge with the given identity, which is empty if the edge does
     * not have a profile
     */
    public float[] profileSamples(int edgeId) {
        Preconditions.checkArgument(edgeId >= 0);

        int sampleNb = computeSampleNb(edgeId);
        int profileType = Bits.extractUnsigned(profileIds.get(edgeId), 30, 2);
        int firstIndex = Bits.extractUnsigned(profileIds.get(edgeId), 0, 30);
        float currentAltitude = 0; // initialize at 0 to prevent NullPointerException

        switch (profileType) {
            case NONEXISTENT_PROFILE:
                return new float[0];

            case UNCOMPRESSED_PROFILE:
                float[] profileType1 = new float[sampleNb];
                for (int i = 0; i < sampleNb; ++i) {
                    profileType1[i] = Q28_4.asFloat(elevations.get(firstIndex + i));
                }
                return (isInverted(edgeId) ? flip(sampleNb, profileType1) : profileType1);

            case COMPRESSED_PROFILE_Q4_4:
                float[] profileType2 = new float[sampleNb];
                profileType2[0] = Q28_4.asFloat(elevations.get(firstIndex));
                currentAltitude = profileType2[0];
                for (int i = 1; i <= Math.ceil((sampleNb - 1) / 2.0); ++i) {
                    for (int j = 1; j >= 0; --j) {
                        if (2 * i - j < sampleNb) {
                            currentAltitude += Q28_4.asFloat(Bits.extractSigned(elevations.get(firstIndex + i), 8 * j,
                                    8));
                            profileType2[2 * i - j] = currentAltitude;
                        }
                    }
                }
                return (isInverted(edgeId) ? flip(sampleNb, profileType2) : profileType2);

            case COMPRESSED_PROFILE_Q0_4:
                float[] profileType3 = new float[sampleNb];
                profileType3[0] = Q28_4.asFloat(elevations.get(firstIndex));
                currentAltitude = profileType3[0];
                for (int i = 1; i <= Math.ceil((sampleNb - 1) / 4.0); ++i) {
                    for (int j = 3; j >= 0; --j) {
                        if ((4 * i - j) < sampleNb) {
                            currentAltitude += Q28_4.asFloat(Bits.extractSigned(elevations.get(firstIndex + i),
                                    4 * j, 4));
                            profileType3[4 * i - j] = currentAltitude;
                        }
                    }
                }
                return (isInverted(edgeId) ? flip(sampleNb, profileType3) : profileType3);
        }
        return null;
    }

    /**
     * Compute the number of samples of the profile of the edge according to its length
     *
     * @param edgeId Edge's Identity
     * @return the number of samples
     */
    private int computeSampleNb(int edgeId) {
        //todo : besoin de Short.toUnsignedInt(...) pour l ?
        int l = Short.toUnsignedInt(edgesBuffer.getShort(edgeId * EDGEBUFFER_INITIATION + OFFSET_INIT));
//        int l = edgesBuffer.getShort(edgeId * EDGEBUFFER_INITIATION + OFFSET_INIT); // l is a short, so l is an int with 16 0s in the heighest weight
        return 1 + Math2.ceilDiv(l, Q28_4.ofInt(2)); // the formula to compute the sample number
    }

    /**
     * Flip the order of elements of the given array
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

        // todo @Tim : la raison pour laquelle j'ai cree ce for loop = c pas necessaire, mais qd on lit le code, on aime pas return tabAyantLeMmNomQueCeluiQuiEstDansLaListeDesParams;
        // to copy the values of the array :
        float[] tabInverted = new float[profileType.length];
        //todo : change the for loop to         System.arraycopy(profileType, 0, tabInverted, 0, profileType.length);      ?
        for (int i = 0; i < profileType.length; ++i) {
            tabInverted[i] = profileType[i];
        }
        return tabInverted;
    }

    /**
     * Return the identity of the attribute set attached to the given identity edge
     *
     * @param edgeId Edge's Identity
     * @return the identity of the attribute set attached to the given identity edge
     */
    public int attributesIndex(int edgeId) {
        short s = edgesBuffer.getShort(edgeId * EDGEBUFFER_INITIATION + (OFFSET_INIT + OFFSET_LENGTH
                + OFFSET_ELEVATIONGAIN));
        return Short.toUnsignedInt(s);
    }
}
