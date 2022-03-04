package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;

import java.nio.IntBuffer;

/**
 * §2.2.2
 * Recorded class representing the array of all nodes of the JaVelo graph
 *
 * @author Timofey Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */

// buffer = un tableau contenant la valeur des attributs de tous les noeuds du graphe
public record GraphNodes(IntBuffer buffer) {

    // cf §2.2.2 tableau attribut d'un noeud : une cte par attribut d'un noeud
    private static final int OFFSET_E = 0; // nodeAttribut1
    private static final int OFFSET_N = OFFSET_E + 1; // nodeAttribut2
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1; // nodeAttribut3
    // nodeAttribut4 : NODE_INTS = the number of integers needed to represent a node, which is 3
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;
    /* The constants whose name starts with OFFSET contain the position of the different attributes in a node */

    // id d'un noeud est un attribut implicite (cf §fin2.2.2)
    // les trois valeurs sont de type int


    // the following public methods, which are mainly intended to provide access to the different attributes of a node
    // whose identity is known

    /**
     * @return the total number of nodes
     */
    public int count() {
        return (buffer.capacity()) / 3;
    }

    /**
     * @param nodeId
     * @return the E coordinate of the given identity node
     */
    public double nodeE(int nodeId) { //cette meth reçoit une identité de nœud
        // compute to determine the index of the buffer element that the method should read :


    }

    /**
     * @param nodeId
     * @return the N coordinate of the given identity node
     */
    public double nodeN(int nodeId) { //cette meth reçoit une identité de nœud

    }

    /**
     * @param nodeId
     * @return the number of edges exiting the given identity node
     */
    public int outDegree(int nodeId) { //cette meth reçoit une identité de nœud

    }

    /**
     * @param nodeId
     * @param edgeIndex
     * @return the identity of the edgeIndex-th edge that exits the identity node nodeId
     */
    public int edgeId(int nodeId, int edgeIndex) { //cette meth reçoit une identité de nœud
        Preconditions.checkArgument( 0 <= edgeIndex && edgeIndex < outDegree(nodeId));
    }
}
// L'identité d'un nœud JaVelo n'est rien d'autre que son index dans un grand tableau contenant la totalité des nœuds
// du graphe. Les nœuds y sont ordonnés de manière à ce que tous les nœuds se trouvant dans une zone géographique rectangulaire donnée se suivent.