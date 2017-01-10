package view.widgets;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.event.MouseInputListener;

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

import model.TPLocation;
import view.SelectionAdapter;
import view.painters.RoutePainter;
import view.painters.SelectionPainter;
import view.painters.SwingWaypointOverlayPainter;
import view.waypoint.SwingWaypoint;

public class MapPanel extends JXMapViewer{
	private static final long serialVersionUID = -7695876473718305279L;
	
	private Icon markerA;
	private Icon markerB;
	
	private List<Painter<JXMapViewer>> painters = new ArrayList<Painter<JXMapViewer>>();
	
	public MapPanel(){
		super();
		
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
		
		// Create a TileFactoryInfo for OpenStreetMap
		TileFactoryInfo info =  new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.SATELLITE);
		DefaultTileFactory tileFactory = new DefaultTileFactory(info);
		tileFactory.setThreadPoolSize(8);
		
		// Setup local file cache
		//File cacheDir = new File(System.getProperty("user.home") + File.separator + ".jxmapviewer2");
		//LocalResponseCache.installResponseCache(info.getBaseURL(), cacheDir, false);
		
		setTileFactory(tileFactory);
	
		// Add interactions
		MouseInputListener mia = new PanMouseInputListener(this);
		addMouseListener(mia);
		addMouseMotionListener(mia);

		addMouseListener(new CenterMapListener(this));
		addMouseWheelListener(new ZoomMouseWheelListenerCursor(this));
		addKeyListener(new PanKeyListener(this));
		
		// Add a selection painter
		SelectionAdapter sa = new SelectionAdapter(this); 
		SelectionPainter sp = new SelectionPainter(sa); 
		addMouseMotionListener(sa); 
		addMouseListener(sa); 
		setOverlayPainter(sp);

		setPreferredSize(new java.awt.Dimension(300, 440));
	
		resetMap();
	}
	
	public void drawMarkersAtTheEndsOf(List<TPLocation> locs){
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
		            add(w.getImg());
		        }
			}
			
			painters.add(waypointPainter);
		}
	}
	
	public void drawPath(List<TPLocation> path, Color color){
		painters.add(new RoutePainter(path, color));
	}
	
	public void commitDrawings(){
		CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(painters);
		setOverlayPainter(painter);
	}
	
	public void clear(){
		removeAll();
		painters.clear();
		commitDrawings();
	}
	
	public void resetMap(){
		clear();
		GeoPosition initPos = new GeoPosition(-15,-40);
		setZoom(16);
		setAddressLocation(initPos);
	}
	
	public void fitMap(List<TPLocation> trail){
		Set<GeoPosition> pos = new HashSet<GeoPosition>();
		
		for(TPLocation l: trail){
			pos.add(new GeoPosition(l.getLatitude(), l.getLongitude()));
		}

		zoomToBestFit(pos, 0.7);
	}
}
