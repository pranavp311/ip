package kopi;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/** Starts the Kopi JavaFX interface. */
public class Main extends Application {
    /** Shows the application window. */
    @Override
    public void start(Stage stage) {
        stage.setTitle("Kopi");
        stage.setScene(new Scene(new Label("Hello! I'm Kopi."), 400, 600));
        stage.show();
    }
}
