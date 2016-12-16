package view;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Rectangle;

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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.event.MouseInputListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
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
import database.DatabaseManager2;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import model.LocationType;
import model.Statistics;
import model.StretchType;
import model.TPLocation2;
import model.TPLocation;
import model.TypeConstants;
import utils.KmlUtils;
import utils.NetworkUtils;
import utils.StatisticsUtil;
import utils.listeners.KmlParseProgressListener;
import view.factory.ToolbarButtonFactory;
import view.filters.ExtensionFileFilter;
import view.graphs.SpeedPerInclinationGraph;
import view.listeners.DatabaseTrailDeletedListener;
import view.listeners.StretchTypeChangeListener;
import view.painters.RoutePainter;
import view.painters.SelectionPainter;
import view.painters.SwingWaypointOverlayPainter;
import view.popup.TreePopup;
import view.waypoint.SwingWaypoint;
import view.widgets.CustomTreeCellRenderer;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.JTree;

public class Main extends JFrame implements ChartMouseListener, MouseListener, ActionListener {
	private static final long serialVersionUID = -8622730863892625194L;
	
	private static final int LOESS_INTERPOLATOR = 1;
	private static final int POLYNOMIAL_FITTING = 2;
	
	private static final int WINDOW_WIDTH = 1080;
	private static final int WINDOW_HEIGHT = 690;
	private static final int TREE_MINIMUM_WIDTH = 250;
	private static final int SPLIT_MINIMUM_WIDTH = WINDOW_WIDTH - TREE_MINIMUM_WIDTH;
	
	private static final int STEPS = 4;
	
	private static final String TREE_ROOT = "Banco de Tracks";
	
	private static final String OPEN_EVT = "open";
	private static final String EDIT_STRETCH_EVT = "edit_stretch";
	private static final String ERASE_STRETCHS_EVT = "erase_stretchs";
	private static final String STYLES_MANAGER_EVT = "styles_manager";
	private static final String SAVE_IN_DATABASE_EVT = "save_in_database";
	private static final String OPEN_DATABASE_EVT = "open_database";
	private static final String DELETE_IN_DATABASE_EVT = "delete_in_database";
	private static final String STATISTICS_EVT = "calculate_statistics";
	private static final String LOCATION_EVT = "location";
	
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
	
	private boolean databaseIsOpened = true;
	
	private JSplitPane splitPanelVertical;
	private JSplitPane splitPanelHorizontal;
	
	private JToolBar mainToolbar;
	private JProgressBar progressBar;
	
	private JButton locateButton;
	private JButton selectTypeButton;
	private JButton eraseSelectionsButton;
	private JButton stylesManagerButton;
	private JButton statisticsButton;
	private JButton saveButton;
	
	private JLabel labelDistance;
	private JLabel labelElevation;
	private JLabel labelElevationGainLoss;
	private JLabel labelInclinationAvg;
	private JLabel labelInclinationMax;
	
	private Statistics trailStatistics;
	private JTree tree;
	private DefaultMutableTreeNode rootNode;
	private int currentIndexNodeSelected = -1;
	
	private Session session;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
					//frame.hideTree();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Main() {
		//convertToModel2();
		session = Session.getInstance();
		
		setTitle("Tracker");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(new ImageIcon("images/logo.png").getImage());
		setBounds(100, 500, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		NetworkUtils.useProxy();
		initializarIcons();
		initializeMap();
		
		mainToolbar = new JToolBar();
		mainToolbar.setFloatable(false);
		addButtonsToMainToolbar(mainToolbar);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		progressBar = new JProgressBar();
		progressBar.setPreferredSize(new Dimension(1000,20));
		progressBar.setForeground(new Color(221,141,22));
		progressBar.setIndeterminate(true);
		progressBar.setVisible(false);
		
		rootNode = new DefaultMutableTreeNode(TREE_ROOT);
		initializeTree(rootNode);
		
		tree = new JTree(rootNode);
		tree.setCellRenderer(new CustomTreeCellRenderer());
		
		tree.setMinimumSize(new Dimension(TREE_MINIMUM_WIDTH, WINDOW_HEIGHT));
		tree.addMouseListener(this);
		
		splitPanelHorizontal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPanelHorizontal.setOneTouchExpandable(false);
		splitPanelHorizontal.setDividerSize(3);
		splitPanelHorizontal.add(tree);
		splitPanelHorizontal.add(mapViewer);
		
		contentPane.setLayout(new BorderLayout(0, 0));
		contentPane.add(mainToolbar, BorderLayout.NORTH);
		contentPane.add(splitPanelHorizontal, BorderLayout.CENTER);
		contentPane.add(progressBar, BorderLayout.SOUTH);
		
		labelDistance = makeStatLabel();
		labelElevation = makeStatLabel();
		labelElevationGainLoss = makeStatLabel();
		labelInclinationAvg = makeStatLabel();
		labelInclinationMax = makeStatLabel();
	}
	
