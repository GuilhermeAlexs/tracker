package view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;

import database.DatabaseManager;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import model.Configurations;
import model.Statistics;
import model.StretchType;
import model.TPLocation;
import model.TableOfValues;
import model.ToblerFunction;
import model.TypeConstants;
import utils.DateUtils;
import utils.GeoUtils;
import utils.KmlUtils;
import utils.NameSpaceBeautyfier;
import utils.NetworkUtils;
import utils.Predictor;
import utils.StatisticsUtil;
import utils.listeners.KmlParseProgressListener;
import view.factory.ToolbarButtonFactory;
import view.filters.ExtensionFileFilter;
import view.graphs.SpeedPerInclinationGraph;
import view.listeners.StretchTypeChangeListener;
import view.widgets.ElevationPanel;
import view.widgets.MapPanel;
import view.widgets.TreePanel;
import view.widgets.events.ElevationGraphListener;
import view.widgets.events.TreePanelListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JToolBar;
import javax.swing.KeyStroke;

public class Main extends JFrame implements ActionListener, ElevationGraphListener, TreePanelListener {
	private static final long serialVersionUID = -8622730863892625194L;

	private static final int WINDOW_WIDTH = 1080;
	private static final int WINDOW_HEIGHT = 690;
	private static final int TREE_MINIMUM_WIDTH = 230;
	private static final int SPLIT_MINIMUM_WIDTH = WINDOW_WIDTH - TREE_MINIMUM_WIDTH;

	private static final String OPEN_EVT = "open";
	private static final String CONFIG_EVT = "config";
	private static final String EDIT_STRETCH_EVT = "edit_stretch";
	private static final String ERASE_STRETCHS_EVT = "erase_stretchs";
	private static final String STYLES_MANAGER_EVT = "styles_manager";
	private static final String SAVE_IN_DATABASE_EVT = "save_in_database";
	private static final String OPEN_DATABASE_EVT = "open_database";
	private static final String STATISTICS_SPEEDS_EVT = "calculate_speeds_statistics";
	private static final String STATISTICS_SINGLE_EVT = "calculate_single_speed_statistics";
	private static final String LOCATION_EVT = "location";

	public DatabaseManager db = DatabaseManager.getInstance();

	private JPanel contentPane;

	private boolean databaseIsOpened = true;

	private JSplitPane splitPanelVertical;
	private JSplitPane splitPanelHorizontal;

	private MapPanel mapPanel;
	private TreePanel tree;
	private ElevationPanel elevationPanel;

	private JToolBar mainToolbar;
	private JProgressBar progressBar;

	private JButton locateButton;
	private JButton selectTypeButton;
	private JButton eraseSelectionsButton;
	private JButton stylesManagerButton;
	private JButton statisticsSpeedButton;
	private JButton statisticsSingleSpeedButton;
	private JButton saveButton;
	private JToggleButton dbButton;

	private Statistics trailStatistics;
	private Session session;

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
		session = Session.getInstance();

		setTitle("Tracker");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(new ImageIcon("images/logo.png").getImage());
		setBounds(100, 500, WINDOW_WIDTH, WINDOW_HEIGHT);

		Configurations conf = Configurations.getInstance();

		NetworkUtils.useProxy(conf.getProxyAddress(), conf.getProxyPort(), 
				conf.getProxyUser(), conf.getProxyPassword());

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

		tree = new TreePanel(db.getAllTrailsNames());
		tree.setMinimumSize(new Dimension(TREE_MINIMUM_WIDTH, WINDOW_HEIGHT));
		tree.setTreePanelListener(this);

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

