package view.painters;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import javax.swing.JLabel;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.WaypointPainter;

import view.waypoint.SwingWaypoint;

public class SwingWaypointOverlayPainter extends WaypointPainter<SwingWaypoint> {
    @Override
    protected void doPaint(Graphics2D g, JXMapViewer jxMapViewer, int width, int height) {
        for (SwingWaypoint swingWaypoint : getWaypoints()) {
            Point2D point = jxMapViewer.getTileFactory().geoToPixel(
                    swingWaypoint.getPosition(), jxMapViewer.getZoom());
            Rectangle rectangle = jxMapViewer.getViewportBounds();
            int buttonX = (int)(point.getX() - rectangle.getX());
            int buttonY = (int)(point.getY() - rectangle.getY());
            JLabel button = swingWaypoint.getImg();
            button.setLocation(buttonX - button.getWidth() / 2, (buttonY - button.getHeight() / 2)-10);
        }
    }
}
