package view;

import java.awt.EventQueue;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;
import javax.swing.SwingUtilities;
import javax.swing.AbstractAction;
import javax.swing.Box;
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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;

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
import view.popup.TreePopup;
import view.widgets.CustomTreeCellRenderer;
import view.widgets.ElevationPanel;
import view.widgets.MapPanel;
import view.widgets.events.ElevationGraphListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.JTree;

public class Main extends JFrame implements MouseListener, ActionListener, ElevationGraphListener {
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
	
	private JPanel contentPane;

	private boolean databaseIsOpened = true;
	
	private JSplitPane splitPanelVertical;
	private JSplitPane splitPanelHorizontal;
	
	private MapPanel mapPanel;
	private ElevationPanel elevationPanel;
	
	private JToolBar mainToolbar;
	private JProgressBar progressBar;
	
	private JButton locateButton;
	private JButton selectTypeButton;
	private JButton eraseSelectionsButton;
	private JButton stylesManagerButton;
	private JButton statisticsButton;
	private JButton saveButton;
		
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
		mapPanel = new MapPanel();
		
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
		splitPanelHorizontal.add(mapPanel);
		
		contentPane.setLayout(new BorderLayout(0, 0));
		contentPane.add(mainToolbar, BorderLayout.NORTH);
		contentPane.add(splitPanelHorizontal, BorderLayout.CENTER);
		contentPane.add(progressBar, BorderLayout.SOUTH);
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

	private void makeSplitPanelOnTheFly(){
		contentPane.removeAll();
		
		splitPanelVertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPanelVertical.setDividerSize(3);
		splitPanelVertical.setOneTouchExpandable(false);
		splitPanelVertical.add(mapPanel);
		
		elevationPanel = new ElevationPanel(session.getCurrentTrail());
		elevationPanel.setElevationGraphListener(this);
		
		trailStatistics = StatisticsUtil.calculateStats(session.getCurrentTrail());
		elevationPanel.showNumberStatisticsPanel(trailStatistics);
				
		splitPanelVertical.add(elevationPanel);
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

	private void drawStretch(List<TPLocation2> trail){
		mapPanel.clear();
		elevationPanel.clearProfileSelections();
	
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
				mapPanel.drawPath(stretch, type.getColor());
				
				if(!lastLoc.getTypeId().equals(TypeConstants.FIXED_TYPE_TRAIL)){
					if(lastLoc.getTypeId().equals(TypeConstants.FIXED_TYPE_INVALID))
						elevationPanel.drawProfileSelection(stretch, type.getColor(), true);
					else
						elevationPanel.drawProfileSelection(stretch, type.getColor(), false);
				}

				stretch.clear();
				stretch.add(lastLoc);
			}

			stretch.add(loc);

			lastLoc = loc;
		}

		StretchType type = session.getStretchTypes().get(lastLoc.getTypeId());
		mapPanel.drawPath(stretch, type.getColor());

		if(!lastLoc.getTypeId().equals(TypeConstants.FIXED_TYPE_TRAIL)){
			if(lastLoc.getTypeId().equals(TypeConstants.FIXED_TYPE_INVALID))
				elevationPanel.drawProfileSelection(stretch, type.getColor(), true);
			else
				elevationPanel.drawProfileSelection(stretch, type.getColor(), false);
		}

		if(selected.size() > 0){
			mapPanel.drawPath(selected, Color.RED);
			elevationPanel.drawProfileSelection(selected, Color.RED, false);
			mapPanel.drawMarkersAtTheEndsOf(selected);
		}

		mapPanel.commitDrawings();
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
			elevationPanel.showNumberStatisticsPanel(StatisticsUtil.calculateStats(locs));
		
		drawStretch(session.getCurrentTrail());
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
							mapPanel.resetMap();
						}
	            	});	            	
	            	pop.show(tree, e.getX(), e.getY());
	            }
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	private void backToNormalStateOfSelection(){
		setSelection(elevationPanel.getLastSelectionEnd(),
			     elevationPanel.getLastSelectionEnd());
		selectTypeButton.setEnabled(false);
		elevationPanel.showNumberStatisticsPanel(trailStatistics);
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
		JDialog dialog = new MarkStretchView(elevationPanel.getLastSelectionStart(), elevationPanel.getLastSelectionEnd(), new StretchTypeChangeListener(){
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
		elevationPanel.showNumberStatisticsPanel(trailStatistics);
		drawStretch(session.getCurrentTrail());
		
		selectTypeButton.setEnabled(false);
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
		
		makeSplitPanelOnTheFly();
	    
	    eraseSelectionsButton.setEnabled(true);
	    locateButton.setEnabled(true);
	    
	    //Desenha toda a track apenas
	    drawStretch(session.getCurrentTrail());
	    
	    mapPanel.fitMap(session.getCurrentTrail());
		
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
			mapPanel.fitMap(session.getCurrentTrail());
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
	    		
	    		elevationPanel.setCurrentTrail(session.getCurrentTrail());
	    		elevationPanel.drawElevationGraph();
	    	    
	    	    eraseSelectionsButton.setEnabled(true);
	    	    locateButton.setEnabled(true);
	    	    saveButton.setEnabled(true);
	    	    
	    	    //Desenha toda a track apenas
	    	    drawStretch(session.getCurrentTrail());
	    	   
	    	    mapPanel.fitMap(session.getCurrentTrail());
	    		
	    		hideTree();
	      }
	   }

	@Override
	public void onGraphSelectionFinished(int selStart, int selEnd) {
		setSelection(selStart, selEnd);
		selectTypeButton.setEnabled(true);
	}

	@Override
	public void onGraphSelectionMoving(int selStart, int selEnd) {
		setSelection(selStart, selEnd);
	}

	@Override
	public void onGraphClearRequest() {
		backToNormalStateOfSelection();
	}

	public void goToFirstState(){
		locateButton.setEnabled(false);
		selectTypeButton.setEnabled(false);
		eraseSelectionsButton.setEnabled(false);
		stylesManagerButton.setEnabled(true);
		statisticsButton.setEnabled(true);
		saveButton.setEnabled(false);
	}
	
	public void goToTrailLoadedState(){
		locateButton.setEnabled(true);
		selectTypeButton.setEnabled(false);
		eraseSelectionsButton.setEnabled(false);
		stylesManagerButton.setEnabled(true);
		saveButton.setEnabled(true);
		
		if(session.getCurrentSourceFile() == null)
			showTree();
		else
			hideTree();
	}
}
