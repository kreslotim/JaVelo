package ch.epfl.javelo.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

public final class ErrorManager {
    private final Pane pane = new Pane();
    private final VBox vBox;
    private final Text text;
    private final SequentialTransition sequentialTransition;

    public ErrorManager() {
        pane.setMouseTransparent(true);


        text = new Text();
        vBox = new VBox(text);
        vBox.getStylesheets().add("error.css");

        pane.getChildren().add(vBox);

        FadeTransition fadeStart = new FadeTransition(new Duration(200), vBox);
        fadeStart.setFromValue(0);
        fadeStart.setToValue(0.8);

        PauseTransition pause = new PauseTransition(new Duration(2000));

        FadeTransition fadeEnd = new FadeTransition(new Duration(500), vBox);
        fadeEnd.setFromValue(0.8);
        fadeEnd.setToValue(0);

        sequentialTransition = new SequentialTransition(fadeStart, pause, fadeEnd);
    }

    public Pane pane() {
        return vBox;
    }

    public void displayError(String errorMessage) {

        //java.awt.Toolkit.getDefaultToolkit().beep();

        text.setText(errorMessage);
        sequentialTransition.stop();
        vBox.setVisible(true);
        sequentialTransition.play();
        sequentialTransition.setOnFinished(e -> vBox.setVisible(false));
    }
}
