package com.datavis;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.geometry.Point2D;
import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Text;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.*;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ChartManager {
    private ObservableList<XYChart.Series<String, Number>> barChartData;
    private ObservableList<XYChart.Series<Number, Number>> scatterPlotData;
    private ObservableList<XYChart.Series<Number, Number>> lineChartData;
    private ObservableList<PieChart.Data> pieChartData;
    private ObservableList<XYChart.Series<Number, Number>> ribbonChartData;
    private ColorPicker colorPicker;
    private VBox chartPane;

    public ChartManager() {
        colorPicker = new ColorPicker(Color.BLUE);
        barChartData = FXCollections.observableArrayList();
        scatterPlotData = FXCollections.observableArrayList();
        lineChartData = FXCollections.observableArrayList();
        pieChartData = FXCollections.observableArrayList();
        ribbonChartData = FXCollections.observableArrayList();
        chartPane = new VBox(10);
    }

    public VBox getChartPane() {
        return chartPane;
    }

    public void loadData() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            loadDataFromFile(file);
            System.out.println("Data loaded successfully from file: " + file.getName());
            displayAllCharts();
        } else {
            System.out.println("File selection canceled.");
        }
    }

    private void loadDataFromFile(File file) {
        barChartData.clear();
        scatterPlotData.clear();
        lineChartData.clear();
        pieChartData.clear();
        ribbonChartData.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String header = br.readLine();
            if (header == null || !header.contains(",")) {
                System.out.println("Invalid or empty CSV file.");
                return;
            }

            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                try {
                    if (values.length == 2) {
                        String category = values[0].trim();
                        double yValue = Double.parseDouble(values[1].trim());

                        XYChart.Series<String, Number> barSeries = getOrCreateSeries(category, barChartData);
                        barSeries.getData().add(new XYChart.Data<>(category, yValue));
                        pieChartData.add(new PieChart.Data(category, yValue));
                    } else if (values.length == 3) {
                        double xValue = Double.parseDouble(values[0].trim());
                        double yValue = Double.parseDouble(values[1].trim());
                        String category = values[2].trim();

                        XYChart.Series<Number, Number> lineSeries = getOrCreateSeries(category, lineChartData);
                        lineSeries.getData().add(new XYChart.Data<>(xValue, yValue));

                        XYChart.Series<Number, Number> scatterSeries = getOrCreateSeries(category, scatterPlotData);
                        scatterSeries.getData().add(new XYChart.Data<>(xValue, yValue));

                        XYChart.Series<Number, Number> ribbonSeries = getOrCreateSeries(category, ribbonChartData);
                        ribbonSeries.getData().add(new XYChart.Data<>(xValue, yValue));
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format in line: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void displayAllCharts() {
        chartPane.getChildren().clear();

        if (!barChartData.isEmpty()) chartPane.getChildren().add(createBarChart());
        if (!lineChartData.isEmpty()) chartPane.getChildren().add(createLineChart());
        if (!scatterPlotData.isEmpty()) chartPane.getChildren().add(createScatterPlot());
        if (!pieChartData.isEmpty()) chartPane.getChildren().add(createPieChart());
        if (!ribbonChartData.isEmpty()) chartPane.getChildren().add(createRibbonChart());
    }

    // Chart Creation Methods with Titles
    public BarChart<String, Number> createBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.getData().addAll(barChartData);
        barChart.setTitle("Bar Chart: Comparison of Categories");
        barChart.setStyle("-fx-background-color: #f5f5f5;");
        return barChart;
    }

    public LineChart<Number, Number> createLineChart() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.getData().addAll(lineChartData);
        lineChart.setTitle("Line Chart: Trend Analysis Over Time");
        lineChart.setStyle("-fx-background-color: #e8f0fe;");
        return lineChart;
    }

    public ScatterChart<Number, Number> createScatterPlot() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        ScatterChart<Number, Number> scatterChart = new ScatterChart<>(xAxis, yAxis);
        scatterChart.getData().addAll(scatterPlotData);
        scatterChart.setTitle("Scatter Plot: Data Distribution");
        scatterChart.setStyle("-fx-background-color: #fffbe6;");
        return scatterChart;
    }

public StackPane createPieChart() {
    PieChart pieChart = new PieChart(pieChartData);
    pieChart.setClockwise(true);
    pieChart.setTitle("Category Distribution");

    double total = pieChartData.stream().mapToDouble(PieChart.Data::getPieValue).sum();
    StackPane pane = new StackPane();
    pane.getChildren().add(pieChart);

    double currentAngle = 0; // Track angles for correct positioning
    for (PieChart.Data data : pieChartData) {
        double percentage = (data.getPieValue() / total) * 100;
        Text text = new Text(String.format("%.1f%%", percentage));

        // Compute slice angle
        double sliceAngle = (data.getPieValue() / total) * 360;
        double midAngle = currentAngle + (sliceAngle / 2);

        // Convert angle to (x, y) position
        double radius = 80; // Adjust radius to move labels out of center
        double x = radius * Math.cos(Math.toRadians(midAngle));
        double y = radius * Math.sin(Math.toRadians(midAngle));

        text.setTranslateX(x);
        text.setTranslateY(y);

        pane.getChildren().add(text);
        currentAngle += sliceAngle;
    }

    return pane;
}
    public LineChart<Number, Number> createRibbonChart() {
        if (ribbonChartData.isEmpty()) return null;
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<Number, Number> ribbonChart = new LineChart<>(xAxis, yAxis);
        ribbonChart.getData().addAll(ribbonChartData);
        ribbonChart.setTitle("Ribbon Chart: Layered Data Visualization");
        ribbonChart.setStyle("-fx-background-color: #fdf5e6;");
        return ribbonChart;
    }

    private <X, Y> XYChart.Series<X, Y> getOrCreateSeries(String category, ObservableList<XYChart.Series<X, Y>> chartData) {
        for (XYChart.Series<X, Y> series : chartData) {
            if (series.getName().equals(category)) {
                return series;
            }
        }
        XYChart.Series<X, Y> newSeries = new XYChart.Series<>();
        newSeries.setName(category);
        chartData.add(newSeries);
        return newSeries;
    }
}
