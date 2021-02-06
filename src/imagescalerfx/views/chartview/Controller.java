package imagescalerfx.views.chartview;

import imagescalerfx.utils.SceneLoader;
import imagescalerfx.utils.ThreadScaler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Class that controls the chart view.
 * @author Jose Valera
 * @version 1.0
 * @since 14/11/2020
 */
public class Controller implements Initializable {

    @FXML
    private BarChart barChart;

    /**
     * Initialize the view by updating the chart data.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showChart();
    }

    private void showChart(){
        List<ThreadScaler> threadScalers =
                imagescalerfx.views.mainview.Controller.getThreadsScalers();

        barChart.getData().clear();

        barChart.setTitle("Thread Performance");
        XYChart.Series chartData = new XYChart.Series();
        threadScalers.forEach(scaler -> {
            chartData.getData().add(
                    new XYChart.Data(
                            scaler.getImage().getFileName(),
                            scaler.getRequiredMilliseconds()));
        });

        barChart.getData().add(chartData);
    }

    @FXML
    private void goToMainView(ActionEvent event) {
        SceneLoader.goToView(event,
                "/imagescalerfx/views/mainview/main.fxml",
                true);
    }
}
