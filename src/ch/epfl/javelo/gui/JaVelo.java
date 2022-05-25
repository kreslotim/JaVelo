package ch.epfl.javelo.gui;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * Main class of the JaVelo application
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public final class JaVelo extends Application {
    private final DoubleProperty highlightProperty = new SimpleDoubleProperty(Double.NaN);
    private final ObjectProperty<AnnotatedMapManager> annotatedMapManagerProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<MapViewParameters> mapViewParametersProperty = new SimpleObjectProperty<>(
                    new MapViewParameters(12, 543200, 370650));


    /**
     * Default JaVelo constructor
     */
    public JaVelo() {
    }

    /**
     * Main method that launches the JaVelo application
     *
     * @param args they are totally ignored
     */
    public static void main(String[] args) {launch(args);}


    /**
     * Builds the final graphic interface of the JaVelo application
     *
     * @param primaryStage the primary Stage is constructed by the platform (JavaFX)
     * @throws Exception   any type of exception is thrown in case of incorrect data
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        Graph graph = Graph.loadFrom(Path.of("javelo-data"));

        Path cacheBasePath = Path.of("osm-cache");
        String tileServerHost = "tile.openstreetmap.org";
        TileManager tileManager = new TileManager(cacheBasePath, tileServerHost);


        CostFunction costFunction = new CityBikeCF(graph);
        RouteComputer routeComputer = new RouteComputer(graph, costFunction);

        RouteBean routeBean = new RouteBean(routeComputer);

        ElevationProfileManager elevationProfileManager =
                new ElevationProfileManager(routeBean.elevationProfileProperty(), highlightProperty);

        Pane elevationPane = elevationProfileManager.pane();
        SplitPane.setResizableWithParent(elevationPane,false);

        ErrorManager errorManager = new ErrorManager();
        errorManager.pane().setVisible(false);
        Consumer<String> errorConsumer = errorManager::displayError;

        AnnotatedMapManager annotatedMapManager =
                new AnnotatedMapManager(graph, tileManager, routeBean, errorConsumer, mapViewParametersProperty);

        SplitPane splitPane = new SplitPane(annotatedMapManager.pane());
        StackPane stackPane = new StackPane(splitPane, errorManager.pane());


        /*************************************************************************************************************
         *                                         BINDINGS & LISTENERS                                              *
         *************************************************************************************************************/


        annotatedMapManagerProperty.addListener((p,o,n) -> {

            splitPane.getItems().set(0,n.pane());

            //Necessary to put binding inside listener for updating the appropriate binding with the corresponding
            //annotated map manager. The binding is executed only once at each creation
            //of a new annotated map manager, which seems to be reasonable
            routeBean.highlightedPositionProperty().bind(Bindings
                    .when(annotatedMapManagerProperty.get().mousePositionOnRouteProperty().greaterThanOrEqualTo(0))
                    .then(annotatedMapManagerProperty.get().mousePositionOnRouteProperty())
                    .otherwise(elevationProfileManager.mousePositionOnProfileProperty()));
        });


        highlightProperty.bind(elevationProfileManager.mousePositionOnProfileProperty());
        highlightProperty.bind(routeBean.highlightedPositionProperty());


        routeBean.elevationProfileProperty().addListener((p,o,n) -> {

            if      (o == null && n != null) splitPane.getItems().add(1, elevationPane);
            else if (o != null && n == null) splitPane.getItems().remove(1);
        });


        /*************************************************************************************************************
         *                                           PRIMARY STAGE                                                   *
         *************************************************************************************************************/

        annotatedMapManagerProperty.set(annotatedMapManager);
        splitPane.setOrientation(Orientation.VERTICAL);
        MenuBar menuBar = displayMenuBar(routeBean, graph, errorConsumer);
        menuBar.setUseSystemMenuBar(true);

        BorderPane rootPane = new BorderPane(stackPane, menuBar, null, null, null);

        primaryStage.setTitle("JaVelo");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        Scene scene = new Scene(rootPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Auxiliary (private) method that manages the Menu display (at the top of the application),
     * allowing to export an existing route in GPX format,
     * and switch between different layers pattern.
     *
     * @param  routeBean Route's Bean (JavaFX)
     * @return MenuBar - the bar (at the top of the application) containing the "File" button,
     * allowing to export an existing route in GPX format
     */
    private MenuBar displayMenuBar(RouteBean routeBean, Graph graph, Consumer<String> errorConsumer) {

        Menu fileMenu = new Menu("Fichier");
        MenuItem exportGPX_menuItem = new MenuItem("Exporter GPX");

        Menu layers = new Menu("Fonds de carte");

        MenuItem normalLayer = new MenuItem("OpenStreetMap");
        MenuItem cycleLayer = new MenuItem("Cycle-OSM");
        MenuItem darkLayer = new MenuItem("Dark Layer");
        MenuItem lightLayer = new MenuItem("Light Layer");
        MenuItem swissLayer = new MenuItem("Swiss Style");

        normalLayer.setOnAction(e -> {

            TileManager tileManager = new TileManager(Path.of("osm-cache"),
                    "tile.openstreetmap.org");

            AnnotatedMapManager annotatedMapManager =
                    new AnnotatedMapManager(graph, tileManager, routeBean, errorConsumer, mapViewParametersProperty);

            annotatedMapManagerProperty.set(annotatedMapManager);

        });


        cycleLayer.setOnAction(e -> {

            TileManager tileManager = new TileManager(Path.of("osm-cache-cycle"),
                    "c.tile-cyclosm.openstreetmap.fr/cyclosm");

            AnnotatedMapManager annotatedMapManager =
                    new AnnotatedMapManager(graph, tileManager, routeBean, errorConsumer, mapViewParametersProperty);

            annotatedMapManagerProperty.set(annotatedMapManager);

        });


        darkLayer.setOnAction(e -> {

            TileManager tileManager = new TileManager(Path.of("osm-cache-dark"),
                    "cartodb-basemaps-1.global.ssl.fastly.net/dark_all");

            AnnotatedMapManager annotatedMapManager =
                    new AnnotatedMapManager(graph, tileManager, routeBean, errorConsumer, mapViewParametersProperty);

            annotatedMapManagerProperty.set(annotatedMapManager);

        });


        lightLayer.setOnAction(e -> {

            TileManager tileManager = new TileManager(Path.of("osm-cache-light"),
                    "cartodb-basemaps-1.global.ssl.fastly.net/light_all");

            AnnotatedMapManager annotatedMapManager =
                    new AnnotatedMapManager(graph, tileManager, routeBean, errorConsumer, mapViewParametersProperty);

            annotatedMapManagerProperty.set(annotatedMapManager);

        });

        swissLayer.setOnAction(e -> {

            TileManager tileManager = new TileManager(Path.of("osm-cache-swiss-style"),
                    "tile.osm.ch/osm-swiss-style"); //tile.osm.ch/switzerland

            AnnotatedMapManager annotatedMapManager =
                    new AnnotatedMapManager(graph, tileManager, routeBean, errorConsumer, mapViewParametersProperty);

            annotatedMapManagerProperty.set(annotatedMapManager);
        });

        exportGPX_menuItem.disableProperty().bind(routeBean.routeProperty().isNull());

        exportGPX_menuItem.setOnAction(e -> {
                try {
                    GpxGenerator.writeGpx("javelo.gpx", routeBean.routeProperty().get(),
                                                     routeBean.elevationProfileProperty().get());
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
        });

        layers.getItems().add(normalLayer);
        layers.getItems().add(cycleLayer);
        layers.getItems().add(darkLayer);
        layers.getItems().add(lightLayer);
        layers.getItems().add(swissLayer);

        fileMenu.getItems().add(exportGPX_menuItem);
        return new MenuBar(fileMenu, layers);
    }
}
