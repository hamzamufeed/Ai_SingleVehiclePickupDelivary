package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;

public class ChartController extends MainController implements Initializable{

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private LineChart<Number, Number> lineChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		lineChart.getData().clear();
		XYChart.Series series = new XYChart.Series();
		series.setName("Time");
		if(Path != null)
			for (int i = 0; i < Path.size(); i++){
				series.getData().add(new XYChart.Data(Integer.toString(Path.get(i).getRequest_number()), Path.get(i).currentTime));
			}
		lineChart.setLegendVisible(true);
		lineChart.setAxisSortingPolicy(LineChart.SortingPolicy.NONE);
		lineChart.setLegendSide(Side.TOP);
		lineChart.getData().addAll(series);
	}

}