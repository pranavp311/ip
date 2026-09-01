package kopi;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** Starts the Kopi JavaFX interface. */
public class Main extends Application {
    /** Shows the application window. */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainWindow = loader.load();

        stage.setTitle("Kopi");
        stage.setResizable(false);
        stage.setScene(new Scene(mainWindow));
        stage.show();
    }
}
