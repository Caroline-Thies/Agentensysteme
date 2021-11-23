import javax.swing.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

import java.util.Arrays;

public class UIApp {
    private static XYDataset dataset;

    public static void run() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Analyse");

                frame.setSize(900, 600);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);

                JFreeChart chart = ChartFactory.createXYLineChart("Results",
                        "Iteration",
                        "Kosten",
                        UIApp.dataset,
                        PlotOrientation.VERTICAL,
                        true,
                        true,
                        false);

                ChartPanel chartPanel = new ChartPanel(chart);
                frame.getContentPane().add(chartPanel);
            }
        });
    }

    public static void addDataset(int[] xData, int[] yData, String name){
        DefaultXYDataset dataset = new DefaultXYDataset();
        if(UIApp.dataset != null){
            dataset = (DefaultXYDataset) UIApp.dataset;
        }
        double[] xDataDoubles = Arrays.stream(xData).asDoubleStream().toArray();
        double[] yDataDoubles = Arrays.stream(yData).asDoubleStream().toArray();
        double[][] data = {xDataDoubles, yDataDoubles};
        dataset.addSeries(name, data);
        UIApp.dataset = dataset;
    }

}
