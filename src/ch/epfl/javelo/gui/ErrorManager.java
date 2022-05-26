package ch.epfl.javelo.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * ErrorManager manages the display of error messages
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public final class ErrorManager {
    private final Pane pane = new Pane();
    private final VBox vBox;
    private final Text text;
    private final SequentialTransition sequentialTransition;
    private final int FADE_START_DURATION = 200;
    private final int FADE_END_DURATION = 500;
    private final int PAUSE_DURATION = 2000;
    private final double MIN_FADE_VALUE = 0;
    private final double MAX_FADE_VALUE = 0.8;


    /**
     * Default ErrorManager constructor
     */
    public ErrorManager() {
        pane.setMouseTransparent(true);

        text = new Text();
        vBox = new VBox(text);
        vBox.getStylesheets().add("error.css");

        pane.getChildren().add(vBox);

        FadeTransition fadeStart = new FadeTransition(new Duration(FADE_START_DURATION), vBox);
        fadeStart.setFromValue(MIN_FADE_VALUE);
        fadeStart.setToValue(MAX_FADE_VALUE);

        PauseTransition pause = new PauseTransition(new Duration(PAUSE_DURATION));

        FadeTransition fadeEnd = new FadeTransition(new Duration(FADE_END_DURATION), vBox);
        fadeEnd.setFromValue(MAX_FADE_VALUE);
        fadeEnd.setToValue(MIN_FADE_VALUE);

        sequentialTransition = new SequentialTransition(fadeStart, pause, fadeEnd);
    }


    /**
     * Returns JavaFX panel (vBox) containing the display of error messages
     *
     * @return JavaFX panel (vBox)
     */
    public Pane pane() {
        return vBox;
    }


    /**
     * Displays temporarily the error message on the screen
     *
     * @param errorMessage a short error message
     */
    public void displayError(String errorMessage) {

        //java.awt.Toolkit.getDefaultToolkit().beep(); // sound indicating th error (disabled)

        text.setText(errorMessage);
        sequentialTransition.stop();
        vBox.setVisible(true);
        sequentialTransition.play();
        sequentialTransition.setOnFinished(e -> vBox.setVisible(false));
    }
}
