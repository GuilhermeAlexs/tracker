package view.widgets;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.entity.PlotEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.Layer;

import model.Statistics;
import model.TPLocation;
import view.widgets.events.ElevationGraphListener;

public class ElevationPanel extends JPanel implements ChartMouseListener, MouseListener{
	private static final long serialVersionUID = 8224184190104138985L;
	
	private ChartPanel chartPanel;
	private JFreeChart chart;
	
	private IntervalMarker selection;
	private int selStart = -1;
	private int selEnd = -1;
	
	private boolean firstClick = true;
	private boolean escapeMouseLeftClickEvent = false;
	private boolean wasMouseRightClickEvent = false;
	private boolean escapeMouseMoveEvent = false;
	
	private JLabel labelDistance;
	private JLabel labelElevation;
	private JLabel labelElevationGainLoss;
	private JLabel labelInclinationAvg;
	private JLabel labelInclinationMax;
	
	private List<TPLocation> currentTrail;
	
	private ElevationGraphListener elevationGraphListener;
	
	public ElevationPanel(List<TPLocation> trail){
		super();
		
		this.currentTrail = trail;
		
		labelDistance = makeStatLabel();
		labelElevation = makeStatLabel();
		labelElevationGainLoss = makeStatLabel();
		labelInclinationAvg = makeStatLabel();
		labelInclinationMax = makeStatLabel();
		
		JPanel numbersPanel = new JPanel();
		numbersPanel.setOpaque(true);
		numbersPanel.setBackground(Color.BLACK);
		numbersPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		numbersPanel.add(labelDistance);
		numbersPanel.add(Box.createRigidArea(new Dimension(5,0)));
		numbersPanel.add(labelElevation);
		numbersPanel.add(Box.createRigidArea(new Dimension(5,0)));
		numbersPanel.add(labelElevationGainLoss);
		numbersPanel.add(Box.createRigidArea(new Dimension(5,0)));
		numbersPanel.add(labelInclinationMax);
		numbersPanel.add(Box.createRigidArea(new Dimension(5,0)));
		numbersPanel.add(labelInclinationAvg);
		
		setLayout(new BorderLayout(0, 0));
		add(numbersPanel, BorderLayout.NORTH);
		
		drawElevationGraph();
		
		add(chartPanel, BorderLayout.CENTER);
	}
	
	public ElevationGraphListener getElevationGraphListener() {
		return elevationGraphListener;
	}

	public void setElevationGraphListener(ElevationGraphListener elevationGraphListener) {
		this.elevationGraphListener = elevationGraphListener;
	}

	public List<TPLocation> getCurrentTrail() {
		return currentTrail;
	}

	public void setCurrentTrail(List<TPLocation> currentTrail) {
		this.currentTrail = currentTrail;
	}

	public void drawElevationGraph(){
		XYSeries series = new XYSeries("Profile");
		
		double i = 0;
		double maxAltitude = -1;
		double minAltitude = 9000;
		List<TPLocation> locs = currentTrail;
		TPLocation loc = locs.get(0);
		Iterator<TPLocation> it = locs.iterator();
		
		while(it.hasNext()){
			
			if(loc.getAltitude() > maxAltitude)
				maxAltitude = loc.getAltitude();
			
			if(loc.getAltitude() < minAltitude)
				minAltitude = loc.getAltitude();

			series.add(i, loc.getAltitude());
			
			loc = it.next();
			i = i + 1;
		}
        
        final XYSeriesCollection data = new XYSeriesCollection(series);
        chart = ChartFactory.createXYLineChart(null,null,null, data,PlotOrientation.VERTICAL,false,true,false);

        final XYAreaRenderer rend = new XYAreaRenderer();
        //rend.setPrecision(50);

        rend.setSeriesItemLabelsVisible(0, false);
        rend.setSeriesPaint(0, new Color(221, 141, 22));
        rend.setSeriesStroke(0, new BasicStroke(1.8f));
        
        XYPlot xyPlot = (XYPlot) chart.getPlot();

        NumberAxis domain = (NumberAxis) xyPlot.getDomainAxis();
        domain.setLabel(null);
        domain.setRange(0.00, i);
        domain.setTickUnit(new NumberTickUnit(100));
        domain.setTickLabelsVisible(false);
        domain.setAxisLineVisible(false);

        NumberAxis range = (NumberAxis) xyPlot.getRangeAxis();
        range.setRange(minAltitude, maxAltitude + 10);
        range.setTickUnit(new NumberTickUnit(50));
        range.setLabel(null);
        range.setTickLabelsVisible(true);
        range.setTickLabelFont(domain.getTickLabelFont().deriveFont(10.0f));
        range.setAxisLineVisible(false);
       
        chart.getPlot().setOutlineVisible(false);
        chart.getPlot().setBackgroundPaint(Color.BLACK);
        chart.setBackgroundPaint(Color.BLACK);
        
        xyPlot.setRenderer(rend);
        
        selection = new IntervalMarker(0, 0);
        selection.setPaint(new Color(221/3, 0, 0));
        
        xyPlot.addDomainMarker(selection, Layer.BACKGROUND);
        
        chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(Color.BLACK);
        chartPanel.setDomainZoomable(false);
        chartPanel.setRangeZoomable(false);
        chartPanel.setPopupMenu(null);
        chartPanel.setBorder(null);
        
        chartPanel.addChartMouseListener(this);
        chartPanel.addMouseListener(this);
	}
	
