package view.widgets;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class CustomTreeCellRenderer extends DefaultTreeCellRenderer{
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
	    boolean leaf, int row, boolean hasFocus) {
			super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		
	        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
	        
	        if (tree.getModel().getRoot().equals(node)) 
	        	setIcon(new ImageIcon("images/database_root_tree.png"));
	        else
	        	setIcon(new ImageIcon("images/track.png"));
	        
	        return this;
	}
}
