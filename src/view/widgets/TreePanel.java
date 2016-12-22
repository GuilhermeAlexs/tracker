package view.widgets;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import database.DatabaseManager;
import view.widgets.events.TreePanelListener;

public class TreePanel extends JTree implements MouseListener, KeyListener, ActionListener{
	private static final long serialVersionUID = -5777942093127701836L;
	
	private final String RENAME = "Renomear...";
	private final String REMOVE = "Excluir";
	private final String SAVE_IN_DB = "Salvar no Banco de Trilhas";

	private DefaultMutableTreeNode rootNode;
	private DefaultMutableTreeNode dbNode;
	private DefaultMutableTreeNode tempNode;

	private DefaultMutableTreeNode currentIndexNodeSelected = null;

	private TreePanelListener treePanelListener;

	public TreePanel(String[] names){
		super(new DefaultMutableTreeNode("Root"));

		this.rootNode = (DefaultMutableTreeNode) this.getModel().getRoot();

		this.dbNode = new DefaultMutableTreeNode("Banco de Trilhas");
		this.tempNode = new DefaultMutableTreeNode("Tempor�rio");

		rootNode.add(dbNode);
		rootNode.add(tempNode);

	    if(names != null){
		    Arrays.sort(names);

		    for(int i = 0; i < names.length; i++)
		    	insertDBItemNode(names[i]);
	    }

		setCellRenderer(new CustomTreeCellRenderer());
		addMouseListener(this);
		addKeyListener(this);
		
		setRootVisible(false);
		refresh();
	}
	
	private void showPopupMenu(boolean isInDB, int x, int y){
		JPopupMenu menu = new JPopupMenu();
		
		JMenuItem item;
		
		if(!isInDB){
			item = new JMenuItem(SAVE_IN_DB);
			item.addActionListener(this);
			menu.add(item);
		}
		
		item = new JMenuItem(RENAME);
		item.addActionListener(this);
		menu.add(item);
		
		item = new JMenuItem(REMOVE);
		item.setAccelerator(KeyStroke.getKeyStroke("delete"));
		item.addActionListener(this);
		menu.add(item);
		
		menu.show(this, x, y);
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
	
	public void clearTempNode(){
		tempNode.removeAllChildren();
	}
	
	public void clearDBNode(){
		dbNode.removeAllChildren();
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
	
	public DefaultMutableTreeNode getDbNode() {
		return dbNode;
	}

	public void setDbNode(DefaultMutableTreeNode dbNode) {
		this.dbNode = dbNode;
	}

	public DefaultMutableTreeNode getTempNode() {
		return tempNode;
	}

	public void setTempNode(DefaultMutableTreeNode tempNode) {
		this.tempNode = tempNode;
	}
	
	private boolean wasSelectedAimed(DefaultMutableTreeNode selectedNode){
        if(currentIndexNodeSelected == selectedNode)
        	return true;
        else
        	return false;
	}
	
	private boolean wasSelectedInDB(DefaultMutableTreeNode selectedNode){
		if(selectedNode.isNodeAncestor(dbNode))
	    	return true;
		else
			return false;
	}
	
	private void deleteNode(DefaultMutableTreeNode selectedNode){
	   if(selectedNode.equals(dbNode) || selectedNode.equals(tempNode))
		   return;
	   
	   int resp = JOptionPane.showConfirmDialog(getParent(), "Você está prestes a excluir uma trilha do Banco de Dados. Isso vai alterar todas as estatísticas. Confirma?", "Exclusão", JOptionPane.YES_NO_OPTION);
		
	   if(resp == JOptionPane.YES_OPTION){
	       treePanelListener.onTreeNodeDeleted(selectedNode.getUserObject(), wasSelectedAimed(selectedNode), wasSelectedInDB(selectedNode));
		   selectedNode.removeFromParent();
			
		   DefaultTreeModel model = (DefaultTreeModel)getModel();
		   model.reload();
		   refresh();
	   }
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

			        if(selectedNode != null && selectedNode.isLeaf() && !selectedNode.getAllowsChildren())
		            	showPopupMenu(wasSelectedInDB(selectedNode), e.getX(), e.getY());
	            }
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_DELETE){
	        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) getLastSelectedPathComponent();
			deleteNode(selectedNode);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) getLastSelectedPathComponent();
		DatabaseManager db = DatabaseManager.getInstance();
		
		switch(e.getActionCommand()){
			case RENAME:
				String newName = JOptionPane.showInputDialog(getParent(), "Substituir por:", "Renomear", JOptionPane.QUESTION_MESSAGE);

				if(newName != null){
					if(selectedNode.getParent().equals(getDbNode())){
						if(db.contains(newName)){
							JOptionPane.showMessageDialog(getParent(), "Esse nome já existe. Escolha outro!", "Erro ao renomear", JOptionPane.ERROR_MESSAGE);
							return;
						}
						
						//TODO: tirar a l�gica de banco de dados daqui
				        db.renameTrail(selectedNode.getUserObject().toString(), newName);
					}

			        selectedNode.setUserObject(newName);

			        DefaultTreeModel model = (DefaultTreeModel) getModel();
			        model.nodeChanged(selectedNode);
				}
				
		        break;
			case REMOVE:
				deleteNode(selectedNode);
				
				break;
			case SAVE_IN_DB:
				treePanelListener.onTreeNodeAddedToDB(selectedNode.getUserObject(), wasSelectedAimed(selectedNode));

				break;
		}	
	}
}
