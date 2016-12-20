package view.popup;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import database.DatabaseManager;
import view.listeners.TreePopupListener;
import view.widgets.TreePanel;

public class TreePopup extends JPopupMenu implements ActionListener{
	private static final long serialVersionUID = -6041321118922619389L;
	
	private final String RENAME = "Renomear...";
	private final String REMOVE = "Excluir";
	private final String SAVE_IN_DB = "Salvar no Banco de Trilhas";
	
	private Component parent;
	private TreePanel tree;
	private TreePopupListener listener;
	
	public TreePopup(Component parent, TreePanel tree, boolean isInDB, TreePopupListener listener){
		this.parent = parent;
		this.tree = tree;
		this.listener = listener;
		
		JMenuItem item;
		
		if(!isInDB){
			item = new JMenuItem(SAVE_IN_DB);
			item.addActionListener(this);
			add(item);
		}
		
		item = new JMenuItem(RENAME);
		item.addActionListener(this);
		add(item);
		
		item = new JMenuItem(REMOVE);
		item.addActionListener(this);
		add(item);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		DatabaseManager db = DatabaseManager.getInstance();
		
		switch(e.getActionCommand()){
			case RENAME:
				String newName = JOptionPane.showInputDialog(parent, "Substituir por:", "Renomear", JOptionPane.QUESTION_MESSAGE);
		        
				if(newName != null){
					if(selectedNode.getParent().equals(tree.getDbNode())){
						if(db.contains(newName)){
							JOptionPane.showMessageDialog(parent, "Esse nome já existe. Escolha outro!", "Erro ao renomear", JOptionPane.ERROR_MESSAGE);
							return;
						}
						
						//TODO: tirar a l�gica de banco de dados daqui
				        db.renameTrail(selectedNode.getUserObject().toString(), newName);
					}
					
			        selectedNode.setUserObject(newName);
			        
			        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
			        model.nodeChanged(selectedNode);
				}
				
		        break;
			case REMOVE:
				int resp = JOptionPane.showConfirmDialog(parent, "Você está prestes a excluir uma trilha do Banco de Dados. Isso vai alterar todas as estatísticas. Confirma?", "Exclusão", JOptionPane.YES_NO_OPTION);
				
				if(resp == JOptionPane.YES_OPTION){			
					listener.onTrailDeleteRequested();
				}
				
				break;
			case SAVE_IN_DB:
				listener.onTrailAddRequested();
				
				break;
		}	
	}
}