	private void makeSplitPanelOnTheFly(){
		contentPane.removeAll();

		splitPanelVertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPanelVertical.setDividerSize(3);
		splitPanelVertical.setOneTouchExpandable(false);
		splitPanelVertical.add(mapPanel);

		trailStatistics = StatisticsUtil.calculateStats(session.getCurrentTrail());

		elevationPanel = new ElevationPanel(session.getCurrentTrail(), trailStatistics);
		elevationPanel.setElevationGraphListener(this);

		elevationPanel.showElevationStatisticsPanel(trailStatistics);

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

	    dbButton = ToolbarButtonFactory.makeNavigationToggleButton("database_conf", OPEN_DATABASE_EVT, "Abrir Banco de Trilhas", this);
	    dbButton.setSelected(true);
	    toolBar.add(dbButton);

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

	    eraseSelectionsButton = ToolbarButtonFactory.makeNavigationButton("erase", ERASE_STRETCHS_EVT, "Apagar todas as marcações da trilha", this);
	    eraseSelectionsButton.setEnabled(false);
	    toolBar.add(eraseSelectionsButton);

	    stylesManagerButton = ToolbarButtonFactory.makeNavigationButton("stylesmanager", STYLES_MANAGER_EVT, "Gerenciar Tipos Trecho", this);
	    toolBar.add(stylesManagerButton);

	    toolBar.add(Box.createRigidArea(new Dimension(25,0)));	

	    statisticsSpeedButton = ToolbarButtonFactory.makeNavigationButton("stats", STATISTICS_SPEEDS_EVT, "Estatísticas de velocidade", this);
	    toolBar.add(statisticsSpeedButton);

	    statisticsSingleSpeedButton = ToolbarButtonFactory.makeNavigationButton("statssingle", STATISTICS_SINGLE_EVT, "Estatísticas individuais de velocidade", this);
	    toolBar.add(statisticsSingleSpeedButton);
	    
	    toolBar.add(Box.createRigidArea(new Dimension(25,0)));

	    button = ToolbarButtonFactory.makeNavigationButton("settings", CONFIG_EVT, "Configurar", this);
	    toolBar.add(button);
	}

