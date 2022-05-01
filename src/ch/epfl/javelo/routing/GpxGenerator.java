package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Locale;

/**
 * Route Generator in GPX format
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public class GpxGenerator {
    /**
     * Default (not instantiable) GpxGenerator constructor
     */
    private GpxGenerator() {}

    /**
     * Writes the corresponding GPX document into the given file
     *
     * @param fileName name of the given file (to write in)
     * @param route JaVelo route
     * @param profileRoute route's profile
     * @throws IOException if the expected file does not exist
     */
    public static void writeGpx(String fileName, Route route, ElevationProfile profileRoute) throws IOException {
        Document doc = createGpx(route, profileRoute);
        Writer w = new FileWriter(fileName);
        try {
            Transformer transformer = TransformerFactory
                    .newDefaultInstance()
                    .newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(w));
        } catch (TransformerException transformerException) {
            throw new Error(transformerException);
        }
    }

    /**
     * Returns the corresponding GPX document, to the given Route and profile of the route.
     *
     * @param route JaVelo route
     * @param profileRoute route's profile
     * @return GPX document
     */
    public static Document createGpx(Route route, ElevationProfile profileRoute) {

        Document doc = newDocument(); // see below

        Element root = doc.createElementNS("http://www.topografix.com/GPX/1/1", "gpx");
        doc.appendChild(root);

        root.setAttributeNS(
                "http://www.w3.org/2001/XMLSchema-instance",
                "xsi:schemaLocation",
                "http://www.topografix.com/GPX/1/1 " + "http://www.topografix.com/GPX/1/1/gpx.xsd");
        root.setAttribute("version", "1.1");
        root.setAttribute("creator", "JaVelo");

        Element metadata = doc.createElement("metadata");
        root.appendChild(metadata);

        Element name = doc.createElement("name");
        metadata.appendChild(name);
        name.setTextContent("Route JaVelo");

        // fais le miroir pour l'element rte :
        Element rte = doc.createElement("rte");
        root.appendChild(rte);

        // list de Edges
        List<Edge> edgesList = route.edges();
        double position = 0;

        int countNode = 0;
        // list de pointCh
        List<PointCh> pointChList = route.points();
        for (int i = 0; i < pointChList.size(); ++i) {
            Element rtePt = doc.createElement("rtept");
            rte.appendChild(rtePt);
            rtePt.setAttribute("lon", String.format(Locale.ROOT, "%.5f", Math.toDegrees(pointChList.get(i).lon())));
            rtePt.setAttribute("lat", String.format(Locale.ROOT, "%.5f", Math.toDegrees(pointChList.get(i).lat())));
            Element ele = doc.createElement("ele");
            rtePt.appendChild(ele);
            ele.setTextContent(String.format(Locale.ROOT, "%.2f", profileRoute.elevationAt(position)));

            if (i < edgesList.size()) {
                position += edgesList.get(i).length();
            }

            countNode++;
        }

        System.out.println("Nodes number : "+countNode);
        return doc;
    }

    /**
     * @return
     */
    private static Document newDocument() {
        try {
            return DocumentBuilderFactory
                    .newDefaultInstance()
                    .newDocumentBuilder()
                    .newDocument();
        } catch (ParserConfigurationException e) {
            throw new Error(e); // Should never happen
        }
    }

}
