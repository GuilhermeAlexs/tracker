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

import view.listeners.TreePopupListener;
import view.popup.TreePopup;
import view.widgets.events.TreePanelListener;

public class TreePanel extends JTree implements MouseListener{
	private static final long serialVersionUID = -5777942093127701836L;
	
	private DefaultMutableTreeNode rootNode;
	private DefaultMutableTreeNode dbNode;
	private DefaultMutableTreeNode tempNode;
	
	private DefaultMutableTreeNode currentIndexNodeSelected = null;
	
	private TreePanelListener treePanelListener;

	public TreePanel(String[] names){
		super(new DefaultMutableTreeNode("Root"));
		
		this.rootNode = (DefaultMutableTreeNode) this.getModel().getRoot();
		
		this.dbNode = new DefaultMutableTreeNode("Banco de Trilhas");
		this.tempNode = new DefaultMutableTreeNode("Temporário");
		
		rootNode.add(dbNode);
		rootNode.add(tempNode);
		
	    if(names != null){
		    Arrays.sort(names);
		    
		    for(int i = 0; i < names.length; i++)
		    	insertDBItemNode(names[i]);
	    }

		setCellRenderer(new CustomTreeCellRenderer());
		addMouseListener(this);
		
		this.setRootVisible(false);
		refresh();
	}
	
	public void expandAll(){
		for (int i = 0; i < getRowCount(); i++) {
		    expandRow(i);
		}
	}
	
	public void refresh(){
		DefaultTreeModel model = (DefaultTreeModel)getModel();
		model.reload();
		expandAll();
	}
	
	public DefaultMutableTreeNode insertDBItemNode(Object data){
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(data);
		node.setAllowsChildren(false);
		dbNode.add(node);
		refresh();
		
		return node;
	}
	
	public DefaultMutableTreeNode insertTempItemNode(Object data){
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(data);
		node.setAllowsChildren(false);
		tempNode.add(node);
		refresh();
		
		return node;
	}
	
	public void selectLastAddedNode(){
		DefaultTreeModel model = (DefaultTreeModel)getModel();
		DefaultMutableTreeNode lastNode = (DefaultMutableTreeNode) model.getChild(dbNode, model.getChildCount(rootNode) - 1);
		TreePath tpath = new TreePath(lastNode.getPath());
		setSelectionPath(tpath);
		scrollPathToVisible(tpath);
	}
	
	public void selectNode(DefaultMutableTreeNode node){
		TreePath tpath = new TreePath(node.getPath());
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
			if(e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)){
		        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) getLastSelectedPathComponent();

		        if(selectedNode != null && selectedNode.isLeaf() && !selectedNode.getAllowsChildren()){
		        	if(selectedNode.isNodeAncestor(dbNode)){
				        currentIndexNodeSelected = selectedNode;
				        treePanelListener.onTreeNodeSelected(selectedNode.getUserObject(), true);
		        	}else{
				        currentIndexNodeSelected = selectedNode;
				        treePanelListener.onTreeNodeSelected(selectedNode.getUserObject(), false);
		        	}
		        }
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
			        
			        final boolean selectedWasRemoved;
			        
			        if(currentIndexNodeSelected == selectedNode)
			        	selectedWasRemoved = true;
			        else
			        	selectedWasRemoved = false;

			        if(selectedNode != null && selectedNode.isLeaf() && !selectedNode.getAllowsChildren()){
			        	boolean isInDB = false;
			        	
			        	if(selectedNode.isNodeAncestor(dbNode))
			        		isInDB = true;
			        	
		            	TreePopup pop = new TreePopup(this.getParent(), this, isInDB, new TreePopupListener(){
							@Override
							public void onTrailDeleteRequested() {
					    	   treePanelListener.onTreeNodeDeleted(selectedNode.getUserObject(), selectedWasRemoved, true);
					    	   selectedNode.removeFromParent();
								
							   DefaultTreeModel model = (DefaultTreeModel)getModel();
							   model.reload();
							   refresh();
							}

							@Override
							public void onTrailAddRequested() {
								treePanelListener.onTreeNodeAddedToDB(selectedNode.getUserObject());
							}
		            	});	            	
		            	pop.show(this, e.getX(), e.getY());
			        }
	            }
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}
}
