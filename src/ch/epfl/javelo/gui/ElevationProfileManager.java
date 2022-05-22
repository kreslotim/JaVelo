package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
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
    private final Pane pane;
    private final Path grid;
    private final Text stats;
    private final Group labels;
    private final Polygon polygon;
    private final Line highlightLine;
    private final BorderPane mainPane;
    private final int KILOMETER = 1000;
    private final Insets insets = new Insets(10, 10, 20, 40);
    private final int[] ELE_STEPS = { 5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000 };
    private final int[] POS_STEPS = { 1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000 };
    private final List<Double> profilesList = new ArrayList<>();
    private final ReadOnlyDoubleProperty highlightProperty;
    private final ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty;
    private final DoubleProperty mousePositionOnProfileProperty = new SimpleDoubleProperty();
    private final ObjectProperty<Rectangle2D> rectangleProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<Transform> screenToWorldProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<Transform> worldToScreenProperty = new SimpleObjectProperty<>();

    /**
     * Default ElevationProfileManager constructor
     *
     * @param elevationProfileProperty JavaFX property containing the elevation profile along of a route
     * @param highlightProperty        JavaFX property containing the highlighted position on the route
     */
    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty,
                                   ReadOnlyDoubleProperty highlightProperty) {
        this.elevationProfileProperty = elevationProfileProperty;
        this.highlightProperty = highlightProperty;

        rectangleProperty.set(Rectangle2D.EMPTY);
        screenToWorldProperty.set(new Affine());
        worldToScreenProperty.set(new Affine());
        mousePositionOnProfileProperty.set(Double.NaN);

        grid = new Path();
        stats = new Text();
        labels = new Group();
        polygon = new Polygon(); // 0,500, 100, 300, 500, 100, 700, 500
        highlightLine = new Line(0, 0, 0, 0);
        pane = new Pane(grid, labels, polygon, highlightLine);

        bindRectangle();
        setupTransformations();
        bindHighlightLineProperties();

        VBox vBox = new VBox(stats);
        vBox.setId("profile_data");
        grid.setId("grid");
        polygon.setId("profile");
        mainPane = new BorderPane(pane, null, null, vBox, null);
        mainPane.getStylesheets().add("elevation_profile.css");

        installListenersAndEventHandler();
    }


    /**
     * Returns the JavaFX panel containing the elevation profile along of a route, represented by a polygone,
     * labeled statistics of the route and elevation, and grid in the background
     *
     * @return the main panel mainPane
     */
    public Pane pane() {
        return mainPane;
    }


    /**
     * Returns a read-only property containing the position (double) of the mouse pointer along the elevation profile
     * (in meters, rounded to the nearest integer), or NaN if the mouse pointer is not above the profile
     *
     * @return read-only property containing the position of the mouse on the profile
     */
    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {
        return mousePositionOnProfileProperty;
    }


    /**
     * Auxiliary (private) method drawing the elevation profile along of a route,
     * represented by a JavaFX polygone, build with a list of consecutive (double) values X followed by Y
     * of each point.
     */
    private void drawPolygone() {

        profilesList.clear();

        Rectangle2D rectangle = rectangleProperty.get();

        for (int x = (int) rectangle.getMinX(); x <= rectangle.getMaxX(); x++) { //TODO go until 589, OK ?

            double worldPositionX = screenToWorldProperty.get().transform(x,0).getX();

            double y = worldToScreenProperty.get().transform(worldPositionX,
                    elevationProfileProperty.get().elevationAt(worldPositionX)).getY();

            //System.out.println("x: "+x); //TODO Ok if NaN at the beginning ?
            //System.out.println("y: "+y);

            profilesList.add((double)x);
            profilesList.add(y);
        }

        profilesList.add(rectangle.getMaxX()); // first X
        profilesList.add(rectangle.getMaxY()); // first Y

        profilesList.add(rectangle.getMinX()); // last X
        profilesList.add(rectangle.getMaxY()); // last Y

        polygon.getPoints().setAll(profilesList);

    }


    /**
     * Auxiliary (private) method setting up the screen-to-world and world-to-screen JavaFX transformations
     */
    private void setupTransformations() {

        if (elevationProfileProperty.get() != null) {

            Affine affine = new Affine();

            affine.prependTranslation(-rectangleProperty.get().getMinX(), -rectangleProperty.get().getMinY());

            double scaleFactorX = elevationProfileProperty.get().length() / rectangleProperty.get().getWidth();
            double scaleFactorY = (elevationProfileProperty.get().minElevation()
                    - elevationProfileProperty.get().maxElevation()) / rectangleProperty.get().getHeight();

            affine.prependScale(scaleFactorX, scaleFactorY);
            affine.prependTranslation(0, elevationProfileProperty.get().maxElevation());

            screenToWorldProperty.set(affine);

            try { worldToScreenProperty.set(affine.createInverse()); }
            catch (NonInvertibleTransformException e) { e.printStackTrace(); }
        }
    }

    /**
     * Auxiliary (private) method binding the highlight line properties
     * (line representing the highlighted position, along the elevation profile)
     */
    private void bindHighlightLineProperties() {


        highlightLine.layoutXProperty().bind(Bindings.createDoubleBinding(() -> worldToScreenProperty.get()
                        .transform(highlightProperty.get(), 0).getX(),
                highlightProperty, worldToScreenProperty)); //TODO OK to use .get() property everytime ?


        highlightLine.startYProperty().bind(Bindings.select(rectangleProperty, "minY"));

        highlightLine.endYProperty().bind(Bindings.select(rectangleProperty, "maxY"));

        highlightLine.visibleProperty().bind(highlightProperty.greaterThanOrEqualTo(0)); //TODO mouseProperty ?


    }


    /**
     * Auxiliary (private) method binding the rectangle (JavaFX Rectangle) width and height property,
     * containing the elevation profile, to the panel (JavaFX Pane) width and height properties,
     * containing the grid, the labels and the highlight line in addition to the elevation profile
     */
    private void bindRectangle() {

        rectangleProperty.bind(Bindings.createObjectBinding(() -> {
                    double minX = insets.getLeft();
                    double minY = insets.getTop();
                    double width = pane.getWidth() - insets.getLeft() - insets.getRight();
                    double height = pane.getHeight() - insets.getBottom() - insets.getTop();

                    if (width < 0) width = 0.0;
                    if (height < 0) height = 0.0;

                    return new Rectangle2D(minX, minY, width, height);
                },
                pane.widthProperty(),
                pane.heightProperty()));
    }


    /**
     * Auxiliary (private) method setting up the grid (JavaFX Path) superimposed on the elevation profile,
     * composed of horizontal and vertical lines (JavaFX PathElement)
     */
    private void setupGrid() {

        labels.getChildren().clear();
        grid.getElements().clear();

        ElevationProfile elevationProfile = elevationProfileProperty.get();
        Transform worldToScreenTransform = worldToScreenProperty.get();

        //CHOOSING RELEVANT HORIZONTAL STEP
        int horizontalStepChosen = POS_STEPS[POS_STEPS.length-1];
        for (int horizontalStep : POS_STEPS) {
            int HORIZONTAL_SPACING = 50;
            if (worldToScreenTransform.deltaTransform(horizontalStep, 0).getX() >= HORIZONTAL_SPACING) {
                horizontalStepChosen = horizontalStep;
                break;
            }
        }

        //BUILD VERTICAL LINES
        for (int step = 0; step < elevationProfile.length(); step += horizontalStepChosen) {

            Point2D startLinePoint = worldToScreenTransform.transform(step, elevationProfile.minElevation());
            Point2D endLinePoint = worldToScreenTransform.transform(step, elevationProfile.maxElevation());

            MoveTo moveTo = new MoveTo(startLinePoint.getX(), startLinePoint.getY());
            LineTo lineTo = new LineTo(endLinePoint.getX(), endLinePoint.getY());

            grid.getElements().add(moveTo);
            grid.getElements().add(lineTo);



            //LABELING
            setLabels(step, startLinePoint, "horizontal");
        }

        //CHOOSING RELEVANT VERTICAL STEP
        int verticalStepChosen = ELE_STEPS[ELE_STEPS.length-1];
        for (int verticalStep : ELE_STEPS) {
            int VERTICAL_SPACING = 25;
            if (worldToScreenTransform.deltaTransform(0, -verticalStep).getY() >= VERTICAL_SPACING) {
                verticalStepChosen = verticalStep;
                break;
            }
        }


        int initialElevation = (int)Math.ceil(
                elevationProfile.minElevation() / verticalStepChosen) * verticalStepChosen;

        //BUILD HORIZONTAL LINES
        for (int step = initialElevation; step < elevationProfile.maxElevation(); step += verticalStepChosen) {

            Point2D startLinePoint = worldToScreenTransform.transform(0, step);
            Point2D endLinePoint = worldToScreenTransform.transform(elevationProfile.length(), step);

            MoveTo moveTo = new MoveTo(startLinePoint.getX(), startLinePoint.getY());
            LineTo lineTo = new LineTo(endLinePoint.getX(), endLinePoint.getY());

            grid.getElements().add(moveTo);
            grid.getElements().add(lineTo);


            //LABELING
            setLabels(step, startLinePoint, "vertical");
        }
    }


    /**
     * Auxiliary (private) method setting up the labels, which values are associated with each of the lines of the grid
     * and are given in the margin of the elevation profile graph,
     * at the bottom for the position, on the left for the altitude.
     *
     * @param step           (in meters) by which the horizontal or vertical lines are delimited
     * @param startLinePoint starting point of each horizontal or vertical line
     * @param axis           horizontal or vertical axis
     */
    private void setLabels(int step, Point2D startLinePoint, String axis) {

        Text label = new Text(String.valueOf(step));
        label.setFont(Font.font("Avenir", 10));

        double DISPLAY_POSITION_OFFSET;
        if (axis.equals("horizontal")) {
            label.setText(String.valueOf(step/KILOMETER));
            label.setTextOrigin(VPos.TOP);
            DISPLAY_POSITION_OFFSET = label.prefWidth(0)/2;
        }
        else { // if axis is vertical
            label.setTextOrigin(VPos.CENTER);
            DISPLAY_POSITION_OFFSET = label.prefWidth(0) + 2;
        }


        label.setX(startLinePoint.getX() - DISPLAY_POSITION_OFFSET);
        label.setY(startLinePoint.getY());
        label.getStyleClass().addAll("grid_label", axis);
        labels.getChildren().add(label);
    }


    /**
     * Auxiliary (private) method setting up the route statistics, presented at the bottom of the panel
     */
    private void setupStats() {
        stats.textProperty().set(String.format("Length : %.1f km" +
                                          "     Ascent : %.0f m" +
                                          "     Descent : %.0f m" +
                                          "     Elevation : from %.0f m to %.0f m",
                elevationProfileProperty.get().length() / KILOMETER, elevationProfileProperty.get().totalAscent(),
                elevationProfileProperty.get().totalDescent(), elevationProfileProperty.get().minElevation(),
                elevationProfileProperty.get().maxElevation()));
    }


    /**
     * Auxiliary (private) method installing all the listeners implemented on the appropriate properties,
     * and event handler implemented on the panel (JavaFX Pane), updating the property containing the position (double)
     * of the mouse pointer along the elevation profile, in the screen coordinate system
     */
    private void installListenersAndEventHandler() {

        elevationProfileProperty.addListener((p,o,n) -> {

            if (n != null) {
                setupTransformations();
                drawPolygone();
                setupGrid();
                setupStats();
            }

        });

        rectangleProperty.addListener((p,o,n) -> setupTransformations());

        worldToScreenProperty.addListener((p,o,n) -> {
            drawPolygone();
            setupGrid();
        });


        pane.setOnMouseMoved(e -> {

            Point2D mouse = new Point2D(e.getX(), e.getY());

            mousePositionOnProfileProperty.set(
                    rectangleProperty.get().contains(mouse)
                            ? screenToWorldProperty.get().transform(mouse).getX()
                            : Double.NaN);

        });

        pane.setOnMouseExited(e -> mousePositionOnProfileProperty.set(Double.NaN));
    }
}
