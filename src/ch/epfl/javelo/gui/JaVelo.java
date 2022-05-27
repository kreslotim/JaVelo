package ch.epfl.javelo.gui;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.UncheckedIOException;
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
    private final static int MIN_SCENE_WIDTH = 800;
    private final static int MIN_SCENE_HEIGHT = 600;
    private final static int ELEVATION_PANE_INDEX = 1;

    /**
     * Default JaVelo constructor
     */
    public JaVelo() {}

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
        CostFunction costFunction = new CityBikeCF(graph);
        RouteComputer routeComputer = new RouteComputer(graph, costFunction);

        TileManager tileManager = new TileManager(cacheBasePath, tileServerHost);
        RouteBean routeBean = new RouteBean(routeComputer);

        ElevationProfileManager elevationProfileManager =
                new ElevationProfileManager(routeBean.elevationProfileProperty(), highlightProperty);

        Pane elevationPane = elevationProfileManager.pane();
        SplitPane.setResizableWithParent(elevationPane,false);

        ErrorManager errorManager = new ErrorManager();

        Consumer<String> errorConsumer = errorManager::displayError;

        AnnotatedMapManager annotatedMapManager =
                new AnnotatedMapManager(graph, tileManager, routeBean, errorConsumer);

        SplitPane splitPane = new SplitPane(annotatedMapManager.pane());
        StackPane stackPane = new StackPane(splitPane, errorManager.pane());

        errorManager.pane().setVisible(false);

        /*************************************************************************************************************
         *                                         BINDINGS & LISTENERS                                              *
         *************************************************************************************************************/

        routeBean.highlightedPositionProperty().bind(Bindings
                .when(annotatedMapManager.mousePositionOnRouteProperty().greaterThanOrEqualTo(0))
                .then(annotatedMapManager.mousePositionOnRouteProperty())
                .otherwise(elevationProfileManager.mousePositionOnProfileProperty()));

        highlightProperty.bind(elevationProfileManager.mousePositionOnProfileProperty());
        highlightProperty.bind(routeBean.highlightedPositionProperty());

        routeBean.elevationProfileProperty().addListener((p,o,n) -> {

            if      (o == null && n != null) splitPane.getItems().add(ELEVATION_PANE_INDEX, elevationPane);
            else if (o != null && n == null) splitPane.getItems().remove(ELEVATION_PANE_INDEX);

        });


        /*************************************************************************************************************
         *                                           PRIMARY STAGE                                                   *
         *************************************************************************************************************/

        splitPane.setOrientation(Orientation.VERTICAL);
        MenuBar menuBar = displayMenuBar(routeBean);
        menuBar.setUseSystemMenuBar(true);
        BorderPane rootPane = new BorderPane(stackPane, menuBar, null, null, null);

        primaryStage.setTitle("JaVelo");
        primaryStage.setMinWidth(MIN_SCENE_WIDTH);
        primaryStage.setMinHeight(MIN_SCENE_HEIGHT);
        Scene scene = new Scene(rootPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Auxiliary (private) method that manages the Menu display (at the top of the application),
     * and allows to export an existing route in GPX format
     *
     * @param  routeBean Route's Bean (JavaFX)
     * @return MenuBar - the bar (at the top of the application) containing the "Fichier" button,
     * allowing to export an existing route in GPX format
     */
    private MenuBar displayMenuBar(RouteBean routeBean) {

        Menu menu = new Menu("Fichier");
        MenuItem menuItem = new MenuItem("Exporter GPX");

        menuItem.disableProperty().bind(routeBean.routeProperty().isNull());

        menuItem.setOnAction(e -> {
                try {
                    GpxGenerator.writeGpx("javelo.gpx", routeBean.routeProperty().get(),
                                                     routeBean.elevationProfileProperty().get());
                } catch (IOException exception) {
                    throw new UncheckedIOException(exception);
                }
        });

        menu.getItems().add(menuItem);
        return new MenuBar(menu);
    }
}