	private void drawStretch(List<TPLocation> trail){
		mapPanel.clear();
		elevationPanel.clearProfileSelections();

		List<TPLocation> stretch = new ArrayList<TPLocation>();
		List<TPLocation> selected = new ArrayList<TPLocation>();

		TPLocation lastLoc = trail.get(0);

		for(TPLocation loc: trail){
			if(loc.isSelected()){
				loc.setSelected(false);
				selected.add(loc);
			}

			if(!lastLoc.getTypeId().equals(loc.getTypeId())){
				StretchType type = session.getStretchTypes().get(lastLoc.getTypeId());

				if(type == null){
					lastLoc.setTypeId(TypeConstants.FIXED_TYPE_TRAIL);
					type = session.getStretchTypes().get(lastLoc.getTypeId());
				}

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

		if(type == null){
			lastLoc.setTypeId(TypeConstants.FIXED_TYPE_TRAIL);
			type = session.getStretchTypes().get(lastLoc.getTypeId());
		}

		mapPanel.drawPath(stretch, type.getColor());

		if(!lastLoc.getTypeId().equals(TypeConstants.FIXED_TYPE_TRAIL)){
			if(lastLoc.getTypeId().equals(TypeConstants.FIXED_TYPE_INVALID))
				elevationPanel.drawProfileSelection(stretch, type.getColor(), true);
			else
				elevationPanel.drawProfileSelection(stretch, type.getColor(), false);
		}

		if(selected.size() > 0){
			Color selColor = Configurations.getInstance().getSelectionColor();
			mapPanel.drawPath(selected, selColor);
			elevationPanel.drawProfileSelection(selected, selColor, false);
			mapPanel.drawMarkersAtTheEndsOf(selected);
		}

		mapPanel.commitDrawings();
	}

	private void setSelection(int start, int end){
		if(end == -1 || start == -1 || end < start)
			return;

		List<TPLocation> locs = new ArrayList<TPLocation>();
		TPLocation currLoc;

		for(int i = start; i <= end; i++){
			currLoc = session.getCurrentTrail().get(i);
			currLoc.setSelected(true);
			locs.add(currLoc);
		}

		if(start != end)
			elevationPanel.showElevationStatisticsPanel(StatisticsUtil.calculateStats(locs));

		drawStretch(session.getCurrentTrail());
	}

	private void backToNormalStateOfSelection(){
		setSelection(elevationPanel.getLastSelectionEnd(),
			     elevationPanel.getLastSelectionEnd());
		selectTypeButton.setEnabled(false);
		elevationPanel.showElevationStatisticsPanel(trailStatistics);
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

	private void openConfigurations(){
		JFrame frame = new ConfigurationView();

		frame.setLocationRelativeTo(this);
		frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
		frame.setResizable(false);
	}

	private void eraseAllSelections(){
		int res = JOptionPane.showConfirmDialog(this, "Isso removerá todas as marcações que você fez nessa trilha. Continuar?", "Remover Marcações", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

		if(res != JOptionPane.OK_OPTION)
			return;

		for(TPLocation loc: session.getCurrentTrail())
			loc.setTypeId(TypeConstants.FIXED_TYPE_TRAIL);

		trailStatistics = StatisticsUtil.calculateStats(session.getCurrentTrail());
		elevationPanel.showElevationStatisticsPanel(trailStatistics);
		drawStretch(session.getCurrentTrail());

		selectTypeButton.setEnabled(false);
	}

	private void insertTrailInDatabase(List<TPLocation> trail){		
		String trailName;

        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

        if(selectedNode == null)
        	return;

    	trailName = selectedNode.getUserObject().toString();

		saveTrailAlreadyInDatabase(trail, trailName);
	}

	private void saveTrailAlreadyInDatabase(List<TPLocation> trail, String trailName){
		if(trail == null || trail.size() == 0 || 
				trail.get(0).getWhen() == null){
			JOptionPane.showMessageDialog(this, "O track escolhido não possui informações de tempo e não pode ser adicionado ao Banco de Trilhas.", "Cadê as informações de tempo?", JOptionPane.ERROR_MESSAGE);
			return;
		}

        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

        if(selectedNode == null)
        	return;

		db.insert(trailName, trail);
	}

	private void openFromDatabase(String name){
		session.setCurrentTrail(db.load(name));

		makeSplitPanelOnTheFly();

	    eraseSelectionsButton.setEnabled(true);
	    locateButton.setEnabled(true);

	    //Desenha toda a track apenas
	    drawStretch(session.getCurrentTrail());

	    mapPanel.fitMap(session.getCurrentTrail());
	}

	private void openFromTemp(Placemark placemark){
		try{
			//Setando antes numa outra variável... para não setar a session se der problema
			List<TPLocation> trail = KmlUtils.parsePlacemark(placemark, new KmlParseProgressListener(){
				@Override
				public void onPreParse(int progressTotal) {
				}

				@Override
				public void onParseProgress(int progress) {
				}

				@Override
				public void onParseFolder(Folder folder) {
				}

				@Override
				public void onParsePlacemark(Placemark placemark) {
				}

				@SuppressWarnings({ "unchecked", "rawtypes" })
				@Override
				public void onParseFinish(boolean altitudeWasDownloaded) {
					if(altitudeWasDownloaded){
						int resp = JOptionPane.showConfirmDialog(Main.this, "O seu KML não tinha informações de altitude e tivemos que baixá-las."
								+ "\nVocê quer que essas informações sejam gravadas no arquivo original? "
								+ "\nIsso evita que elas tenham sempre que ser baixadas.", "Gravar Altitude", JOptionPane.YES_NO_OPTION);

						if(resp == JOptionPane.NO_OPTION)
							return;

					    try {
							//Grava o kml com a altitude no mesmo arquivo original!
							String name = session.getCurrentKML().getClass().getSimpleName();
					        if ("Kml".equals(name))
					            name = name.toLowerCase();

					        JAXBContext jaxbContext = JAXBContext.newInstance(Kml.class);
					        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

					        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
							jaxbMarshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new NameSpaceBeautyfier());

					        JAXBElement<Kml> jaxbKml = new JAXBElement(new QName("http://www.opengis.net/kml/2.2", name), (Class<Kml>) session.getCurrentKML().getClass(), session.getCurrentKML());
					        jaxbMarshaller.marshal(jaxbKml, session.getCurrentSourceFile());
				        } catch (Exception e) {
				        	JOptionPane.showMessageDialog(Main.this, "Tivemos algum problema e não foi possível gravar as informações de altitude", "Erro", JOptionPane.ERROR_MESSAGE);
							e.printStackTrace();
						}
					}
				}
			});

			if(Configurations.getInstance().isSmooth())
				trail = GeoUtils.smoothAltitude(trail);

			session.setCurrentTrail(trail);
		}catch(Exception e){
			JOptionPane.showMessageDialog(this, "Arquivo KML inválido para o Tracker. "
					+ "Certifique-se de que ele contenha algum caminho traçado ou que você possua acesso à internet\n"
					+ "para que possamos baixar as informações de altitude.", "Erro ao Abrir KML", JOptionPane.ERROR_MESSAGE);
			return;
		}

		makeSplitPanelOnTheFly();

	    eraseSelectionsButton.setEnabled(true);
	    locateButton.setEnabled(true);

	    //Desenha toda a track apenas
	    drawStretch(session.getCurrentTrail());

	    mapPanel.fitMap(session.getCurrentTrail());
	}

	private void openStretchManager(){
		StretchManager manager = new StretchManager();
		manager.setLocationRelativeTo(this);
		manager.setResizable(false);
		manager.setVisible(true);
	}

	private void doTimePrediction(){
		TableOfValues avgValueTable;
		List<TPLocation> path = Session.getInstance().getCurrentTrail();

		try {
			avgValueTable = StatisticsUtil.calculateAverageSpeedMatrix(session.getStretchTypesIdMap(), 
					session.getStretchTypes().size(), Configurations.getInstance());

			if(avgValueTable == null){
	        	SwingUtilities.invokeLater(new Runnable() {
	                @Override
	                public void run() {
	    	      	    progressBar.setVisible(false);
	    				JOptionPane.showMessageDialog(Main.this, "Banco de Tracks insuficiente! Faça mais trilhas!", "Estatísticas", JOptionPane.ERROR_MESSAGE);
	                }
	        	});

				return;
			}

			List<UnivariateFunction> listFunc;
			listFunc = StatisticsUtil.getListOfPredictionFunctions(avgValueTable.getValues(), Configurations.getInstance());

			double predictedTotalTime = Predictor.predict(path, avgValueTable, Session.getInstance().getStretchTypesIdMap(), 
					listFunc, false, Configurations.getInstance());

			if(path.get(0).getWhen() != null){
				double realTotalTime = (DateUtils.toCalendar(path.get(path.size() - 1).getWhen()).getTimeInMillis() - 
						DateUtils.toCalendar(path.get(0).getWhen()).getTimeInMillis())/1000f;

				System.out.println("Real Duration: " + DateUtils.secondsOnlyToFormattedString(realTotalTime));
			}

			System.out.println("Predicted Duration: " + DateUtils.secondsOnlyToFormattedString(predictedTotalTime));

			Statistics stats = new Statistics();
			stats.setTimeInDB(predictedTotalTime);

			listFunc.clear();

			for(int i = 0; i < Session.getInstance().getStretchTypes().size(); i++)
				listFunc.add(new ToblerFunction(4.3f));

			predictedTotalTime = Predictor.predict(path, avgValueTable, Session.getInstance().getStretchTypesIdMap(), 
					listFunc, true, Configurations.getInstance());
			stats.setTimeTobler(predictedTotalTime);

			elevationPanel.showTimeStatisticsPanel(stats);
		} catch (ParseException | IOException e1) {
			e1.printStackTrace();
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
		}else if(e.getActionCommand().equals(STYLES_MANAGER_EVT)){
			openStretchManager();
		}else if(e.getActionCommand().equals(SAVE_IN_DATABASE_EVT)){
			insertTrailInDatabase(session.getCurrentTrail());
		}else if(e.getActionCommand().equals(OPEN_DATABASE_EVT)){
			if(!databaseIsOpened){
				showTree();
			}else{
				hideTree();
			}

			databaseIsOpened = !databaseIsOpened;
		}else if(e.getActionCommand().equals(STATISTICS_SPEEDS_EVT)){
			new ProcessStatisticsTask(true).start();
		}else if(e.getActionCommand().equals(STATISTICS_SINGLE_EVT)){
			new ProcessStatisticsTask(false).start();
		}else if(e.getActionCommand().equals(LOCATION_EVT)){
			mapPanel.fitMap(session.getCurrentTrail());
		}else if(e.getActionCommand().equals(CONFIG_EVT)){
			openConfigurations();
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

	@Override
	public void onGraphPredictTimeRequested() {
		doTimePrediction();
	}

	@Override
	public void onTreeNodeSelected(Object data, boolean isInDB) {
		if(isInDB){
			openFromDatabase((String)data);
		}else{
			openFromTemp((Placemark)data);
		}

        saveButton.setEnabled(true);
	}

	@Override
	public void onTreeNodePredictRequested(Object data, boolean selectedWasAimed, boolean isInDB) {
		if(isInDB){
			openFromDatabase((String)data);
		}else{
			openFromTemp((Placemark)data);
		}

		doTimePrediction();
	}

	@Override
	public void onTreeNodeAddedToDB(Object data, boolean selectedWasAimed) {
		try {
			List<TPLocation> trail;

			if(!selectedWasAimed)
				trail = KmlUtils.parsePlacemark((Placemark) data, null);
			else
				trail = session.getCurrentTrail();

			insertTrailInDatabase(trail);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Arquivo KML inválido para o Tracker. Certifique-se de que ele contenha algum caminho traçado.", "Erro ao Salvar KML", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public void onTreeNodeSaveDB(Object data, boolean selectedWasAimed) {
		if(selectedWasAimed)
			saveTrailAlreadyInDatabase(session.getCurrentTrail(), (String)data);
	}

	@Override
	public void onTreeNodeDeleted(Object data, boolean selectedWasRemoved, boolean isInDB) {
		if(isInDB){
			String trailName = data.toString();
			db.delete(trailName);
			session.setCurrentTrail(null);
		}

		if(!selectedWasRemoved)
			return;

		mapPanel.resetMap();

		if(splitPanelVertical != null){
			splitPanelVertical.getBottomComponent().setPreferredSize(new Dimension(0,0));
			splitPanelVertical.setResizeWeight(1f);
			splitPanelVertical.setDividerLocation(1f);
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
        		tree.clearTempNode();
        		session.setCurrentKML(getKml(session.getCurrentSourceFile()));
			} catch (Exception e) {
				e.printStackTrace();
			}

        	KmlUtils.parseKml(session.getCurrentKML(), new KmlParseProgressListener(){
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
				public void onParseFinish(boolean altitudeWasDownloaded) {
					 SwingUtilities.invokeLater(new Runnable() {
			                @Override
			                public void run() {
			                	progressBar.setVisible(false);
			                }
			         });
				}

				@Override
				public void onParseFolder(Folder folder) {
				}

				@Override
				public void onParsePlacemark(Placemark placemark) {
					tree.insertTempItemNode(placemark);
				}
    		});
      }
    }

    private class ProcessStatisticsTask extends Thread {
    	private boolean performAverage;
    	
        public ProcessStatisticsTask(boolean performAverage){
        	this.performAverage = performAverage;
        }

        public void run(){
        	SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                	progressBar.setIndeterminate(false);
					progressBar.setVisible(true);
                }
        	});

      	    progressBar.setVisible(true);

      		try {
    			TableOfValues speedTable = null;
    			
    			if(performAverage)
    				speedTable = StatisticsUtil.calculateAverageSpeedMatrix(session.getStretchTypesIdMap(), 
    						session.getStretchTypes().size(), Configurations.getInstance());
    			else
    				speedTable = StatisticsUtil.calculateSingleSpeedMatrix(session.getCurrentTrail(),
    						session.getStretchTypesIdMap(), session.getStretchTypes().size(), 
    						Configurations.getInstance());
    			
    			if(speedTable == null){
    	        	SwingUtilities.invokeLater(new Runnable() {
    	                @Override
    	                public void run() {
    	    	      	    progressBar.setVisible(false);
    	    				JOptionPane.showMessageDialog(Main.this, "Banco de Tracks insuficiente! Faça mais trilhas!", "Estatísticas", JOptionPane.ERROR_MESSAGE);
    	                }
    	        	});

    				return;
    			}

    			List<UnivariateFunction> listFunc = StatisticsUtil.getListOfPredictionFunctions(speedTable.getValues(), Configurations.getInstance());

            	SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                    	SpeedPerInclinationGraph graph;
                		graph = new SpeedPerInclinationGraph(listFunc, Configurations.getInstance().getSteps(), "Gráfico de Velocidades", "Inclinação", "Velocidade");
                   
                    	graph.setLocationRelativeTo(null);
            			graph.pack();
            			graph.setVisible(true);
                     }
            	});
    		} catch (ParseException e) {
    			e.printStackTrace();
    		}

      		progressBar.setVisible(false);

        	SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
    	      	    progressBar.setVisible(false);
                 }
        	});
        }
     }
}
