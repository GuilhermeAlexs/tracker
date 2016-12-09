package view.waypoint;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JLabel;

import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

public class SwingWaypoint extends DefaultWaypoint{
	private final JLabel labelImg;

    public SwingWaypoint(Icon ic, GeoPosition coord) {
        super(coord);
        labelImg = new JLabel();
     
        labelImg.setIcon(ic);
        labelImg.setSize(24, 24);
        labelImg.setPreferredSize(new Dimension(24, 24));
        labelImg.addMouseListener(new SwingWaypointMouseListener());
        labelImg.setVisible(true);
    }

    public JLabel getImg() {
        return labelImg;
    }

    private class SwingWaypointMouseListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
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
}