	@SuppressWarnings("unused")
	private void convertToModel2(){
		DatabaseManager2 db2 = DatabaseManager2.getInstance();
		
		String names [] = db2.getAllTrailsNames();
		List<TPLocation> locsOld;
		List<TPLocation2> locsNew = new ArrayList<TPLocation2>();
		TPLocation2 newLoc;
		
		for(int i = 0; i < names.length; i++){
			locsOld = db2.load(names[i]);
			
			if(locsOld == null){
				db.delete(names[i]);
				continue;
			}
			
			for(TPLocation oldLoc: locsOld){
				newLoc = new TPLocation2();
				newLoc.setId(oldLoc.getId());
				newLoc.setLatitude(oldLoc.getLatitude());
				newLoc.setLongitude(oldLoc.getLongitude());
				newLoc.setAltitude(oldLoc.getAltitude());
				
				if(oldLoc.getType() == LocationType.INVALID)
					newLoc.setTypeId(TypeConstants.FIXED_TYPE_INVALID);
				else if(oldLoc.getType() == LocationType.TRAIL)
					newLoc.setTypeId(TypeConstants.FIXED_TYPE_TRAIL);
				else if(oldLoc.getType() == LocationType.ROAD)
					newLoc.setTypeId(TypeConstants.FIXED_TYPE_ROAD);
				else if(oldLoc.getType() == LocationType.RIVER)
					newLoc.setTypeId(TypeConstants.FIXED_TYPE_RIVER);
				else if(oldLoc.getType() == LocationType.SNOW)
					newLoc.setTypeId(TypeConstants.FIXED_TYPE_SNOW);
				else if(oldLoc.getType() == LocationType.FOREST)
					newLoc.setTypeId(TypeConstants.FIXED_TYPE_TRAIL);
				
				newLoc.setWhen(oldLoc.getWhen());
				newLoc.setSelected(oldLoc.isSelected());
				
				locsNew.add(newLoc);
			}
			
			db.delete(names[i]);
			db.insert(names[i], locsNew);
			locsNew.clear();
		}
	}
	
	private void initializeTree(DefaultMutableTreeNode track){
	    String[] names = db.getAllTrailsNames();
	    
	    if(names == null)
	    	return;
	    
	    Arrays.sort(names);
	    
	    for(int i = 0; i < names.length; i++)
	    	rootNode.add(new DefaultMutableTreeNode(names[i]));
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
		
		labelElevation.setText("Elev. Min Méd Max: " + new DecimalFormat("#").format(stats.getMinElevation()) + "m  " + 
				new DecimalFormat("#").format(stats.getAvgElevation()) + "m  " + new DecimalFormat("#").format(stats.getMaxElevation()) + "m");

		labelElevationGainLoss.setText("Ganho/Perda de Elev.: " + new DecimalFormat("#").format(stats.getElevationGain())
				+ "m  " + new DecimalFormat("#").format(stats.getElevationLoss()) + "m");
		
		labelInclinationMax.setText("Incl. Máxima: " + new DecimalFormat("#.#").format(stats.getMaxInclinationPositive()*pC) + "%  "
				+ new DecimalFormat("#.#").format(stats.getMaxInclinationNegative()*pC) + "%");
		
		labelInclinationAvg.setText("Incl. Média: " + new DecimalFormat("#.#").format(stats.getAvgInclinationPositive()*pC) + "%  " 
				+ new DecimalFormat("#.#").format(stats.getAvgInclinationNegative()*pC) + "%");
	}
	
