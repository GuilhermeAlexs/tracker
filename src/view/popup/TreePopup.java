package view.popup;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import database.DatabaseManager;
import view.listeners.DatabaseTrailDeletedListener;

public class TreePopup extends JPopupMenu implements ActionListener{
	private static final long serialVersionUID = -6041321118922619389L;
	
	private final String RENAME = "Renomear...";
	private final String REMOVE = "Excluir";
	
	private Component parent;
	private JTree tree;
	private DatabaseTrailDeletedListener listener;
	
	public TreePopup(Component parent, JTree tree, DatabaseTrailDeletedListener listener){
		this.parent = parent;
		this.tree = tree;
		this.listener = listener;
		
		JMenuItem item = new JMenuItem("Renomear...");
		item.addActionListener(this);
		add(item);
		
		item = new JMenuItem("Excluir");
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
					if(db.contains(newName)){
						JOptionPane.showMessageDialog(parent, "Esse nome j√° existe. Escolha outro!", "Erro ao renomear", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
			        db.renameTrail(selectedNode.getUserObject().toString(), newName);
					
			        selectedNode.setUserObject(newName);
			        
			        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
			        model.nodeChanged(selectedNode);
			        
				}
				
		        break;
			case REMOVE:
				int resp = JOptionPane.showConfirmDialog(parent, "Voc√™ est√° prestes a excluir uma trilha do Banco de Dados. Isso vai alterar todas as estat√≠sticas. Confirma?", "Exclus√£o", JOptionPane.YES_NO_OPTION);
				
				if(resp == JOptionPane.YES_OPTION){
					String trailName = selectedNode.getUserObject().toString();

					db.delete(trailName);
					
					JOptionPane.showConfirmDialog(this, "A trilha foi removida do banco mas vocÍ ainda pode mexer nela.", "RemoÁ„o Finalizada", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);

					selectedNode.removeFromParent();
					
					DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
					model.reload();
					
					listener.onDatabaseTrailDeleted();
				}
				
				break;
		}	
	}
}
