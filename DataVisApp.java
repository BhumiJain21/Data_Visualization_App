package com.datavis;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class DataVisApp extends Application {

    private ChartManager chartManager;

    @Override
    public void start(Stage primaryStage) {
        chartManager = new ChartManager();
        
        BorderPane root = new BorderPane();
        MenuBar menuBar = createMenuBar();
        root.setTop(menuBar);
        root.setCenter(chartManager.getChartPane());

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Data Visualization Tool");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem loadData = new MenuItem("Load Data");
        loadData.setOnAction(e -> chartManager.loadData());
        fileMenu.getItems().add(loadData);
        menuBar.getMenus().add(fileMenu);

        Menu chartMenu = new Menu("Charts");
        MenuItem lineChart = new MenuItem("Line Chart");
        MenuItem barChart = new MenuItem("Bar Chart");
        MenuItem scatterPlot = new MenuItem("Scatter Plot");

        lineChart.setOnAction(e -> chartManager.createLineChart());
        barChart.setOnAction(e -> chartManager.createBarChart());
        scatterPlot.setOnAction(e -> chartManager.createScatterPlot());

        chartMenu.getItems().addAll(lineChart, barChart, scatterPlot);
        menuBar.getMenus().add(chartMenu);

        return menuBar;
    }

    public static void main(String[] args) {
        launch(args);
    }
}