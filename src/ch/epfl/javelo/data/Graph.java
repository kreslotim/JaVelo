package ch.epfl.javelo.data;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

/**
 * represents the JaVelo graph
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
//  classe publique et immuable
public class Graph {

    /**
     * @param basePath
     * @return
     * @throws IOException
     */
    public static Graph loadFrom(Path basePath) throws IOException {
//        try (InputStream s = new FileInputStream("nodes.bin")) {
//
//        }

        //todo besoin de     public static FileChannel open(Path path, OpenOption... options) pour savoir le processur pour ouvrir le fichier
        try (FileChannel.open(basePath, )) {

        }


        return (() ? : );
    }


}
