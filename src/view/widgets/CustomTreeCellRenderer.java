package view.widgets;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import de.micromata.opengis.kml.v_2_2_0.Placemark;

@SuppressWarnings("serial")
public class CustomTreeCellRenderer extends DefaultTreeCellRenderer{
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
		    boolean leaf, int row, boolean hasFocus) {
			super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
			
        	DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

        	if(node.isLeaf() && !node.getAllowsChildren()){
        		setIcon(new ImageIcon("images/track.png"));
        	}else{
        		setIcon(new ImageIcon("images/database_root_tree.png"));
        	}

	        Object userObj = node.getUserObject();
	    	if(userObj instanceof Placemark)
	    		setText(((Placemark) userObj).getName());
	        
	        return this;
	}
}
