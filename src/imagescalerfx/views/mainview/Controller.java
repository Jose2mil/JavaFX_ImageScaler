package imagescalerfx.views.mainview;

import imagescalerfx.utils.*;
import javafx.collections.FXCollections;
import javafx.concurrent.ScheduledService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * Class that controls the main view.
 * @author Jose Valera
 * @version 1.0
 * @since 14/11/2020
 */
public class Controller implements Initializable {
    @FXML
    private Button buttonChart;
    @FXML
    private ListView<ImageData> listViewScaledInstances;
    @FXML
    private ListView<ImageData> listViewImages;
    @FXML
    private Button buttonStart;
    @FXML
    private ImageView imageViewSelectedImage;
    @FXML
    private Label labelStatus;

    private StatusService statusService;
    static private List<ThreadScaler> threadsScalers = new ArrayList<>();

    /**
     * Initialize the view by creating the Service status and loading
     * the necessary controls.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        statusService = new StatusService(
                            null,
                            labelStatus,
                            new Control[]{buttonStart, buttonChart},
                            Duration.ZERO,
                            Duration.seconds(1));

        if(threadsScalers.size() == 0)
            buttonChart.setDisable(true);

        else
            listViewImages.getItems()
                          .addAll(threadsScalers.stream()
                                                .map(ThreadScaler::getImage).collect(Collectors.toList()));
    }

    /**
     * Returns a list of the threads performed in the last operation.
     * @return Threads performed in the last operation.
     */
    static public List<ThreadScaler> getThreadsScalers() {
        return threadsScalers;
    }

    @FXML
    private void StartScales(ActionEvent event) {
        if(threadsScalers.size() == 0 ||
                ( threadsScalers.size() > 0 &&
                MessageUtils.showConfirmation(
                    "Do you want to scale again?",
                    " You will delete the information from the listings") == ButtonType.OK) ) {
            File selectedDirectory =
                    new DirectoryChooser().showDialog(
                            ((Node) event.getSource()).getScene().getWindow());

            if (selectedDirectory != null) {
                SetExitDialog(event);

                List<ThreadScaler> threadScalersAux =
                        IOUtils.getImages(selectedDirectory.toPath())
                                .stream().map(p -> new ThreadScaler(p, listViewImages))
                                .collect(Collectors.toList());

                if (threadScalersAux.size() == 0)
                    MessageUtils.showError(
                            "No compatible format images found in directory",
                            "Select another directory");

                else {
                    threadsScalers = threadScalersAux;
                    prepareControlsForScale();
                    scaleImages();
                }
            }
        }
    }

    private void scaleImages() {
        ThreadPoolExecutor executor = (ThreadPoolExecutor)
                Executors.newFixedThreadPool(
                        Runtime.getRuntime().availableProcessors());

        threadsScalers.stream().forEach(executor::execute);
        statusService.setNewExecutor(executor);

        executor.shutdown();
        statusService.restart();
    }

    private void prepareControlsForScale() {
        buttonStart.setDisable(true);
        buttonChart.setDisable(true);
        listViewImages.getItems().clear();
        listViewScaledInstances.getItems().clear();
        imageViewSelectedImage.setImage(null);
    }

    @FXML
    private void ShowListViewScaledInstances() {
        ImageData imageData = listViewImages.getSelectionModel().getSelectedItem();

        if(imageData != null) {
            String folderPath =
                    imageData.getPath().toString()
                             .substring(0, imageData.getPath().toString().lastIndexOf('.'));
            listViewScaledInstances.setItems(
                    FXCollections.observableList(
                            IOUtils.getImages(Path.of(folderPath))));
        }
    }

    @FXML
    private void ShowScaledImage() {
        ImageData imageData = listViewScaledInstances.getSelectionModel().getSelectedItem();

        if(imageData != null) {
            imageViewSelectedImage.setImage(
                    new Image(
                            new File(
                                    imageData.getPath().toString()).toURI().toString()) );
        }
    }

    @FXML
    private void goToChartView(ActionEvent event) {
        SceneLoader.goToView(event,
                "/imagescalerfx/views/chartview/chart.fxml",
                true);
    }

    private void SetExitDialog(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setOnCloseRequest(e -> {
            boolean exit = false;

            if(statusService != null && statusService.isRunning())
                MessageUtils.showError(
                        "Scale processes are in progress.",
                        "Wait for them to complete...");

            else
                exit = MessageUtils.showConfirmation(
                        "Do you want to exit the application?",
                        "") == ButtonType.OK;

            if(!exit)
                e.consume();
        });
    }
}