	private void makeSplitPanelOnTheFly(){
		contentPane.removeAll();
		
		splitPanelVertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPanelVertical.setDividerSize(3);
		splitPanelVertical.setOneTouchExpandable(false);
		splitPanelVertical.add(mapViewer);
		
		JPanel numbersPanel = new JPanel();
		numbersPanel.setOpaque(true);
		numbersPanel.setBackground(Color.BLACK);
		numbersPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		trailStatistics = StatisticsUtil.calculateStats(session.getCurrentTrail());
		showNumberStatisticsPanel(trailStatistics);
		
		numbersPanel.add(labelDistance);
		numbersPanel.add(Box.createRigidArea(new Dimension(5,0)));
		numbersPanel.add(labelElevation);
		numbersPanel.add(Box.createRigidArea(new Dimension(5,0)));
		numbersPanel.add(labelElevationGainLoss);
		numbersPanel.add(Box.createRigidArea(new Dimension(5,0)));
		numbersPanel.add(labelInclinationMax);
		numbersPanel.add(Box.createRigidArea(new Dimension(5,0)));
		numbersPanel.add(labelInclinationAvg);
		
		JPanel statsPanel = new JPanel();
		statsPanel.setLayout(new BorderLayout(0, 0));
		statsPanel.add(numbersPanel, BorderLayout.NORTH);
		statsPanel.add(chartPanel, BorderLayout.CENTER);
		
		splitPanelVertical.add(statsPanel);
		splitPanelVertical.setMinimumSize(new Dimension(SPLIT_MINIMUM_WIDTH, WINDOW_HEIGHT));
		
		contentPane.add(mainToolbar, BorderLayout.NORTH);
		
		splitPanelHorizontal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPanelHorizontal.setDividerSize(3);
		splitPanelHorizontal.setOneTouchExpandable(false);
		
		splitPanelHorizontal.add(tree);
		splitPanelHorizontal.add(splitPanelVertical);
		
		contentPane.add(splitPanelHorizontal, BorderLayout.CENTER);
		
        contentPane.revalidate(); 
        contentPane.repaint();

        //splitPanelHorizontal.setResizeWeight(0.7f);
		splitPanelVertical.setResizeWeight(0.7f);
        
		showTree();
	}
	
	public void showTree(){
		splitPanelHorizontal.setDividerLocation(TREE_MINIMUM_WIDTH);
		splitPanelHorizontal.setEnabled(true);
	}
	
	public void hideTree(){
		splitPanelHorizontal.setDividerLocation(0);
		splitPanelHorizontal.setEnabled(false);
	}
	
	@SuppressWarnings("serial")
	protected void addButtonsToMainToolbar(JToolBar toolBar) {
	    JButton button = null;

	    button = ToolbarButtonFactory.makeNavigationButton("open", OPEN_EVT, "Abrir um KML", this);
	    toolBar.add(button);
	    
	    saveButton = ToolbarButtonFactory.makeNavigationButton("save", SAVE_IN_DATABASE_EVT, "Salvar no Banco de Trilhas", this);
	    saveButton.setEnabled(false);
	    toolBar.add(saveButton);
	    
	    button = ToolbarButtonFactory.makeNavigationButton("database_conf", OPEN_DATABASE_EVT, "Abrir Banco de Trilhas", this);
	    toolBar.add(button);
	    
	    toolBar.add(Box.createRigidArea(new Dimension(25,0)));
	    
	    locateButton = ToolbarButtonFactory.makeNavigationButton("location", LOCATION_EVT, "Move o a visualização para o início da trilha", this);
	    locateButton.setEnabled(false);
	    toolBar.add(locateButton);
	    
	    toolBar.add(Box.createRigidArea(new Dimension(25,0)));
	    
	    selectTypeButton = ToolbarButtonFactory.makeNavigationButton("foot", EDIT_STRETCH_EVT, "Editar trecho", this);
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
	    
	    eraseSelectionsButton = ToolbarButtonFactory.makeNavigationButton("erase", ERASE_STRETCHS_EVT, "Apagar todas as marcaï¿½ï¿½es da trilha", this);
	    eraseSelectionsButton.setEnabled(false);
	    toolBar.add(eraseSelectionsButton);
	    
	    stylesManagerButton = ToolbarButtonFactory.makeNavigationButton("stylesmanager", STYLES_MANAGER_EVT, "Gerenciar Tipos Trecho", this);
	    toolBar.add(stylesManagerButton);
	    
	    toolBar.add(Box.createRigidArea(new Dimension(25,0)));	

	    statisticsButton = ToolbarButtonFactory.makeNavigationButton("calc", STATISTICS_EVT, "Ver estatï¿½stica", this);
	    toolBar.add(statisticsButton);
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
		
		double i = 0;
		double maxAltitude = -1;
		double minAltitude = 9000;
		List<TPLocation2> locs = session.getCurrentTrail();
		TPLocation2 loc = locs.get(0);
		Iterator<TPLocation2> it = locs.iterator();
		
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
		
		mapViewer.setPreferredSize(new java.awt.Dimension(300, 440));
	
		resetMap();
	}
	
