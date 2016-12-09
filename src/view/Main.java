package view;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;
import javax.swing.SwingUtilities;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import javax.swing.event.MouseInputListener;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FileUtils;
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
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.VirtualEarthTileFactoryInfo;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.LocalResponseCache;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.WaypointPainter;

import database.DatabaseManager;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import model.LocationType;
import model.Statistics;
import model.TPLocation;
import model.TableOfSpeeds;
import utils.KmlUtils;
import utils.MathOperation;
import utils.StatisticsUtil;
import utils.listeners.KmlParseProgressListener;
import view.filters.ExtensionFileFilter;
import view.graphs.SpeedPerInclinationGraph;
import view.listeners.StretchTypeChangeListener;
import view.painters.RoutePainter;
import view.painters.SelectionPainter;
import view.painters.SwingWaypointOverlayPainter;
import view.waypoint.SwingWaypoint;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JToolBar;
import javax.swing.KeyStroke;

public class Main extends JFrame implements ChartMouseListener, MouseListener, ActionListener {
	private static final long serialVersionUID = -8622730863892625194L;
	
	private static final String OPEN_EVT = "open";
	private static final String EDIT_STRETCH_EVT = "edit_stretch";
	private static final String ERASE_STRETCHS_EVT = "erase_stretchs";
	private static final String SAVE_IN_DATABASE_EVT = "save_in_database";
	private static final String DELETE_IN_DATABASE_EVT = "delete_in_database";
	private static final String OPEN_FROM_DATABASE_EVT = "configure_from_database";
	private static final String STATISTICS_EVT = "calculate_statistics";
	
	public static String authUser = "c1278491";
	public static String authPassword = "91769457";
	
	public DatabaseManager db = DatabaseManager.getInstance();
	
	public Icon markerA;
	public Icon markerB;
	
	private JXMapViewer mapViewer;
	private IntervalMarker selection;
	private int selStart = -1;
	private int selEnd = -1;
	
	private ChartPanel chartPanel;
	private JFreeChart chart;
	
	private JPanel contentPane;
	
	private boolean firstClick = true;
	private boolean escapeMouseLeftClickEvent = false;
	private boolean wasMouseRightClickEvent = false;
	private boolean escapeMouseMoveEvent = false;
	
	private JSplitPane splitPanel;
	private JToolBar toolbar;
	private JProgressBar progressBar;
	private JButton selectTypeButton;
	private JButton insertDatabaseButton;
	private JButton deleteDatabaseButton;
	private JButton openDatabaseButton;
	private JButton eraseSelectionsButton;
	private JButton statisticsButton;
	
	private JLabel labelDistance;
	private JLabel labelElevation;
	private JLabel labelElevationGainLoss;
	private JLabel labelInclinationAvg;
	private JLabel labelInclinationMax;
	
	private Statistics trailStatistics;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Main() {
		setTitle("Tracker");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(new ImageIcon("/logo.png").getImage());
		setBounds(100, 500, 1086, 731);
		
		useProxy();
		initializarIcons();
		initializeMap();
		
		toolbar = new JToolBar();
		toolbar.setFloatable(false);
		addButtonsToToolbar(toolbar);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		progressBar = new JProgressBar();
		progressBar.setPreferredSize(new Dimension(1000,20));
		progressBar.setForeground(new Color(221,141,22));
		progressBar.setIndeterminate(true);
		progressBar.setVisible(false);
		
		contentPane.setLayout(new BorderLayout(0, 0));
		contentPane.add(toolbar, BorderLayout.NORTH);
		contentPane.add(mapViewer, BorderLayout.CENTER);
		contentPane.add(progressBar, BorderLayout.SOUTH);
		
		labelDistance = makeStatLabel();
		labelElevation = makeStatLabel();
		labelElevationGainLoss = makeStatLabel();
		labelInclinationAvg = makeStatLabel();
		labelInclinationMax = makeStatLabel();
	}
	
	private JLabel makeStatLabel(){
		JLabel label = new JLabel();
		label.setForeground(Color.WHITE);
		label.setFont(label.getFont().deriveFont(10f));
		
		return label;
	}
	
