import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;

public class SmartIrrigationGUI extends JFrame {
    private JLabel tempLabel, humLabel, moistLabel, pumpLabel;
    private XYSeries tempSeries, humSeries, moistSeries;
    private int timeIndex = 0;
    private ThingSpeakIntegration thingSpeakIntegration;

    public SmartIrrigationGUI() {
        setTitle("Smart Irrigation Monitor (Dummy)");
        setSize(900, 600);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel topPanel = new JPanel(new GridLayout(2, 2));
        tempLabel = new JLabel("Temp: -- C");
        humLabel = new JLabel("Humidity: -- %");
        moistLabel = new JLabel("Soil Moisture: -- %");
        pumpLabel = new JLabel("Pump: --");

        topPanel.add(tempLabel);
        topPanel.add(humLabel);
        topPanel.add(moistLabel);
        topPanel.add(pumpLabel);
        add(topPanel, BorderLayout.NORTH);

        tempSeries = new XYSeries("Temp");
        humSeries = new XYSeries("Humidity");
        moistSeries = new XYSeries("Moisture");

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(tempSeries);
        dataset.addSeries(humSeries);
        dataset.addSeries(moistSeries);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Sensor Readings Over Time",
                "Time",
                "Value",
                dataset
        );

        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        plot.setRenderer(renderer);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 500));
        add(chartPanel, BorderLayout.CENTER);

        JButton exportButton = new JButton("Export CSV");
        exportButton.addActionListener(e -> exportToCSV());
        add(exportButton, BorderLayout.SOUTH);

        thingSpeakIntegration = new ThingSpeakIntegration();

        setVisible(true);
        simulateData(); // Start data simulation
    }

    private void simulateData() {
        // Hardcoded dummy data for the first 10 time indices
        float[][] sensorData = {
            {23.12f, 55.23f, 67.89f}, // Time 0
            {26.45f, 60.12f, 45.67f}, // Time 1
            {22.89f, 58.23f, 32.78f}, // Time 2
            {25.67f, 63.56f, 50.23f}, // Time 3
            {27.34f, 65.12f, 75.12f}, // Time 4
            {24.78f, 59.34f, 43.56f}, // Time 5
            {28.45f, 62.78f, 30.89f}, // Time 6
            {26.67f, 57.89f, 60.34f}, // Time 7
            {29.11f, 68.45f, 80.12f}, // Time 8
            {21.34f, 54.67f, 55.45f}  // Time 9
        };

        // Timer to simulate data every 2 seconds
        new Timer(2000, e -> {
            if (timeIndex < sensorData.length) {
                float temp = sensorData[timeIndex][0];
                float hum = sensorData[timeIndex][1];
                float moist = sensorData[timeIndex][2];
                int pump = moist < 40 ? 1 : 0;

                // Update the labels
                tempLabel.setText("Temp: " + temp + " C");
                humLabel.setText("Humidity: " + hum + " %");
                moistLabel.setText("Soil Moisture: " + moist + " %");
                pumpLabel.setText("Pump: " + (pump == 1 ? "ON" : "OFF"));

                // Add the data to the series for the graph
                tempSeries.add(timeIndex, temp);
                humSeries.add(timeIndex, hum);
                moistSeries.add(timeIndex, moist);
                timeIndex++;

                // If internet is available, send data to ThingSpeak
                if (thingSpeakIntegration.isInternetAvailable()) {
                    thingSpeakIntegration.sendDataToThingSpeak(temp, hum, moist);
                } else {
                    handleOfflineData(temp, hum, moist);
                }
            }
        }).start();
    }

    private void exportToCSV() {
        try (FileWriter writer = new FileWriter("sensor_data.csv")) {
            writer.write("Time,Temperature,Humidity,Moisture\n");
            for (int i = 0; i < tempSeries.getItemCount(); i++) {
                writer.write(i + "," +
                        tempSeries.getY(i) + "," +
                        humSeries.getY(i) + "," +
                        moistSeries.getY(i) + "\n");
            }
            JOptionPane.showMessageDialog(this, "Data exported to sensor_data.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleOfflineData(float temp, float hum, float moist) {
        try (FileWriter writer = new FileWriter("offline_data.csv", true)) {
            writer.write(timeIndex + "," + temp + "," + hum + "," + moist + "\n");
            System.out.println("Data saved locally while offline.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new SmartIrrigationGUI();
    }
}

/*import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class SmartIrrigationDummy extends JFrame {
    private JLabel tempLabel, humLabel, moistLabel, pumpLabel;
    private XYSeries tempSeries, humSeries, moistSeries;
    private int timeIndex = 0;
    private ThingSpeakIntegration thingSpeakIntegration;

    public SmartIrrigationDummy() {
        setTitle("Smart Irrigation Monitor (Dummy)");
        setSize(900, 600);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel topPanel = new JPanel(new GridLayout(2, 2));
        tempLabel = new JLabel("Temp: -- C");
        humLabel = new JLabel("Humidity: -- %");
        moistLabel = new JLabel("Soil Moisture: -- %");
        pumpLabel = new JLabel("Pump: --");

        topPanel.add(tempLabel);
        topPanel.add(humLabel);
        topPanel.add(moistLabel);
        topPanel.add(pumpLabel);
        add(topPanel, BorderLayout.NORTH);

        tempSeries = new XYSeries("Temp");
        humSeries = new XYSeries("Humidity");
        moistSeries = new XYSeries("Moisture");

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(tempSeries);
        dataset.addSeries(humSeries);
        dataset.addSeries(moistSeries);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Sensor Readings Over Time",
                "Time",
                "Value",
                dataset
        );

        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        plot.setRenderer(renderer);

        ChartPanel chartPanel = new ChartPanel(chart);
        add(chartPanel, BorderLayout.CENTER);

        JButton exportButton = new JButton("Export CSV");
        exportButton.addActionListener(e -> exportToCSV());
        add(exportButton, BorderLayout.SOUTH);

        thingSpeakIntegration = new ThingSpeakIntegration();

        setVisible(true);
        simulateData(); // Replaces serial
    }

    private void simulateData() {
        Random random = new Random();

        new Timer(2000, e -> {
            float temp = 20 + random.nextFloat() * 10;
            float hum = 40 + random.nextFloat() * 30;
            float moist = random.nextFloat() * 100;
            int pump = moist < 40 ? 1 : 0;

            tempLabel.setText("Temp: " + temp + " C");
            humLabel.setText("Humidity: " + hum + " %");
            moistLabel.setText("Soil Moisture: " + moist + " %");
            pumpLabel.setText("Pump: " + (pump == 1 ? "ON" : "OFF"));

            tempSeries.add(timeIndex, temp);
            humSeries.add(timeIndex, hum);
            moistSeries.add(timeIndex, moist);
            timeIndex++;

            if (thingSpeakIntegration.isInternetAvailable()) {
                thingSpeakIntegration.sendDataToThingSpeak(temp, hum, moist);
            } else {
                handleOfflineData(temp, hum, moist);
            }
        }).start();
    }

    private void exportToCSV() {
        try (FileWriter writer = new FileWriter("sensor_data.csv")) {
            writer.write("Time,Temperature,Humidity,Moisture\n");
            for (int i = 0; i < tempSeries.getItemCount(); i++) {
                writer.write(i + "," +
                        tempSeries.getY(i) + "," +
                        humSeries.getY(i) + "," +
                        moistSeries.getY(i) + "\n");
            }
            JOptionPane.showMessageDialog(this, "Data exported to sensor_data.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleOfflineData(float temp, float hum, float moist) {
        try (FileWriter writer = new FileWriter("offline_data.csv", true)) {
            writer.write(timeIndex + "," + temp + "," + hum + "," + moist + "\n");
            System.out.println("Data saved locally while offline.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new SmartIrrigationDummy();
    }
}

*/