	private void resetMap(){
		mapViewer.removeAll();
		mapViewer.setOverlayPainter(null);
		GeoPosition initPos = new GeoPosition(-15,-40);
		mapViewer.setZoom(16);
		mapViewer.setAddressLocation(initPos);
	}
	
	private void fitMap(){
		Set<GeoPosition> pos = new HashSet<GeoPosition>();
		
		for(TPLocation2 l: session.getCurrentTrail()){
			pos.add(new GeoPosition(l.getLatitude(), l.getLongitude()));
		}
		
		mapViewer.zoomToBestFit(pos, 0.7);
	}
	
	private WaypointPainter<SwingWaypoint> getWaypointsPainter(List<TPLocation2> locs){
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
	
	private void drawProfileSelection(List<TPLocation2> locs, Color color, boolean foreground){
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

	private void drawStretch(List<TPLocation2> trail){
		mapViewer.removeAll();
		clearProfileSelections();

		List<Painter<JXMapViewer>> painters = new ArrayList<Painter<JXMapViewer>>();
	
		List<TPLocation2> stretch = new ArrayList<TPLocation2>();
		List<TPLocation2> selected = new ArrayList<TPLocation2>();

		TPLocation2 lastLoc = trail.get(0);

		for(TPLocation2 loc: trail){
			if(loc.isSelected()){
				loc.setSelected(false);
				selected.add(loc);
			}

			if(!lastLoc.getTypeId().equals(loc.getTypeId())){
				StretchType type = session.getStretchTypes().get(lastLoc.getTypeId());
				painters.add(new RoutePainter(stretch, type.getColor()));
				
				if(!lastLoc.getTypeId().equals(TypeConstants.FIXED_TYPE_TRAIL)){
					if(lastLoc.getTypeId().equals(TypeConstants.FIXED_TYPE_INVALID))
						drawProfileSelection(stretch, type.getColor(), true);
					else
						drawProfileSelection(stretch, type.getColor(), false);
				}

				stretch.clear();
				stretch.add(lastLoc);
			}

			stretch.add(loc);

			lastLoc = loc;
		}

		StretchType type = session.getStretchTypes().get(lastLoc.getTypeId());
		painters.add(new RoutePainter(stretch, type.getColor()));

		if(!lastLoc.getTypeId().equals(TypeConstants.FIXED_TYPE_TRAIL)){
			if(lastLoc.getTypeId().equals(TypeConstants.FIXED_TYPE_INVALID))
				drawProfileSelection(stretch, type.getColor(), true);
			else
				drawProfileSelection(stretch, type.getColor(), false);
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
		
		return (session.getCurrentTrail().size() - 1);
	}
	
	private void setSelection(int start, int end){
		if(end == -1 || start == -1 || end < start)
			return;
		
		List<TPLocation2> locs = new ArrayList<TPLocation2>();
		TPLocation2 currLoc;
		
		for(int i = start; i <= end; i++){
			currLoc = session.getCurrentTrail().get(i);
			currLoc.setSelected(true);
			locs.add(currLoc);
		}
		
		if(start != end)
			showNumberStatisticsPanel(StatisticsUtil.calculateStats(locs));
		
		drawStretch(session.getCurrentTrail());
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
			selectTypeButton.setEnabled(true);
			escapeMouseLeftClickEvent = true;
			escapeMouseMoveEvent = true; //Impede que movimentos do mouse apaguem a seleï¿½ï¿½o atual
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
	public void mouseClicked(MouseEvent e) {
		if(e.getSource() instanceof JTree){
			if(e.getClickCount() == 1){
		        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		        
		        currentIndexNodeSelected = rootNode.getIndex(selectedNode);
				
		        if(selectedNode == null)
		        	return;
		        
		        openFromDatabase(selectedNode.getUserObject().toString());
		        saveButton.setEnabled(true);
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
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
		if(e.getSource() instanceof JTree){
			if(SwingUtilities.isRightMouseButton(e)){
			    TreePath path = tree.getPathForLocation(e.getX(), e.getY());
	            Rectangle pathBounds = tree.getUI().getPathBounds(tree, path);
	            
	            if (pathBounds != null && pathBounds.contains(e.getX(), e.getY())){
	            	tree.setSelectionPath(path);
	            	
			        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			        
			        if(currentIndexNodeSelected != rootNode.getIndex(selectedNode))
			        	return;

			        if(selectedNode == null || selectedNode.getUserObject().equals(TREE_ROOT))
			        	return;
	            	
	            	TreePopup pop = new TreePopup(this, tree, new DatabaseTrailDeletedListener(){
						@Override
						public void onDatabaseTrailDeleted() {
							resetMap();
						}
	            	});	            	
	            	pop.show(tree, e.getX(), e.getY());
	            }
			}
		}else{
			if(SwingUtilities.isRightMouseButton(e)){ //Botï¿½o direito sempre apaga a seleï¿½ï¿½o
				backToNormalStateOfSelection();
			}else{
				wasMouseRightClickEvent = false;
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	private void openFile(){
		final JFileChooser fc = new JFileChooser();
		FileFilter kmlFilter = new ExtensionFileFilter("kml", new String[] { "kml" });
		
		for(FileFilter f: fc.getChoosableFileFilters())
			fc.removeChoosableFileFilter(f);

		fc.addChoosableFileFilter(kmlFilter);
	    
		int returnVal = fc.showOpenDialog(Main.this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
        	session.setCurrentSourceFile(fc.getSelectedFile());
        	
        	ParseKmlTask task = new ParseKmlTask();                
            task.start();
        }
	}
	
	private void openMarkStretchDialog(){
		JDialog dialog = new MarkStretchView(selStart, selEnd, new StretchTypeChangeListener(){
			@Override
			public void onStretchTypeChanged(String typeId) {
				if(typeId.equals(TypeConstants.FIXED_TYPE_INVALID))
					trailStatistics = StatisticsUtil.calculateStats(session.getCurrentTrail());

				backToNormalStateOfSelection();
			}
		});
		
		dialog.setLocationRelativeTo(this);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
		dialog.setResizable(false);
	}
	
	private void eraseAllSelections(){
		int res = JOptionPane.showConfirmDialog(this, "Isso removerá todas as marcações que você fez nessa trilha. Continuar?", "Remover Marcações", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		
		if(res != JOptionPane.OK_OPTION)
			return;
		
		for(TPLocation2 loc: session.getCurrentTrail())
			loc.setTypeId(TypeConstants.FIXED_TYPE_TRAIL);
		
		trailStatistics = StatisticsUtil.calculateStats(session.getCurrentTrail());
		showNumberStatisticsPanel(trailStatistics);
		drawStretch(session.getCurrentTrail());
		
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
		String trailName;
		
		if(session.getCurrentSourceFile() == null){
	        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
	
	        if(selectedNode == null)
	        	return;
	        
			trailName = selectedNode.getUserObject().toString();
		}else{
			trailName = getNameFromFile(session.getCurrentSourceFile());
		}

		if(db.contains(trailName)){
			int res = JOptionPane.showConfirmDialog(this, "Essa trilha já existe no banco. Deseja substituir?", "Substituição", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if(res != JOptionPane.YES_OPTION)
				return;
		}
		
		db.insert(trailName, session.getCurrentTrail());

		JOptionPane.showConfirmDialog(this, "A trilha foi inserida com sucesso.", "Inserção Finalizada", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
		
		if(session.getCurrentSourceFile() != null){
			//Adiciona na Model da JTree e faz um reload para dar um refresh
			rootNode.add(new DefaultMutableTreeNode(trailName));
			DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
			model.reload();
			//Daqui pra frente ele seleciona a última trilha adicionanda no JTree
			DefaultMutableTreeNode lastNode = (DefaultMutableTreeNode) model.getChild(rootNode, model.getChildCount(rootNode) - 1);
			TreePath tpath = new TreePath(lastNode.getPath());
			tree.setSelectionPath(tpath);
			tree.scrollPathToVisible(tpath);
		}
		
		showTree();
	}
	
	private void deleteCurrentTrailInDatabase(){
		int res = JOptionPane.showConfirmDialog(this, "Você está prestes a remover permanentemente essa trilha do banco. Continuar?", "Remoção", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		
		if(res != JOptionPane.YES_OPTION)
			return;
		
		String trailName = getNameFromFile(session.getCurrentSourceFile());

		db.delete(trailName);
		
		JOptionPane.showConfirmDialog(this, "A trilha foi removida do banco mas você ainda pode mexer nela.", "Remoção Finalizada", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
	}
	
	private void openFromDatabase(String name){
		session.setCurrentTrail(db.load(name));
		
	    initializeChart();
	    
	    eraseSelectionsButton.setEnabled(true);
	    locateButton.setEnabled(true);
	    
	    //Desenha toda a track apenas
	    drawStretch(session.getCurrentTrail());
	   
		mapViewer.setZoom(4);
		fitMap();
		
		session.setCurrentKML(null);
		
		showTree();
	}
	
	private void showInclinationSpeedGraph(int typeOfGraph){
		try {
			double [][] avgSpeedTable = StatisticsUtil.getSimpleAverageSpeedMatrix(STEPS);
			List<UnivariateFunction> listFunc;
			
			if(typeOfGraph == POLYNOMIAL_FITTING)
				listFunc = StatisticsUtil.getListOfFunctionsWithPolynomialFitting(avgSpeedTable, STEPS);
			else
				listFunc = StatisticsUtil.getListOfFunctionsWithLoess(avgSpeedTable, STEPS);
			
			SpeedPerInclinationGraph graph = new SpeedPerInclinationGraph(listFunc, STEPS);
			graph.setLocationRelativeTo(null);
			graph.pack();
			graph.setVisible(true);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	private void openStretchManager(){
		StretchManager manager = new StretchManager();
		manager.setLocationRelativeTo(this);
		manager.setResizable(false);
		manager.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(OPEN_EVT)){
			openFile();
		}else if(e.getActionCommand().equals(EDIT_STRETCH_EVT)){
			openMarkStretchDialog();
		}else if(e.getActionCommand().equals(ERASE_STRETCHS_EVT)){
			eraseAllSelections();
		}else if(e.getActionCommand().equals(STYLES_MANAGER_EVT)){
			openStretchManager();
		}else if(e.getActionCommand().equals(SAVE_IN_DATABASE_EVT)){
			insertCurrentTrailInDatabase();
		}else if(e.getActionCommand().equals(OPEN_DATABASE_EVT)){
			if(!databaseIsOpened)
				showTree();
			else
				hideTree();

			databaseIsOpened = !databaseIsOpened;
		}else if(e.getActionCommand().equals(DELETE_IN_DATABASE_EVT)){
			deleteCurrentTrailInDatabase();
		}else if(e.getActionCommand().equals(STATISTICS_EVT)){
			showInclinationSpeedGraph(LOESS_INTERPOLATOR);
		}else if(e.getActionCommand().equals(LOCATION_EVT)){
			fitMap();
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
	        		session.setCurrentKML(getKml(session.getCurrentSourceFile()));
				} catch (Exception e) {
					e.printStackTrace();
				}
	    		
	        	session.setCurrentTrail(KmlUtils.getAllPlacemarks(session.getCurrentKML(), new KmlParseProgressListener(){
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
	    		}));
	    		
	    	    initializeChart();
	    	    
	    	    eraseSelectionsButton.setEnabled(true);
	    	    locateButton.setEnabled(true);
	    	    saveButton.setEnabled(true);
	    	    
	    	    //Desenha toda a track apenas
	    	    drawStretch(session.getCurrentTrail());
	    	   
	    		mapViewer.setZoom(4);
	    		fitMap();
	    		
	    		hideTree();
	      }
	   }
}