	private void showNumberStatisticsPanel(Statistics stats){
		final double pC = 100/(double)90; 
		
		labelDistance.setText("Distância: " + new DecimalFormat("#.##").format(stats.getLength()/(double)1000) + "km");
		
		labelElevation.setText("Elevação Min Avg Max: " + new DecimalFormat("#").format(stats.getMinElevation()) + "m  " + 
				new DecimalFormat("#").format(stats.getAvgElevation()) + "m  " + new DecimalFormat("#").format(stats.getMaxElevation()) + "m");

		labelElevationGainLoss.setText("Ganho/Perda de Elevação: " + new DecimalFormat("#").format(stats.getElevationGain())
				+ "m  " + new DecimalFormat("#").format(stats.getElevationLoss()) + "m");
		
		labelInclinationMax.setText("Inclinação Máxima: " + new DecimalFormat("#.#").format(stats.getMaxInclinationPositive()*pC) + "%  "
				+ new DecimalFormat("#.#").format(stats.getMaxInclinationNegative()*pC) + "%");
		
		labelInclinationAvg.setText("Inclinação Média: " + new DecimalFormat("#.#").format(stats.getAvgInclinationPositive()*pC) + "%  " 
				+ new DecimalFormat("#.#").format(stats.getAvgInclinationNegative()*pC) + "%");
	}
	
	private void makeSplitPanelOnTheFly(){
		contentPane.removeAll();
		
		splitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPanel.setOneTouchExpandable(true);
		splitPanel.setDividerSize(3);

		splitPanel.add(mapViewer);
		
		JPanel numbersPanel = new JPanel();
		numbersPanel.setOpaque(true);
		numbersPanel.setBackground(Color.BLACK);
		numbersPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		trailStatistics = StatisticsUtil.calculateStats(Session.currentTrail);
		showNumberStatisticsPanel(trailStatistics);
		
		numbersPanel.add(labelDistance);
		numbersPanel.add(Box.createRigidArea(new Dimension(10,0)));
		numbersPanel.add(labelElevation);
		numbersPanel.add(Box.createRigidArea(new Dimension(10,0)));
		numbersPanel.add(labelElevationGainLoss);
		numbersPanel.add(Box.createRigidArea(new Dimension(10,0)));
		numbersPanel.add(labelInclinationMax);
		numbersPanel.add(Box.createRigidArea(new Dimension(10,0)));
		numbersPanel.add(labelInclinationAvg);
		
		JPanel statsPanel = new JPanel();
		statsPanel.setLayout(new BorderLayout(0, 0));
		statsPanel.add(numbersPanel, BorderLayout.NORTH);
		statsPanel.add(chartPanel, BorderLayout.CENTER);
		
		splitPanel.add(statsPanel);
		splitPanel.setResizeWeight(0.7f);
		
		contentPane.add(toolbar, BorderLayout.NORTH);
		contentPane.add(splitPanel, BorderLayout.CENTER);
		
        contentPane.revalidate(); 
        contentPane.repaint();
	}
	
