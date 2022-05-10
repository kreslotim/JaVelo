package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

import java.util.ArrayList;
import java.util.List;

/**
 * ElevationProfileManager manages the display and interaction with the profile along of a route
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public final class ElevationProfileManager {
    private final ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty;
    private final ReadOnlyDoubleProperty highlightProperty;
    private final ObjectProperty<Transform> screenToWorldProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<Transform> worldToScreenProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<Rectangle2D> rectangleProperty = new SimpleObjectProperty<>();
    private final BorderPane mainPain;
    private final Line highlightLine;
    private final VBox vBox;
    private final Pane pane;
    private final Insets insets = new Insets(10, 10, 20, 40);


    private final List<Double> profilesPolygone = new ArrayList<>();


    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty,
                                   ReadOnlyDoubleProperty highlightProperty) {
        this.elevationProfileProperty = elevationProfileProperty;
        this.highlightProperty = highlightProperty;

        //rectangleProperty.setValue(new Rectangle2D(0,displayProperty.get().minElevation(), displayProperty.get().length(), displayProperty.get().maxElevation()));
        rectangleProperty.setValue(new Rectangle2D(0,0, elevationProfileProperty.get().length(), elevationProfileProperty.get().maxElevation()));




        Path grid = new Path();
        Group labels = new Group();
        Polygon profileGraph = new Polygon(); // 0,500, 100, 300, 500, 100, 700, 500


        highlightLine = new Line(0, 0, 1, 1);
        Text stats = new Text();



        pane = new Pane(grid, labels, profileGraph, highlightLine);
        vBox = new VBox(stats);
        mainPain = new BorderPane(pane, null, null, vBox, null);

        mainPain.getStylesheets().add("elevation_profile.css");
        vBox.setId("profile_data");
        grid.setId("grid");
        profileGraph.setId("profile");


        setupRectangleProfile();
        setupProperties();
        //displayProperty.setValue(null); //TODO How to set values, if ReadOnly ?



        profilesPolygone.add(0, 40.0);
        profilesPolygone.add(1, -20-rectangleProperty.get().getHeight());

        for (double x = rectangleProperty.get().getMinX() + 40; x < rectangleProperty.get().getMaxX(); x++) {

            System.out.println("x: "+x);

            double realPosition = screenToWorldProperty.get().transform(x,0).getX();

            double y = worldToScreenProperty.get().transform(realPosition, elevationProfileProperty.get().elevationAt(realPosition)).getY();

            System.out.println("y: "+y);

            profilesPolygone.add(x);
            profilesPolygone.add(y);
        }

        profilesPolygone.add(elevationProfileProperty.get().length());
        profilesPolygone.add(0.0);
        profileGraph.getPoints().setAll(profilesPolygone);
    }

    public Pane pane() {
        return mainPain;
    }

    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {
        return null;
    }

    private void setupRectangleProfile() {
        Affine screenToWorld = new Affine();

        screenToWorld.prependTranslation(-rectangleProperty.get().getMinX(), -rectangleProperty.get().getMinY());

        double scaleFactorX = elevationProfileProperty.get().length() / (rectangleProperty.get().getWidth());

        double scaleFactorY = (elevationProfileProperty.get().minElevation() - elevationProfileProperty.get().maxElevation())
                              / (rectangleProperty.get().getHeight());

        screenToWorld.prependScale(scaleFactorX, scaleFactorY);

        screenToWorld.prependTranslation(0, elevationProfileProperty.get().maxElevation());

        screenToWorldProperty.setValue(screenToWorld);

        try { worldToScreenProperty.setValue(screenToWorld.createInverse()); }
        catch (NonInvertibleTransformException e) { e.printStackTrace(); }
    }

    private void setupProperties() {

        highlightLine.layoutXProperty().bind(Bindings.createDoubleBinding(() -> {

            double xTransformed = worldToScreenProperty.get().transform(highlightProperty.get(), 0).getX();

            return xTransformed;

        }, highlightProperty, worldToScreenProperty));


        highlightLine.startYProperty().bind(Bindings.select(rectangleProperty, "minY"));

        highlightLine.endYProperty().bind(Bindings.select(rectangleProperty, "maxY"));

        highlightLine.visibleProperty().bind(highlightProperty.greaterThanOrEqualTo(0)); //TODO mouseProperty ?
    }
}
