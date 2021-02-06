package imagescalerfx.utils;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Class in charge of handling between views.
 * @author Nacho Iborra (Edited by Jose Valera)
 * @version 1.0
 * @since 25/10/2020
 */
public class SceneLoader {
    private static void loadScene(String viewPath, Stage stage) throws IOException
    {
        Parent root = FXMLLoader.load(SceneLoader.class.getResource(viewPath));
        Scene view1Scene = new Scene(root);
        stage.setScene(view1Scene);
        stage.show();
    }

    /**
     * By means of the window extracted by means of an event of the
     * same and a route, a new view is shown with the value of resize that is passed to it
     * by parameter.
     * @param event Event launched in the window containing the view.
     * @param path Path of the ".fxml" file of the view to display.
     * @param resizable Boolean to determine if the next view will be resizable.
     */
    public static void goToView(ActionEvent event, String path, boolean resizable) {
        try {
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setResizable(resizable);
            loadScene(path, stage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}