	@SuppressWarnings("serial")
	protected void addButtonsToToolbar(JToolBar toolBar) {
	    JButton button = null;

	    button = makeNavigationButton("open", OPEN_EVT, "Abrir um KML");
	    toolBar.add(button);
	    
	    toolbar.add(Box.createRigidArea(new Dimension(25,0)));
	    
	    selectTypeButton = makeNavigationButton("foot", EDIT_STRETCH_EVT, "Editar trecho");
	    selectTypeButton.setEnabled(false);
	    selectTypeButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
	            KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), EDIT_STRETCH_EVT);
	    selectTypeButton.getActionMap().put(EDIT_STRETCH_EVT, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openMarkStretchDialog();
            }
        });
	    toolBar.add(selectTypeButton);
	    
	    eraseSelectionsButton = makeNavigationButton("erase", ERASE_STRETCHS_EVT, "Apagar todas as marcações da trilha");
	    eraseSelectionsButton.setEnabled(false);
	    toolBar.add(eraseSelectionsButton);
	    
	    toolbar.add(Box.createRigidArea(new Dimension(25,0)));	
	    
	    insertDatabaseButton = makeNavigationButton("database", SAVE_IN_DATABASE_EVT, "Salvar essa trilha no banco de trilhas");
	    insertDatabaseButton.setEnabled(false);
	    toolBar.add(insertDatabaseButton);
	    
	    deleteDatabaseButton = makeNavigationButton("database_rem", DELETE_IN_DATABASE_EVT, "Remover essa trilha do banco");
	    deleteDatabaseButton.setEnabled(false);
	    toolBar.add(deleteDatabaseButton);
	    
	    openDatabaseButton = makeNavigationButton("database_conf", OPEN_FROM_DATABASE_EVT, "Editar trilhas salvas no banco");
	    toolBar.add(openDatabaseButton);
	    
	    statisticsButton = makeNavigationButton("calc", STATISTICS_EVT, "Ver estatística");
	    toolBar.add(statisticsButton);
	}
	
	protected JButton makeNavigationButton(String imageName,String actionCommand,String toolTipText) {
		String imgLocation = "images/" + imageName + ".png";
		
		JButton button = new JButton();
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
		button.addActionListener(this);
		button.setIcon(new ImageIcon(imgLocation));
		button.setFocusPainted(false);

		return button;
	}
	
	private void initializarIcons(){
	    try {
	         BufferedImage img = ImageIO.read(new File("images/markera.png"));
	         Image dimg =  img.getScaledInstance(24, 24,
	        	        Image.SCALE_SMOOTH);
	         markerA = new ImageIcon(dimg);
	         
	         BufferedImage img2 = ImageIO.read(new File("images/markerb.png"));
	         dimg =  img2.getScaledInstance(24, 24,
	        	        Image.SCALE_SMOOTH);
	         markerB = new ImageIcon(dimg);
	      } catch (IOException e) {
	         e.printStackTrace();
	      }
	}
	
	private void initializeChart(){
		XYSeries series = new XYSeries("Profile");
		
		int i = 0;
		double maxAltitude = -1;
		double minAltitude = 9000;
		//double distance = 0;
		List<TPLocation> locs = Session.currentTrail;
		TPLocation loc = locs.get(0);
		Iterator<TPLocation> it = locs.iterator();
		
		while(it.hasNext()){
			//distance = distance + GeoUtils.computeDistance(loc.getLatitude(), loc.getLongitude(), lastLoc.getLatitude(), lastLoc.getLongitude());
			
			if(loc.getAltitude() > maxAltitude){
				maxAltitude = loc.getAltitude();
			}
			
			if(loc.getAltitude() < minAltitude){
				minAltitude = loc.getAltitude();
			}

			series.add(i, loc.getAltitude());
			
			loc = it.next();
			i++;
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
        
        makeSplitPanelOnTheFly();
	}

	private void initializeMap(){
		// Create a TileFactoryInfo for OpenStreetMap
		TileFactoryInfo info =  new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.SATELLITE);
		DefaultTileFactory tileFactory = new DefaultTileFactory(info);
		tileFactory.setThreadPoolSize(8);
		
		// Setup local file cache
		File cacheDir = new File(System.getProperty("user.home") + File.separator + ".jxmapviewer2");
		LocalResponseCache.installResponseCache(info.getBaseURL(), cacheDir, false);
		
		// Setup JXMapViewer
		mapViewer = new JXMapViewer();
		mapViewer.setTileFactory(tileFactory);
	
		// Add interactions
		MouseInputListener mia = new PanMouseInputListener(mapViewer);
		mapViewer.addMouseListener(mia);
		mapViewer.addMouseMotionListener(mia);

		mapViewer.addMouseListener(new CenterMapListener(mapViewer));
		mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));
		mapViewer.addKeyListener(new PanKeyListener(mapViewer));
		
		// Add a selection painter
		SelectionAdapter sa = new SelectionAdapter(mapViewer); 
		SelectionPainter sp = new SelectionPainter(sa); 
		mapViewer.addMouseMotionListener(sa); 
		mapViewer.addMouseListener(sa); 
		mapViewer.setOverlayPainter(sp);
		
		mapViewer.setPreferredSize(new java.awt.Dimension(500, 440));
		
		GeoPosition initPos = new GeoPosition(-15,-40);
		mapViewer.setZoom(16);
		mapViewer.setAddressLocation(initPos);
	}
	
	private void useProxy(){
		Properties systemSettings = System.getProperties();
		systemSettings.put("http.proxyHost", "localhost");
		systemSettings.put("http.proxyPort", "40080");
		

		Authenticator.setDefault(
		   new Authenticator() {
		      @Override
		      public PasswordAuthentication getPasswordAuthentication() {
		         return new PasswordAuthentication(
		               authUser, authPassword.toCharArray());
		      }
		   }
		);

		System.setProperty("http.proxyUser", authUser);
		System.setProperty("http.proxyPassword", authPassword);
	}
	
	private void fitMap(){
		Set<GeoPosition> pos = new HashSet<GeoPosition>();
		
		for(TPLocation l: Session.currentTrail){
			pos.add(new GeoPosition(l.getLatitude(), l.getLongitude()));
		}
		
		mapViewer.zoomToBestFit(pos, 0.7);
	}
	
	private WaypointPainter<SwingWaypoint> getWaypointsPainter(List<TPLocation> locs){
		Set<SwingWaypoint> waypoints = null;
		
		if(locs != null && locs.size() > 0){
			GeoPosition start = new GeoPosition(locs.get(0).getLatitude(), locs.get(0).getLongitude());
			
			if(locs.size() > 1){
				GeoPosition end = new GeoPosition(locs.get(locs.size() - 1).getLatitude(),
						locs.get(locs.size() - 1).getLongitude());
				
				waypoints = new HashSet<SwingWaypoint>(
						Arrays.asList(new SwingWaypoint(markerA, start),new SwingWaypoint(markerB, end)));
			}else{
				waypoints = new HashSet<SwingWaypoint>(
						Arrays.asList(new SwingWaypoint(markerA, start)));
			}
			
			WaypointPainter<SwingWaypoint> waypointPainter = new SwingWaypointOverlayPainter();
			waypointPainter.setWaypoints(waypoints);
			
			if(waypoints != null){
		        for (SwingWaypoint w : waypoints) {
		            mapViewer.add(w.getImg());
		        }
			}
			
			return waypointPainter;
		}
		
		return null;
	}
	
	private void drawProfileSelection(List<TPLocation> locs, Color color, boolean foreground){
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
	
	private void clearProfileSelections(){
		XYPlot xyPlot = (XYPlot) chart.getPlot();
		xyPlot.clearDomainMarkers();
	}
	
	private void drawStretch(List<TPLocation> trail){
		mapViewer.removeAll();
		clearProfileSelections();
		
		List<Painter<JXMapViewer>> painters = new ArrayList<Painter<JXMapViewer>>();
				
		List<TPLocation> stretch = new ArrayList<TPLocation>();
		List<TPLocation> selected = new ArrayList<TPLocation>();
		
		TPLocation lastLoc = trail.get(0);
		
		for(TPLocation loc: trail){
			if(loc.isSelected()){
				loc.setSelected(false);
				selected.add(loc);
			}
			
			if(lastLoc.getType() != loc.getType()){
				if(lastLoc.getType() == LocationType.TRAIL){
					painters.add(new RoutePainter(stretch, Color.GREEN));
				}else if(lastLoc.getType() == LocationType.ROAD){
					painters.add(new RoutePainter(stretch, Color.GRAY));
					drawProfileSelection(stretch, Color.GRAY, false);
				}else if(lastLoc.getType() == LocationType.RIVER){
					painters.add(new RoutePainter(stretch, Color.BLUE));
					drawProfileSelection(stretch, Color.BLUE, false);
				}else if(lastLoc.getType() == LocationType.SNOW){
					painters.add(new RoutePainter(stretch, Color.WHITE));
					drawProfileSelection(stretch, Color.WHITE, false);
				}else if(lastLoc.getType() == LocationType.FOREST){
					painters.add(new RoutePainter(stretch, new Color(0, 100, 0)));
					drawProfileSelection(stretch, new Color(0, 100, 0), false);
				}else if(lastLoc.getType() == LocationType.INVALID){
					painters.add(new RoutePainter(stretch, Color.BLACK));
					drawProfileSelection(stretch, Color.BLACK, true);
				}
				
				stretch.clear();
				stretch.add(lastLoc);
			}
				
			stretch.add(loc);
			
			lastLoc = loc;
		}
		
		if(lastLoc.getType() == LocationType.TRAIL){
			painters.add(new RoutePainter(stretch, Color.GREEN));
		}else if(lastLoc.getType() == LocationType.ROAD){
			painters.add(new RoutePainter(stretch, Color.GRAY));
			drawProfileSelection(stretch, Color.GRAY, false);
		}else if(lastLoc.getType() == LocationType.RIVER){
			painters.add(new RoutePainter(stretch, Color.BLUE));
			drawProfileSelection(stretch, Color.BLUE, false);
		}else if(lastLoc.getType() == LocationType.SNOW){
			painters.add(new RoutePainter(stretch, Color.WHITE));
			drawProfileSelection(stretch, Color.WHITE, false);
		}else if(lastLoc.getType() == LocationType.FOREST){
			painters.add(new RoutePainter(stretch, new Color(0, 100, 0)));
			drawProfileSelection(stretch, new Color(0, 100, 0), false);
		}else if(lastLoc.getType() == LocationType.INVALID){
			painters.add(new RoutePainter(stretch, Color.BLACK));
			drawProfileSelection(stretch, Color.BLACK, true);
		}

		if(selected.size() > 0){
			painters.add(new RoutePainter(selected, Color.RED));
			drawProfileSelection(selected, Color.RED, false);
			painters.add(getWaypointsPainter(selected));
		}
		
		CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(painters);
		mapViewer.setOverlayPainter(painter);
	}

	private int getDomainFromClick(ChartMouseEvent e){
		if(e.getEntity() instanceof XYItemEntity){
			XYItemEntity ce = (XYItemEntity) e.getEntity();
			return (int) ce.getDataset().getX(ce.getSeriesIndex(),  ce.getItem()).doubleValue();
		}else if(e.getEntity() instanceof PlotEntity){
			XYPlot xyplot = (XYPlot) e.getChart().getPlot();
			Point2D p = e.getTrigger().getPoint();
			Rectangle2D plotArea = chartPanel.getScreenDataArea();
			return (int) xyplot.getDomainAxis().java2DToValue(p.getX(), plotArea, xyplot.getDomainAxisEdge());
		}
		
		return (Session.currentTrail.size() - 1);
	}
	
	private void setSelection(int start, int end){
		if(end == -1 || start == -1 || end < start)
			return;
		
		List<TPLocation> locs = new ArrayList<TPLocation>();
		TPLocation currLoc;
		
		for(int i = start; i <= end; i++){
			currLoc = Session.currentTrail.get(i);
			currLoc.setSelected(true);
			locs.add(currLoc);
		}
		
		if(start != end)
			showNumberStatisticsPanel(StatisticsUtil.calculateStats(locs));
		
		drawStretch(Session.currentTrail);
	}
	
	@Override
	public void chartMouseClicked(ChartMouseEvent e) {
		if(wasMouseRightClickEvent || escapeMouseLeftClickEvent)
			return;

		if(firstClick){ //Usuário deu o primeiro click
			escapeMouseMoveEvent = false;
			selStart = getDomainFromClick(e);
			selEnd = selStart;
		}else{
			selectTypeButton.setEnabled(true);
			escapeMouseLeftClickEvent = true;
			escapeMouseMoveEvent = true; //Impede que movimentos do mouse apaguem a seleção atual
			selEnd = getDomainFromClick(e);
			
			setSelection(selStart, selEnd);
		}
		
		firstClick = !firstClick;
	}
	
	@Override
	public void chartMouseMoved(ChartMouseEvent e) {
		if(escapeMouseMoveEvent)
			return;
		
		selEnd = getDomainFromClick(e);
		
		if(firstClick){
			setSelection(selEnd,selEnd);
			return;
		}
		
		setSelection(selStart, selEnd);
	}

	private void clearSelection(){
		setSelection(selEnd,selEnd);
		firstClick = true;
		selStart = -1;
		selEnd = -1;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}
	
	private void backToNormalStateOfSelection(){
		selectTypeButton.setEnabled(false);
		clearSelection();
		escapeMouseMoveEvent = false;
		escapeMouseLeftClickEvent = false;
		wasMouseRightClickEvent = true;
		
		showNumberStatisticsPanel(trailStatistics);
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if(SwingUtilities.isRightMouseButton(e)){ //Botão direito sempre apaga a seleção
			backToNormalStateOfSelection();
		}else{
			wasMouseRightClickEvent = false;
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

	private void openFile(){
		final JFileChooser fc = new JFileChooser();
		FileFilter kmlFilter = new ExtensionFileFilter("kml", new String[] { "kml" });
		
		for(FileFilter f: fc.getChoosableFileFilters())
			fc.removeChoosableFileFilter(f);

		fc.addChoosableFileFilter(kmlFilter);
	    
		int returnVal = fc.showOpenDialog(Main.this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
        	Session.currentSourceFile = fc.getSelectedFile();
        	
        	ParseKmlTask task = new ParseKmlTask();                
            task.start();
        }
	}
	
	private void openMarkStretchDialog(){
		JDialog dialog = new MarkStretchView(selStart, selEnd, new StretchTypeChangeListener(){
			@Override
			public void onStretchTypeChanged(LocationType type) {
				if(type == LocationType.INVALID)
					trailStatistics = StatisticsUtil.calculateStats(Session.currentTrail);

				backToNormalStateOfSelection();
			}
		});
		
		dialog.setLocationRelativeTo(this);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
		dialog.setResizable(false);
		dialog.pack();
	}
	
	private void eraseAllSelections(){
		int res = JOptionPane.showConfirmDialog(this, "Isso removerá todas as marcações que você fez nessa trilha. Continuar?", "Remover Marcações", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		
		if(res != JOptionPane.OK_OPTION)
			return;
		
		for(TPLocation loc: Session.currentTrail){
			loc.setType(LocationType.TRAIL);
		}
		
		trailStatistics = StatisticsUtil.calculateStats(Session.currentTrail);
		showNumberStatisticsPanel(trailStatistics);
		drawStretch(Session.currentTrail);
		
		selectTypeButton.setEnabled(false);
		escapeMouseMoveEvent = false;
		escapeMouseLeftClickEvent = false;
		wasMouseRightClickEvent = true; 
	}
	
	private String getNameFromFile(File file){
		if(file.getName().endsWith(".kml"))
			return file.getName().replace(".kml", "");
		else
			return file.getName().replace(".mtl", "");
	}
	
	private void insertCurrentTrailInDatabase(){
		String trailName = getNameFromFile(Session.currentSourceFile);
		
		if(db.contains(trailName)){
			int res = JOptionPane.showConfirmDialog(this, "Essa trilha já existe no banco. Deseja substituir?", "Substituição", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if(res != JOptionPane.YES_OPTION)
				return;
		}

		db.insert(trailName, Session.currentTrail);
		
		deleteDatabaseButton.setEnabled(true);
		
		JOptionPane.showConfirmDialog(this, "A trilha foi inserida com sucesso.", "Inserção Finalizada", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
	}
	
	private void deleteCurrentTrailInDatabase(){
		int res = JOptionPane.showConfirmDialog(this, "Você está prestes a remover permanentemente essa trilha do banco. Continuar?", "Remoção", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		
		if(res != JOptionPane.YES_OPTION)
			return;
		
		String trailName = getNameFromFile(Session.currentSourceFile);

		db.delete(trailName);
		
		deleteDatabaseButton.setEnabled(false);
		
		JOptionPane.showConfirmDialog(this, "A trilha foi removida do banco mas você ainda pode mexer nela.", "Remoção Finalizada", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
	}
	
	private void openFromDatabase(){
		final JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("./database"));
		
		FileFilter kmlFilter = new ExtensionFileFilter("mtl", new String[] { "mtl" });
		
		for(FileFilter f: fc.getChoosableFileFilters())
			fc.removeChoosableFileFilter(f);

		fc.addChoosableFileFilter(kmlFilter);
	    
		int returnVal = fc.showOpenDialog(Main.this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
        	Session.currentSourceFile = fc.getSelectedFile();
        	
			Session.currentTrail = db.load(getNameFromFile(Session.currentSourceFile));
			
    	    initializeChart();
    	    
    	    insertDatabaseButton.setEnabled(true);
    	    deleteDatabaseButton.setEnabled(true);
    	    eraseSelectionsButton.setEnabled(true);
    	    
    	    //Desenha toda a track apenas
    	    drawStretch(Session.currentTrail);
    	    
    		fitMap();
    		mapViewer.setZoom(4);
    		
    		Session.currentKML = null;
        }
	}
	
	private void calculateStatistics(){
		try {
			String [] names = db.getAllTrailsNames();
			TableOfSpeeds speedTable = StatisticsUtil.calculateTableOfSpeeds(db.load(names[0]));
			TableOfSpeeds speedTable2;
			
			for(int i = 1; i < names.length; i++){
				speedTable2 = StatisticsUtil.calculateTableOfSpeeds(db.load(names[i]));
				speedTable.setCounts(MathOperation.sumMatrix(speedTable.getCounts(), speedTable2.getCounts()));
				speedTable.setSpeeds(MathOperation.sumMatrix(speedTable.getSpeeds(), speedTable2.getSpeeds()));
			}
			
			double [][] avgSpeedTable = MathOperation.divideMatrix(speedTable.getSpeeds(), speedTable.getCounts());
			
			SpeedPerInclinationGraph graph = new SpeedPerInclinationGraph(avgSpeedTable);
			graph.setLocationRelativeTo(null);
			graph.pack();
			graph.setVisible(true);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(OPEN_EVT)){
			openFile();
		}else if(e.getActionCommand().equals(EDIT_STRETCH_EVT)){
			openMarkStretchDialog();
		}else if(e.getActionCommand().equals(ERASE_STRETCHS_EVT)){
			eraseAllSelections();
		}else if(e.getActionCommand().equals(SAVE_IN_DATABASE_EVT)){
			insertCurrentTrailInDatabase();
		}else if(e.getActionCommand().equals(OPEN_FROM_DATABASE_EVT)){
			openFromDatabase();
		}else if(e.getActionCommand().equals(DELETE_IN_DATABASE_EVT)){
			deleteCurrentTrailInDatabase();
		}else if(e.getActionCommand().equals(STATISTICS_EVT)){
			calculateStatistics();
		}
	}
	
    private class ParseKmlTask extends Thread {
	      public ParseKmlTask(){
	      }
	      
	      public Kml getKml(File file) throws Exception {
	    	    String str = FileUtils.readFileToString(file, "ISO-8859-1");
	    	    str = str.replace("xmlns=\"http://earth.google.com/kml/2.2\"", "xmlns=\"http://www.opengis.net/kml/2.2\"" );
	    	    ByteArrayInputStream bais = new ByteArrayInputStream( str.getBytes( "UTF-8" ) );
	    	    return Kml.unmarshal(bais);
	      }

	      public void run(){
	    	    progressBar.setVisible(true);
	        	try {
					Session.currentKML = getKml(Session.currentSourceFile);
				} catch (Exception e) {
					e.printStackTrace();
				}
	    		
	    		Session.currentTrail = KmlUtils.getAllPlacemarks(Session.currentKML, new KmlParseProgressListener(){
					@Override
					public void onPreParse(int progressTotal) {
						 SwingUtilities.invokeLater(new Runnable() {
				                @Override
				                public void run() {
				                	progressBar.setIndeterminate(false);
									progressBar.setVisible(true);
									progressBar.setMaximum(progressTotal);
				                }
				         });
					}

					@Override
					public void onParseProgress(int progress) {
						 SwingUtilities.invokeLater(new Runnable() {
				                @Override
				                public void run() {
				                	progressBar.setValue(progress);
				                }
				         });
					}

					@Override
					public void onParseFinish() {
						 SwingUtilities.invokeLater(new Runnable() {
				                @Override
				                public void run() {
				                	progressBar.setVisible(false);
				                }
				         });
					}
	    		});
	    		
	    	    initializeChart();
	    	    
	    	    insertDatabaseButton.setEnabled(true);
	    	    deleteDatabaseButton.setEnabled(false);
	    	    eraseSelectionsButton.setEnabled(true);
	    	    
	    	    //Desenha toda a track apenas
	    	    drawStretch(Session.currentTrail);
	    	    
	    		fitMap();
	    		mapViewer.setZoom(4);
	      }
	   }   
}
