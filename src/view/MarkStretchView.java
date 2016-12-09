package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import model.LocationType;
import model.TPLocation;
import view.listeners.StretchTypeChangeListener;

import javax.swing.JLabel;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JRadioButton;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MarkStretchView extends JDialog {
	private static final long serialVersionUID = -5467253972384413352L;
	private final JPanel contentPanel = new JPanel();
	private int selStart = 0;
	private int selEnd = 0;

	private StretchTypeChangeListener stretchChangeListener;
	
	public MarkStretchView(int start, int end, StretchTypeChangeListener stretchChangeListener) {
		this.stretchChangeListener = stretchChangeListener;
		this.selStart = start;
		this.selEnd = end;
		
		setTitle("Tipo de Trecho");
		setBounds(100, 100, 493, 191);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{80, 80, 80, 80, 80, 80, 0};
		gbl_contentPanel.rowHeights = new int[]{48, 24, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel lblNewLabel = new JLabel("");
			lblNewLabel.setIcon(new ImageIcon("images/trail.png"));
			GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
			gbc_lblNewLabel.fill = GridBagConstraints.VERTICAL;
			gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel.gridx = 0;
			gbc_lblNewLabel.gridy = 0;
			contentPanel.add(lblNewLabel, gbc_lblNewLabel);
		}
		{
			JLabel lblNewLabel_3 = new JLabel("");
			lblNewLabel_3.setIcon(new ImageIcon("images/river.png"));
			GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
			gbc_lblNewLabel_3.anchor = GridBagConstraints.NORTH;
			gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel_3.gridx = 1;
			gbc_lblNewLabel_3.gridy = 0;
			contentPanel.add(lblNewLabel_3, gbc_lblNewLabel_3);
		}
		{
			JLabel lblNewLabel_1 = new JLabel("");
			lblNewLabel_1.setIcon(new ImageIcon("images/road.png"));
			GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
			gbc_lblNewLabel_1.anchor = GridBagConstraints.NORTH;
			gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel_1.gridx = 2;
			gbc_lblNewLabel_1.gridy = 0;
			contentPanel.add(lblNewLabel_1, gbc_lblNewLabel_1);
		}
		{
			JLabel lblNewLabel_4 = new JLabel("");
			lblNewLabel_4.setIcon(new ImageIcon("images/neve.png"));
			GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
			gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel_4.gridx = 3;
			gbc_lblNewLabel_4.gridy = 0;
			contentPanel.add(lblNewLabel_4, gbc_lblNewLabel_4);
		}
		{
			JLabel lblNewLabel_2 = new JLabel("");
			lblNewLabel_2.setIcon(new ImageIcon("images/forest.png"));
			GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
			gbc_lblNewLabel_2.anchor = GridBagConstraints.NORTH;
			gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel_2.gridx = 4;
			gbc_lblNewLabel_2.gridy = 0;
			contentPanel.add(lblNewLabel_2, gbc_lblNewLabel_2);
		}
		{
			JLabel lblNewLabel_5 = new JLabel("");
			lblNewLabel_5.setIcon(new ImageIcon("images/delete.png"));
			GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
			gbc_lblNewLabel_5.insets = new Insets(0, 0, 5, 0);
			gbc_lblNewLabel_5.gridx = 5;
			gbc_lblNewLabel_5.gridy = 0;
			contentPanel.add(lblNewLabel_5, gbc_lblNewLabel_5);
		}
		{
			JLabel lblTrilha = new JLabel("Trilha");
			GridBagConstraints gbc_lblTrilha = new GridBagConstraints();
			gbc_lblTrilha.insets = new Insets(0, 0, 5, 5);
			gbc_lblTrilha.gridx = 0;
			gbc_lblTrilha.gridy = 1;
			contentPanel.add(lblTrilha, gbc_lblTrilha);
		}
		{
			JLabel lblLeitoDeRio = new JLabel("Leito de Rio");
			GridBagConstraints gbc_lblLeitoDeRio = new GridBagConstraints();
			gbc_lblLeitoDeRio.insets = new Insets(0, 0, 5, 5);
			gbc_lblLeitoDeRio.gridx = 1;
			gbc_lblLeitoDeRio.gridy = 1;
			contentPanel.add(lblLeitoDeRio, gbc_lblLeitoDeRio);
		}
		{
			JLabel lblAsfalto = new JLabel("Asfalto");
			GridBagConstraints gbc_lblAsfalto = new GridBagConstraints();
			gbc_lblAsfalto.insets = new Insets(0, 0, 5, 5);
			gbc_lblAsfalto.gridx = 2;
			gbc_lblAsfalto.gridy = 1;
			contentPanel.add(lblAsfalto, gbc_lblAsfalto);
		}
		{
			JLabel lblNeve = new JLabel("Neve");
			GridBagConstraints gbc_lblNeve = new GridBagConstraints();
			gbc_lblNeve.insets = new Insets(0, 0, 5, 5);
			gbc_lblNeve.gridx = 3;
			gbc_lblNeve.gridy = 1;
			contentPanel.add(lblNeve, gbc_lblNeve);
		}
		{
			JLabel lblFloresta = new JLabel("Floresta");
			GridBagConstraints gbc_lblFloresta = new GridBagConstraints();
			gbc_lblFloresta.insets = new Insets(0, 0, 5, 5);
			gbc_lblFloresta.gridx = 4;
			gbc_lblFloresta.gridy = 1;
			contentPanel.add(lblFloresta, gbc_lblFloresta);
		}
		{
			JLabel lblRemover = new JLabel("Remover");
			GridBagConstraints gbc_lblRemover = new GridBagConstraints();
			gbc_lblRemover.insets = new Insets(0, 0, 5, 0);
			gbc_lblRemover.gridx = 5;
			gbc_lblRemover.gridy = 1;
			contentPanel.add(lblRemover, gbc_lblRemover);
		}
		
		JRadioButton radioButtonTrail = new JRadioButton("");
		GridBagConstraints gbc_radioButtonTrail = new GridBagConstraints();
		gbc_radioButtonTrail.insets = new Insets(0, 0, 0, 5);
		gbc_radioButtonTrail.gridx = 0;
		gbc_radioButtonTrail.gridy = 2;
		radioButtonTrail.setSelected(true);
		contentPanel.add(radioButtonTrail, gbc_radioButtonTrail);
	
	
		JRadioButton radioButtonRiver = new JRadioButton("");
		GridBagConstraints gbc_radioButtonRiver = new GridBagConstraints();
		gbc_radioButtonRiver.insets = new Insets(0, 0, 0, 5);
		gbc_radioButtonRiver.gridx = 1;
		gbc_radioButtonRiver.gridy = 2;
		contentPanel.add(radioButtonRiver, gbc_radioButtonRiver);

		JRadioButton radioButtonRoad = new JRadioButton("");
		GridBagConstraints gbc_radioButtonRoad = new GridBagConstraints();
		gbc_radioButtonRoad.insets = new Insets(0, 0, 0, 5);
		gbc_radioButtonRoad.gridx = 2;
		gbc_radioButtonRoad.gridy = 2;
		contentPanel.add(radioButtonRoad, gbc_radioButtonRoad);

		JRadioButton radioButtonSnow = new JRadioButton("");
		GridBagConstraints gbc_radioButtonSnow = new GridBagConstraints();
		gbc_radioButtonSnow.insets = new Insets(0, 0, 0, 5);
		gbc_radioButtonSnow.gridx = 3;
		gbc_radioButtonSnow.gridy = 2;
		contentPanel.add(radioButtonSnow, gbc_radioButtonSnow);

		JRadioButton radioButtonForest = new JRadioButton("");
		GridBagConstraints gbc_radioButtonForest = new GridBagConstraints();
		gbc_radioButtonForest.insets = new Insets(0, 0, 0, 5);
		gbc_radioButtonForest.gridx = 4;
		gbc_radioButtonForest.gridy = 2;
		contentPanel.add(radioButtonForest, gbc_radioButtonForest);
		
    	JRadioButton radioButtonDelete = new JRadioButton("");
    	GridBagConstraints gbc_radioButtonDelete = new GridBagConstraints();
    	gbc_radioButtonDelete.gridx = 5;
    	gbc_radioButtonDelete.gridy = 2;
    	contentPanel.add(radioButtonDelete, gbc_radioButtonDelete);
    
		ButtonGroup group = new ButtonGroup();
	    group.add(radioButtonTrail);
	    group.add(radioButtonRoad);
	    group.add(radioButtonRiver);
	    group.add(radioButtonSnow);
	    group.add(radioButtonForest);
	    group.add(radioButtonDelete);
	    


		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						TPLocation loc = null;
						
						for(int i = selStart; i <= selEnd; i++){
							loc = Session.currentTrail.get(i);
							
							if(radioButtonTrail.isSelected())
								loc.setType(LocationType.TRAIL);
							else if(radioButtonRoad.isSelected())
								loc.setType(LocationType.ROAD);
							else if(radioButtonRiver.isSelected())
								loc.setType(LocationType.RIVER);
							else if(radioButtonSnow.isSelected())
								loc.setType(LocationType.SNOW);
							else if(radioButtonForest.isSelected())
								loc.setType(LocationType.FOREST);
							else if(radioButtonDelete.isSelected())
								loc.setType(LocationType.INVALID);
							
							loc.setSelected(false);
						}
						
						MarkStretchView.this.stretchChangeListener.onStretchTypeChanged(Session.currentTrail.get(0).getType());					
						
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
	}
}
