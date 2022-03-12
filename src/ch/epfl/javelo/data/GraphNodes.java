package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Q28_4;

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
    private static final int OFFSET_E = 0;
    private static final int OFFSET_N = OFFSET_E + 1;
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;

    // id d'un noeud est un attribut implicite (cf §fin2.2.2)
    // les trois valeurs sont de type int


    // the following public methods, which are mainly intended to provide access to the different attributes of a node
    // whose identity is known

    /**
     * @return the total number of nodes
     */
    public int count() {
        return (buffer.capacity()) / 3;
        // capacity() \equiv length()

    }

    /**
     * @param nodeId
     * @return the E coordinate of the given identity node
     */
    public double nodeE(int nodeId) {
        return Q28_4.asDouble(buffer.get(nodeId * NODE_INTS + OFFSET_E));
        // get(i) \equiv [i]
    }

    /**
     * @param nodeId
     * @return the N coordinate of the given identity node
     */
    public double nodeN(int nodeId) {
        return Q28_4.asDouble(buffer.get(nodeId * NODE_INTS + OFFSET_N));
    }

    /**
     * @param nodeId
     * @return the number of edges exiting the given identity node
     */
    public int outDegree(int nodeId) {
        int slice = buffer.get(nodeId * NODE_INTS + OFFSET_OUT_EDGES);
        return Bits.extractUnsigned(slice, 0, 4); // for format U4
    }

    /**
     * @param nodeId
     * @param edgeIndex
     * @return the identity of the edgeIndex-th edge that exits the identity node nodeId
     */
    public int edgeId(int nodeId, int edgeIndex) {
        assert 0 <= edgeIndex && edgeIndex < outDegree(nodeId);
        int slice = buffer.get(nodeId * NODE_INTS + OFFSET_OUT_EDGES);
        return Bits.extractUnsigned(slice, 4, 28); // for format U28
    }
}
// L'identité d'un nœud JaVelo n'est rien d'autre que son index dans un grand tableau contenant la totalité des nœuds
// du graphe. Les nœuds y sont ordonnés de manière à ce que tous les nœuds se trouvant dans une zone géographique rectangulaire donnée se suivent.


// L'identité d'un nœud JaVelo n'est rien d'autre que son index dans un grand tableau contenant la totalité des nœuds
// du graphe. Les nœuds y sont ordonnés de manière à ce que tous les nœuds se trouvant dans une zone géographique rectangulaire donnée se suivent.

// §2.2.1 résumé :
// Dans le tab des secteurs,
// 1. on regarde le secteur[i] : dans le secteur[i], il y a UN int node_i ET UN short nomNode_i : UN int node_i + UN short nomNode_i = 6 bytes
// 2. int node_i pointe vers le tableau des nodes où il y a node_i.
// 3.
// 3.1. short nomNode_i = 0 ssi ce secteur n'a pas suisse ;
// 3.2. short nomNode_i = valueQuelconque =/= 0 ssi ce secteur a suisse.
// 4.
// 4.1. Comme 3.1., donc va casser le lien qui pointe vers node_i du tableau des nodes
// 4.2. Comme 3.2., donc va pointe vers node_i du tableau des nodes ET dans ce tableau, on va prendre les nodes allant de node_i à node_i+valueQuelconque : pk ? car
// les nodes sont filtrés dqans le tableau de sorte à

// le tableau (en informatique) des secteurs est de taille 128*128 \equiv un carré (avec l'imagination) qui représente la carte de la Suisse de taille 128*128 km²
// 349*221 /16384 = 2.71*1.73 km² = la taille d'un secteur de la carte de la Suisse
//   où 16384 = 128*128 = le nb de secteurs en Suisse
//   où 2.71*1.73 km² = la taille d'un secteur de la carte de la Suisse

// assert := le prgm arrete des qu'il y a une erreur