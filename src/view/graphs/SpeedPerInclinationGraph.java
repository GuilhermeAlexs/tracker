package view.graphs;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

import model.LocationType;

public class SpeedPerInclinationGraph extends JFrame {
	private JPanel contentPane;
	private ChartPanel chartPanel;
	
	private double [][] data;

	public SpeedPerInclinationGraph(double [][] data) {
		this.data = data;
		
		setTitle("Gráfico de Velocidades");
		setIconImage(new ImageIcon("/logo.png").getImage());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		      
		setContentPane(contentPane);
		
		showGraphXY(LocationType.TRAIL);
	}
	
	private void showGraph(LocationType type){
		JFreeChart barChart = ChartFactory.createBarChart(
		         null,           
		         null,            
		         null,            
		         createDataset(type),          
		         PlotOrientation.VERTICAL,           
		         true, true, false);
		
		if(chartPanel == null){
			chartPanel = new ChartPanel(barChart);
			getContentPane().add(chartPanel,BorderLayout.CENTER);
		}else{
			chartPanel.setChart(barChart);
		}		
	}
	
	private void showGraphXY(LocationType type){
	    final JFreeChart chart = ChartFactory.createXYLineChart(
	            "XY Series Demo",
	            "X", 
	            "Y", 
	            createXYDataset(type),
	            PlotOrientation.VERTICAL,
	            true,
	            true,
	            false
	        );
	    
	    chart.getLegend().setBackgroundPaint(Color.BLACK);
	    chart.getLegend().setItemPaint(Color.WHITE);
	    chart.getLegend().setItemLabelPadding(new RectangleInsets(2, 10, 2, 10));
	    
	    XYLineAndShapeRenderer r1 = new XYLineAndShapeRenderer();
	    r1.setSeriesPaint(LocationType.TRAIL.getValue(), Color.GREEN); 
	    r1.setSeriesPaint(LocationType.ROAD.getValue(), Color.GRAY);
	    r1.setSeriesPaint(LocationType.RIVER.getValue(), Color.BLUE); 
	    r1.setSeriesPaint(LocationType.SNOW.getValue(), Color.WHITE); 
	    r1.setSeriesPaint(LocationType.FOREST.getValue(), new Color(0, 100, 0)); 

	    for(int i = 0; i < 5; i++){
	    	r1.setSeriesItemLabelsVisible(i, true);
	    	r1.setSeriesStroke(i, new BasicStroke(2.8f));
	    }

	    XYPlot plot = (XYPlot) chart.getPlot(); 

	    NumberAxis domain = (NumberAxis) plot.getDomainAxis();
        domain.setLabel("Inclinação Média");
        domain.setLabelPaint(new Color(200,200,200));
        domain.setLabelFont(domain.getTickLabelFont().deriveFont(14.0f));
        domain.setTickLabelsVisible(true);
        domain.setTickLabelPaint(Color.WHITE);
        domain.setTickLabelFont(domain.getTickLabelFont().deriveFont(10.0f));
        domain.setAxisLineVisible(true);

        NumberAxis range = (NumberAxis) plot.getRangeAxis();
        range.setLabel("Velocidade");
        range.setLabelPaint(new Color(200,200,200));
        range.setLabelFont(domain.getTickLabelFont().deriveFont(14.0f));
        range.setTickLabelPaint(Color.WHITE);
        range.setTickLabelsVisible(true);
        range.setTickLabelFont(domain.getTickLabelFont().deriveFont(10.0f));
        range.setAxisLineVisible(true);

        chart.getPlot().setOutlineVisible(false);
        chart.getPlot().setBackgroundPaint(Color.BLACK);
        chart.setBackgroundPaint(Color.BLACK);

	    plot.setRenderer(0, r1);

		if(chartPanel == null){
			chartPanel = new ChartPanel(chart);
			getContentPane().add(chartPanel,BorderLayout.CENTER);
		}else{
			chartPanel.setChart(chart);
		}		
	}

	private CategoryDataset createDataset(LocationType type) {
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        for(int j = 3; j < 15; j++){
    		dataset.addValue(data[type.getValue()][j], ((10*j) - 90) + "% - " + ((10*(j+1)) - 90) + "%", Integer.valueOf(type.getValue()));
        }
        
        return dataset;
    }

	private IntervalXYDataset createXYDataset(LocationType type) {
        final XYSeriesCollection dataset = new XYSeriesCollection();
        double m = 0;
        
        for(int i = 0; i < 5; i++){
        	XYSeries series = new XYSeries(LocationType.getTypeFromValue(i).getName());

	        for(int j = 0; j < 17; j++){
	        	m = ((double)((10*j) - 90)+((10*(j+1)) - 90))/(double)2;
	        	
	        	if(data[i][j] == 0)
	        		continue;
	        	
	        	series.add(m, data[i][j]);
	        }
	        
	        dataset.addSeries(series);
        }
        return dataset;
    }
}
