package view.graphs;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.OutOfRangeException;
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
import model.TableOfSpeedsContinous;

public class SpeedPerInclinationGraph extends JFrame {
	private JPanel contentPane;
	private ChartPanel chartPanel;
	
	private double [][] data = null;
	private List<UnivariateFunction> listFunctions = null;
	private int steps;
	
	public SpeedPerInclinationGraph(List<UnivariateFunction> data, int steps) {
		this.listFunctions = data;
		
		initGUI();
	}
	
	public SpeedPerInclinationGraph(double [][] data, int steps) {
		this.data = data;
		
		initGUI();
	}
	
	private void initGUI(){
		setTitle("Grráfico de Velocidades");
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
		IntervalXYDataset dataset;
		
		if(data != null)
			dataset = createXYDataset(type);
		else
			dataset = createXYDatasetContinuous(type);
		
	    final JFreeChart chart = ChartFactory.createXYLineChart(
	            "XY Series Demo",
	            "X", 
	            "Y", 
	            dataset,
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
	    r1.setShapesVisible(false);
	    
	    for(int i = 0; i < 5; i++){
	    	r1.setSeriesItemLabelsVisible(i, true);
	    	r1.setSeriesStroke(i, new BasicStroke(2.8f));
	    }

	    XYPlot plot = (XYPlot) chart.getPlot(); 

	    NumberAxis domain = (NumberAxis) plot.getDomainAxis();
        domain.setLabel("Inclinação");
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
        
        for(int j = 3; j < 177; j++){
    		dataset.addValue(data[type.getValue()][j], ((1*j) - 90) + "% - " + ((1*(j+1)) - 90) + "%", Integer.valueOf(type.getValue()));
        }
        
        return dataset;
    }

	private IntervalXYDataset createXYDataset(LocationType type) {
        final XYSeriesCollection dataset = new XYSeriesCollection();
        double m = 0;
        int total = 180/steps;
        
        for(int i = 0; i < 5; i++){
        	XYSeries series = new XYSeries(LocationType.getTypeFromValue(i).getName());

	        for(int j = 0; j < total; j++){
	        	m = ((double)((steps*j) - 90)+((steps*(j+1)) - 90))/(double)2;
	        	
	        	if(data[i][j] == 0)
	        		continue;
	        	
	        	series.add(m, data[i][j]);
	        }
	        
	        dataset.addSeries(series);
        }
        return dataset;
    }
	
	private IntervalXYDataset createXYDatasetContinuous(LocationType type) {
        final XYSeriesCollection dataset = new XYSeriesCollection();
        double v = 0;
        
        UnivariateFunction function;
        
        for(int i = 0; i < 5; i++){
        	XYSeries series = new XYSeries(LocationType.getTypeFromValue(i).getName());
        	function = listFunctions.get(i);
        	
        	if(function == null)
        		continue;
        	
	        for(double m = -80; m <= 80; m++){
	        	try{
	        		v = function.value(m);
	        		series.add(m, v);
	        	}catch(OutOfRangeException e){
	        		
	        	}
	        }
	        
	        dataset.addSeries(series);
        }
        return dataset;
    }
}
