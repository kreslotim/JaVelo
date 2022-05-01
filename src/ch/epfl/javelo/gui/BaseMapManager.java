package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import java.io.IOException;

/**
 * BaseMapManager manages the display and interaction with the basemap
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public final class BaseMapManager {
    private final TileManager tileManager;
    private final ObjectProperty<MapViewParameters> mapViewParametersProperty;
    private final ObjectProperty<Point2D> point2DProperty = new SimpleObjectProperty<>();
    private boolean redrawNeeded;

    private final Canvas canvas;
    private final Pane baseMapPane;
    private MapViewParameters mapViewParameters;

    private final int TILE_SIDE_PIXELS = 256;


    /**
     * Default BaseMapManager constructor
     *
     * @param tileManager that gets the tiles from the map
     * @param waypointsManager gets the waypoints
     */
    public BaseMapManager(TileManager tileManager, WaypointsManager waypointsManager,
                          ObjectProperty<MapViewParameters> mapViewParametersProperty) {

        this.tileManager = tileManager;
        this.mapViewParametersProperty = mapViewParametersProperty;

        baseMapPane = new Pane();
        canvas = new Canvas();
        setUpPaneAndCanvas();

        mapViewParametersProperty.addListener((property, oldS, newS) -> redrawOnNextPulse());

        baseMapPane.setOnScroll(scrollEvent -> {
            System.out.println("SETONSCROLL");

            mapViewParameters = mapViewParametersProperty.get();

            int newZoom = Math2.clamp(8, (int) (mapViewParameters.zoomLevel() + Math.signum(scrollEvent.getDeltaY())), 19);

            double cursorX = scrollEvent.getX();
            double cursorY = scrollEvent.getY();

            PointWebMercator pointCursor = mapViewParameters.pointAt(cursorX, cursorY);

            int newUpLeftX = (int) (pointCursor.xAtZoomLevel(newZoom) - cursorX);
            int newUpLeftY = (int) (pointCursor.yAtZoomLevel(newZoom) - cursorY);

            mapViewParametersProperty.setValue(new MapViewParameters(newZoom,
                    newUpLeftX,
                    newUpLeftY));
        });

        baseMapPane.setOnMouseClicked(e -> {
            if (e.isStillSincePress()) waypointsManager.addWaypoint(e.getX(), e.getY());
        });

        baseMapPane.setOnMousePressed(e -> {
            Point2D pressedPoint = new Point2D(e.getX(), e.getY());
            point2DProperty.setValue(pressedPoint);
        });

        baseMapPane.setOnMouseDragged(e -> {

            mapViewParameters = mapViewParametersProperty.get();

            Point2D previousPoint = point2DProperty.get();
            Point2D newPoint = new Point2D(e.getX(), e.getY());

            Point2D shiftMap = previousPoint.subtract(newPoint);

            double newUpLeftX = mapViewParameters.mapTopLeftPositionX() + shiftMap.getX();
            double newUpLeftY = mapViewParameters.mapTopLeftPositionY() + shiftMap.getY();

            MapViewParameters newMapViewParameters = mapViewParameters.withMinXY(newUpLeftX, newUpLeftY);

            mapViewParametersProperty.setValue(newMapViewParameters);
            point2DProperty.setValue(newPoint);
        });


        baseMapPane.setOnMouseReleased(e -> point2DProperty.setValue(null));
    }

    /**
     * Returns JavaFX panel displaying the basemap.
     *
     * @return JavaFX panel
     */
    public Pane pane() {return baseMapPane;}

    /**
     * Auxiliary (private) method drawing the full map
     */
    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;

        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

        mapViewParameters = mapViewParametersProperty.get();

        double mapViewTopLeftX = mapViewParameters.mapTopLeftPositionX(); // in pixels
        double mapViewTopLeftY = mapViewParameters.mapTopLeftPositionY(); // in pixels
        int zoom = mapViewParameters.zoomLevel();

        int xStart = (int) (mapViewTopLeftX / TILE_SIDE_PIXELS);                      // starting index X
        int yStart = (int) (mapViewTopLeftY / TILE_SIDE_PIXELS);                      // starting index Y
        int xEnd = (int) ((mapViewTopLeftX + canvas.getWidth()) / TILE_SIDE_PIXELS);  // ending index X
        int yEnd = (int) ((mapViewTopLeftY + canvas.getHeight()) / TILE_SIDE_PIXELS); // ending index Y

        for (int y = yStart; y <= yEnd; y++) {
            for (int x = xStart; x <= xEnd; x++) {

                TileManager.TileId tileId = new TileManager.TileId(zoom, x, y);
                Image tileImage = null;

                try {
                    tileImage = tileManager.imageForTileAt(tileId);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                graphicsContext.drawImage(tileImage,
                        TILE_SIDE_PIXELS * x - mapViewTopLeftX,
                        TILE_SIDE_PIXELS * y - mapViewTopLeftY);
            }
        }
    }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    private void setUpPaneAndCanvas() {
        baseMapPane.setPrefSize(600,300);
        baseMapPane.getChildren().addAll(canvas);

        canvas.widthProperty().bind(baseMapPane.widthProperty());
        canvas.heightProperty().bind(baseMapPane.heightProperty());

        canvas.widthProperty().addListener(o -> redrawOnNextPulse());
        canvas.heightProperty().addListener(o -> redrawOnNextPulse());

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });
    }

}
