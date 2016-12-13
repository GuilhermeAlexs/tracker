package view;

import model.TypeConstants;
import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import database.DatabaseManager;
import model.StretchType;
import view.widgets.StretchTypeRenderer;

import javax.swing.JList;
import javax.swing.JToolBar;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.SwingConstants;

public class StretchManager extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	private static final String ADD_EVT = "add";
	private static final String EDIT_EVT = "edit";
	private static final String REM_EVT = "remove";
	private static final String SAVE_EVT = "save";
	private static final String CANCEL_EVT = "cancel";
	
	private DefaultListModel<StretchType> listModel;
	
	private JPanel contentPane;
	private JList<StretchType> listStretchs;
	private Session session;
	
	public StretchManager() {
		this.session = Session.getInstance();
		
		setTitle("Gerenciar Trechos");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		toolbar.setOrientation(SwingConstants.VERTICAL);

		addButtonsToToolbar(toolbar);
		contentPane.add(toolbar, BorderLayout.WEST);
		
		listModel = new DefaultListModel<>();
		
		for (Map.Entry<String, StretchType> entry : session.getStretchTypes().entrySet()){
			listModel.addElement(entry.getValue());
		}
		
		listStretchs = new JList<StretchType>(listModel);
		listStretchs.setCellRenderer(new StretchTypeRenderer());
		listStretchs.setBounds(10, 11, 396, 197);
		JScrollPane scPane = new JScrollPane(listStretchs);
		contentPane.add(scPane, BorderLayout.CENTER);

		JPanel btnPanel = new JPanel();
		contentPane.add(btnPanel, BorderLayout.SOUTH);
		btnPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

		JButton btnSave = new JButton("Salvar");
		btnSave.setActionCommand(SAVE_EVT);
		btnSave.addActionListener(this);
		btnPanel.add(btnSave);

		JButton btnCancel = new JButton("Cancelar");
		btnCancel.setActionCommand(CANCEL_EVT);
		btnCancel.addActionListener(this);
		btnPanel.add(btnCancel);
	}

	protected void addButtonsToToolbar(JToolBar toolbar) {
	    JButton button = null;

	    button = makeNavigationButton("add", ADD_EVT, "Adicionar tipo de trecho");
	    toolbar.add(button);

	    button = makeNavigationButton("edit", EDIT_EVT, "Editar tipo de trecho");
	    toolbar.add(button);

	    button = makeNavigationButton("remove", REM_EVT, "Remover tipo de trecho");
	    toolbar.add(button);
	}

	protected JButton makeNavigationButton(String imageName,String actionCommand,String toolTipText) {
		String imgLocation = "images/" + imageName + ".png";

		JButton button = new JButton();
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
		button.addActionListener(this);
		button.setIcon(new ImageIcon(imgLocation));
		button.setFocusPainted(false);

		return button;
	}

	private void openStretch(StretchType type){
		AddEditStretchView dialog = new AddEditStretchView(type, listStretchs);
		dialog.setLocationRelativeTo(this);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
		dialog.setResizable(false);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()){
			case ADD_EVT:
				openStretch(null);
				break;
			case EDIT_EVT:
				openStretch(listStretchs.getSelectedValue());
				break;
			case REM_EVT:
				int indexToRemove = listStretchs.getSelectedIndex();
				listModel.remove(indexToRemove);
				session.getStretchTypes().remove(indexToRemove);
				break;
			case SAVE_EVT:
				DatabaseManager.getInstance().saveStretchTypes(session.getStretchTypes());
				break;
			case CANCEL_EVT:
				dispose();
				break;
		}
	}
}
