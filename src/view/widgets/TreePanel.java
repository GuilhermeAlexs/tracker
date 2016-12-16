package view.widgets;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import view.listeners.DatabaseTrailDeletedListener;
import view.popup.TreePopup;
import view.widgets.events.TreePanelListener;

public class TreePanel extends JTree implements MouseListener{
	private static final long serialVersionUID = -5777942093127701836L;
	
	private static final String TREE_ROOT = "Banco de Tracks";
	
	private DefaultMutableTreeNode rootNode;
	private int currentIndexNodeSelected = -1;
	
	private TreePanelListener treePanelListener;

	public TreePanel(String[] names, DefaultMutableTreeNode root){
		super(root);
		
		this.rootNode = root;
		
	    if(names != null){
		    Arrays.sort(names);
		    
		    for(int i = 0; i < names.length; i++)
		    	rootNode.add(new DefaultMutableTreeNode(names[i]));
	    }

		setCellRenderer(new CustomTreeCellRenderer());
		addMouseListener(this);
		
		for (int i = 0; i < getRowCount(); i++) {
		    expandRow(i);
		}
	}
	
	public void insertNode(String node){
		rootNode.add(new DefaultMutableTreeNode(node));
		DefaultTreeModel model = (DefaultTreeModel)getModel();
		model.reload();
	}
	
	public void selectLastAddedNode(){
		DefaultTreeModel model = (DefaultTreeModel)getModel();
		DefaultMutableTreeNode lastNode = (DefaultMutableTreeNode) model.getChild(rootNode, model.getChildCount(rootNode) - 1);
		TreePath tpath = new TreePath(lastNode.getPath());
		setSelectionPath(tpath);
		scrollPathToVisible(tpath);
	}
	
	public TreePanelListener getTreePanelListener() {
		return treePanelListener;
	}

	public void setTreePanelListener(TreePanelListener treePanelListener) {
		this.treePanelListener = treePanelListener;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getSource() instanceof JTree){
			if(e.getClickCount() == 1){
		        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) getLastSelectedPathComponent();

		        if(selectedNode == null || selectedNode.isRoot())
		        	return;
		        
		        currentIndexNodeSelected = rootNode.getIndex(selectedNode);
		        treePanelListener.onTreeNodeSelected(selectedNode.getUserObject().toString());
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
			    TreePath path = getPathForLocation(e.getX(), e.getY());
	            Rectangle pathBounds = getUI().getPathBounds(this, path);
	            
	            if (pathBounds != null && pathBounds.contains(e.getX(), e.getY())){
	            	setSelectionPath(path);
	            	
			        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) getLastSelectedPathComponent();
			        
			        if(currentIndexNodeSelected != rootNode.getIndex(selectedNode))
			        	return;

			        if(selectedNode == null || selectedNode.getUserObject().equals(TREE_ROOT))
			        	return;
	            	
	            	TreePopup pop = new TreePopup(this, this, new DatabaseTrailDeletedListener(){
						@Override
						public void onDatabaseTrailDeleted() {
							treePanelListener.onTreeNodeDeleted(selectedNode.getUserObject().toString());
						}
	            	});	            	
	            	pop.show(this, e.getX(), e.getY());
	            }
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}
}
