package ch.epfl.javelo;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.gui.*;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.CostFunction;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.function.Consumer;

public final class Stage9Test extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Graph graph = Graph.loadFrom(Path.of("lausanne"));
        Path cacheBasePath = Path.of(".");
        String tileServerHost3 = "tile.openstreetmap.org";
        String tileServerHost1 = "cartodb-basemaps-1.global.ssl.fastly.net/dark_all";
        String tileServerHost = "tile.memomaps.de/tilegen";
        String tileServerHost2 = "c.tile-cyclosm.openstreetmap.fr/cyclosm";
        // https://{s}.tile-cyclosm.openstreetmap.fr/cyclosm/19/271725/185422.png

        CostFunction cf = new CityBikeCF(graph);
        RouteComputer rc = new RouteComputer(graph, cf);
        RouteBean routeBean = new RouteBean(rc);
        routeBean.setHighlightedPositionProperty(1000);

        TileManager tileManager = new TileManager(cacheBasePath, tileServerHost);

        MapViewParameters mapViewParameters =
                new MapViewParameters(12, 543200, 370650);
        ObjectProperty<MapViewParameters> mapViewParametersP = new SimpleObjectProperty<>(mapViewParameters);


        Consumer<String> errorConsumer = new ErrorConsumer();

        WaypointsManager waypointsManager =
                new WaypointsManager(graph,
                        mapViewParametersP,
                        routeBean.getWaypoints(),
                        errorConsumer);
        BaseMapManager baseMapManager =
                new BaseMapManager(tileManager,
                        waypointsManager,
                        mapViewParametersP);

        RouteManager routeManager =
                new RouteManager(routeBean,
                        mapViewParametersP);

        StackPane mainPane =
                new StackPane(baseMapManager.pane(),
                        waypointsManager.pane(),
                        routeManager.pane());

        mainPane.getStylesheets().add("map.css");
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(300);
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();
    }

    private static final class ErrorConsumer
            implements Consumer<String> {
        @Override
        public void accept(String s) { System.out.println(s); }
    }
}