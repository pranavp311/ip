package kopi;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/** Displays one message beside its speaker's image. */
public class DialogBox extends HBox {
    private static final String USER_STYLE = "-fx-background-color: #d8ead2; -fx-background-radius: 8;"
            + " -fx-padding: 10;";
    private static final String KOPI_STYLE = "-fx-background-color: #efe1d1; -fx-background-radius: 8;"
            + " -fx-padding: 10;";

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        dialog.setText(text);
        displayPicture.setImage(image);
        dialog.setMinHeight(Region.USE_PREF_SIZE);
        HBox.setHgrow(dialog, Priority.ALWAYS);
    }

    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }

    /** Creates a dialog aligned for the user. */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.dialog.setStyle(USER_STYLE);
        return dialogBox;
    }

    /** Creates a dialog aligned for Kopi. */
    public static DialogBox getKopiDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.dialog.setStyle(KOPI_STYLE);
        dialogBox.flip();
        return dialogBox;
    }
}