	private JLabel makeStatLabel(){
		JLabel label = new JLabel();
		label.setForeground(Color.WHITE);
		label.setFont(label.getFont().deriveFont(10f));
		
		return label;
	}
	
	public int getLastSelectionStart(){
		return this.selStart;
	}
	
	public int getLastSelectionEnd(){
		return this.selEnd;
	}
	
	public void clearProfileSelections(){
		XYPlot xyPlot = (XYPlot) chart.getPlot();
		xyPlot.clearDomainMarkers();
	}
	
	public void drawProfileSelection(List<TPLocation> locs, Color color, boolean foreground){
		if(locs == null || locs.size() == 0)
			return;
		
		XYPlot xyPlot = (XYPlot) chart.getPlot();
		
		IntervalMarker stretchMarker = new IntervalMarker(locs.get(0).getId(), locs.get(locs.size() - 1).getId());
		stretchMarker.setPaint(color);
		stretchMarker.setAlpha(0.5f);
		
		if(!foreground)
			xyPlot.addDomainMarker(stretchMarker, Layer.BACKGROUND);
		else{
			stretchMarker.setAlpha(0.88f);
			xyPlot.addDomainMarker(stretchMarker, Layer.FOREGROUND);
		}
	}
	
	public void showNumberStatisticsPanel(Statistics stats){
		final double pC = 100/(double)90; 
		
		labelDistance.setText("Distância: " + new DecimalFormat("#.##").format(stats.getLength()/(double)1000) + "km");
		
		labelElevation.setText("Elev. Min Méd Max: " + new DecimalFormat("#").format(stats.getMinElevation()) + "m  " + 
				new DecimalFormat("#").format(stats.getAvgElevation()) + "m  " + new DecimalFormat("#").format(stats.getMaxElevation()) + "m");

		labelElevationGainLoss.setText("Ganho/Perda de Elev.: " + new DecimalFormat("#").format(stats.getElevationGain())
				+ "m  " + new DecimalFormat("#").format(stats.getElevationLoss()) + "m");
		
		labelInclinationMax.setText("Incl. Máxima: " + new DecimalFormat("#.#").format(stats.getMaxInclinationPositive()*pC) + "%  "
				+ new DecimalFormat("#.#").format(stats.getMaxInclinationNegative()*pC) + "%");
		
		labelInclinationAvg.setText("Incl. Média: " + new DecimalFormat("#.#").format(stats.getAvgInclinationPositive()*pC) + "%  " 
				+ new DecimalFormat("#.#").format(stats.getAvgInclinationNegative()*pC) + "%");
	}

	private int getDomainFromClick(ChartMouseEvent e){
		int x;
		
		if(e.getEntity() instanceof XYItemEntity){
			XYItemEntity ce = (XYItemEntity) e.getEntity();
			x = (int) ce.getDataset().getX(ce.getSeriesIndex(),  ce.getItem()).doubleValue();
		}else if(e.getEntity() instanceof PlotEntity){
			XYPlot xyplot = (XYPlot) e.getChart().getPlot();
			Point2D p = e.getTrigger().getPoint();
			Rectangle2D plotArea = chartPanel.getScreenDataArea();
			x = (int) xyplot.getDomainAxis().java2DToValue(p.getX(), plotArea, xyplot.getDomainAxisEdge());
		}else{
			x = currentTrail.size() - 1;
		}
		
		return x;
	}
		
	@Override
	public void chartMouseClicked(ChartMouseEvent e) {
		if(wasMouseRightClickEvent || escapeMouseLeftClickEvent)
			return;

		if(firstClick){ //Usuï¿½rio deu o primeiro click
			escapeMouseMoveEvent = false;
			selStart = getDomainFromClick(e);
			selEnd = selStart;
		}else{
			escapeMouseLeftClickEvent = true;
			escapeMouseMoveEvent = true; //Impede que movimentos do mouse apaguem a seleï¿½ï¿½o atual
			selEnd = getDomainFromClick(e);
			
			elevationGraphListener.onGraphSelectionFinished(selStart, selEnd);
		}
		
		firstClick = !firstClick;
	}

	@Override
	public void chartMouseMoved(ChartMouseEvent e) {
		if(escapeMouseMoveEvent)
			return;
		
		selEnd = getDomainFromClick(e);
		
		if(firstClick){
			elevationGraphListener.onGraphSelectionMoving(selEnd, selEnd);
			return;
		}
		
		elevationGraphListener.onGraphSelectionMoving(selStart, selEnd);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(SwingUtilities.isRightMouseButton(e)){ //Botï¿½o direito sempre apaga a seleï¿½ï¿½o
			escapeMouseMoveEvent = false;
			escapeMouseLeftClickEvent = false;
			wasMouseRightClickEvent = true;
			
			elevationGraphListener.onGraphClearRequest();
			
			firstClick = true;
			selStart = -1;
			selEnd = -1;
		}else{
			wasMouseRightClickEvent = false;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
}
