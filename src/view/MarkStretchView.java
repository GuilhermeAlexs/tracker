package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import model.StretchType;
import model.TPLocation2;
import view.listeners.StretchTypeChangeListener;
import view.widgets.StretchTypeRenderer;

import javax.swing.DefaultListModel;
import java.awt.event.ActionListener;
import java.util.Map;
import java.awt.event.ActionEvent;
import javax.swing.JList;

public class MarkStretchView extends JDialog{
	private static final long serialVersionUID = -5467253972384413352L;
	private int selStart = 0;
	private int selEnd = 0;
	
	private StretchTypeChangeListener stretchChangeListener;
	private JList<StretchType> listTypes;
	
	public MarkStretchView(int start, int end, StretchTypeChangeListener stretchChangeListener) {
		this.stretchChangeListener = stretchChangeListener;
		this.selStart = start;
		this.selEnd = end;
		
		setTitle("Tipo de Trecho");
		setBounds(100, 100, 300, 260);
		getContentPane().setLayout(new BorderLayout());
	    
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Selecionar");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						TPLocation2 loc = null;
						Session session = Session.getInstance();
						StretchType typeSel = listTypes.getSelectedValue();
						
						for(int i = selStart; i <= selEnd; i++){
							loc = session.getCurrentTrail().get(i);
							
							loc.setTypeId(typeSel.getId());
							
							loc.setSelected(false);
						}
						
						MarkStretchView.this.stretchChangeListener.onStretchTypeChanged(session.getCurrentTrail().get(0).getTypeId());					
						
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		{
			DefaultListModel<StretchType> listModel = new DefaultListModel<>();
			
			for (Map.Entry<String, StretchType> entry : Session.getInstance().getStretchTypes().entrySet()){
				listModel.addElement(entry.getValue());
			}
			
			listTypes = new JList<StretchType>(listModel);
			listTypes.setCellRenderer(new StretchTypeRenderer());
			JScrollPane scPane = new JScrollPane(listTypes);
			
			getContentPane().add(scPane, BorderLayout.CENTER);
		}
	}
}
