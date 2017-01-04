package view.graphs;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;
import java.util.Map;

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
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

import model.TypeConstants;
import model.StretchType;
import view.Session;

public class SpeedPerInclinationGraph extends JFrame {
	private static final long serialVersionUID = 2450136146764792751L;

	private String xLabel;
	private String yLabel;

	private JPanel contentPane;
	private ChartPanel chartPanel;

	private List<UnivariateFunction> listFunctions = null;

	public SpeedPerInclinationGraph(List<UnivariateFunction> data, double steps, String title, String xLabel, String yLabel) {
		this.listFunctions = data;
		this.xLabel = xLabel;
		this.yLabel = yLabel;
		
		setTitle(title);
		setIconImage(new ImageIcon("/logo.png").getImage());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		
		setContentPane(contentPane);
		
		showGraphXY();
	}
	
	private void showGraphXY(){
		XYSeriesCollection dataset;

		dataset = createXYDatasetContinuous();
		
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
	    XYPlot plot = (XYPlot) chart.getPlot(); 
	    
	    Session session = Session.getInstance();

	    int index = 0;
		for (Map.Entry<String, StretchType> entry : session.getStretchTypes().entrySet()){
			if(entry.getKey().equals(TypeConstants.FIXED_TYPE_INVALID))
				continue;
			
			if(((XYSeriesCollection)plot.getDataset(0)).getSeries(index).getKey().toString().contains("<<!null!>>")){
				r1.setSeriesVisibleInLegend(index, Boolean.FALSE);
			}
			
			r1.setSeriesPaint(index, entry.getValue().getColor()); 
			index++;
		}

	    r1.setShapesVisible(false);
	    
	    for(int i = 0; i < session.getStretchTypes().size(); i++){
	    	r1.setSeriesItemLabelsVisible(i, true);
	    	r1.setSeriesStroke(i, new BasicStroke(2.8f));
	    }

	    NumberAxis domain = (NumberAxis) plot.getDomainAxis();
        domain.setLabel(xLabel);
        domain.setLabelPaint(new Color(200,200,200));
        domain.setLabelFont(domain.getTickLabelFont().deriveFont(14.0f));
        domain.setTickLabelsVisible(true);
        domain.setTickLabelPaint(Color.WHITE);
        domain.setTickLabelFont(domain.getTickLabelFont().deriveFont(10.0f));
        domain.setAxisLineVisible(true);

        NumberAxis range = (NumberAxis) plot.getRangeAxis();
        range.setLabel(yLabel);
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
	
	private String getIdFromIndexType(int index){
	    Session session = Session.getInstance();

		for (Map.Entry<String, Integer> entry : session.getStretchTypesIdMap().entrySet()){
			if(entry.getValue() == index){
				return entry.getKey();
			}
		}

		return null;
	}
	
	private XYSeriesCollection createXYDatasetContinuous() {
        final XYSeriesCollection dataset = new XYSeriesCollection();
        double v = 0;

        UnivariateFunction function;

        Session session = Session.getInstance();
        String id;

        for(int i = 0; i < session.getStretchTypes().size(); i++){
        	id = getIdFromIndexType(i);

        	if(id.equals(TypeConstants.FIXED_TYPE_INVALID)){
        		continue;
        	}

        	XYSeries series = new XYSeries(session.getStretchTypes().get(id).getName());
        	function = listFunctions.get(i);

        	if(function != null){        	
		        for(double m = -90; m <= 90; m = m + 1){
		        	try{
		        		v = 3.6*function.value(m);

		        		if(v < 0)
		        			continue;

		        		series.add(m, v);
		        	}catch(OutOfRangeException e){

		        	}
		        }
        	}else{
        		series.setKey(session.getStretchTypes().get(id).getName() + ":<<!null!>>");
        	}

        	dataset.addSeries(series);
        }

        return dataset;
    }
}
