package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import model.LocationType;
import model.TPLocation;
import view.listeners.StretchTypeChangeListener;

import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionEvent;

public class MarkStretchView extends JDialog implements MouseListener {
	private static final long serialVersionUID = -5467253972384413352L;
	private final JPanel contentPanel = new JPanel();
	private int selStart = 0;
	private int selEnd = 0;
	private JLabel selectedType = null;
	private Color selectionColor = new Color(190,190,190);
	
	private StretchTypeChangeListener stretchChangeListener;
	
	public MarkStretchView(int start, int end, StretchTypeChangeListener stretchChangeListener) {
		this.stretchChangeListener = stretchChangeListener;
		this.selStart = start;
		this.selEnd = end;
		
		setTitle("Tipo de Trecho");
		setBounds(100, 100, 493, 129);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{80, 80, 80, 80, 80, 80, 0};
		gbl_contentPanel.rowHeights = new int[]{48, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel btnTrail = new JLabel("Trilha");
			btnTrail.setBorder(new EmptyBorder(10, 15, 10, 15));
			btnTrail.setOpaque(true);
			btnTrail.setBackground(selectionColor);
			this.selectedType = btnTrail;
			btnTrail.setIcon(new ImageIcon("images/trail.png"));
			btnTrail.setHorizontalTextPosition(JLabel.CENTER);
			btnTrail.setVerticalTextPosition(JLabel.BOTTOM);
			btnTrail.addMouseListener(this);
			GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
			gbc_lblNewLabel.fill = GridBagConstraints.VERTICAL;
			gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel.gridx = 0;
			gbc_lblNewLabel.gridy = 0;
			contentPanel.add(btnTrail, gbc_lblNewLabel);
		}
		{
			JLabel btnRiver = new JLabel("Rio");
			btnRiver.setBorder(new EmptyBorder(10, 15, 10, 15));
			btnRiver.setIcon(new ImageIcon("images/river.png"));
			btnRiver.setHorizontalTextPosition(JLabel.CENTER);
			btnRiver.setVerticalTextPosition(JLabel.BOTTOM);
			btnRiver.addMouseListener(this);
			GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
			gbc_lblNewLabel_3.anchor = GridBagConstraints.NORTH;
			gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel_3.gridx = 1;
			gbc_lblNewLabel_3.gridy = 0;
			contentPanel.add(btnRiver, gbc_lblNewLabel_3);
		}
		{
			JLabel btnRoad = new JLabel("Estrada");
			btnRoad.setBorder(new EmptyBorder(10, 15, 10, 15));
			btnRoad.setIcon(new ImageIcon("images/road.png"));
			btnRoad.setHorizontalTextPosition(JLabel.CENTER);
			btnRoad.setVerticalTextPosition(JLabel.BOTTOM);
			btnRoad.addMouseListener(this);
			GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
			gbc_lblNewLabel_1.anchor = GridBagConstraints.NORTH;
			gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel_1.gridx = 2;
			gbc_lblNewLabel_1.gridy = 0;
			contentPanel.add(btnRoad, gbc_lblNewLabel_1);
		}
		{
			JLabel btnSnow = new JLabel("Neve");
			btnSnow.setBorder(new EmptyBorder(10, 15, 10, 15));
			btnSnow.setIcon(new ImageIcon("images/neve.png"));
			btnSnow.setHorizontalTextPosition(JLabel.CENTER);
			btnSnow.setVerticalTextPosition(JLabel.BOTTOM);
			btnSnow.addMouseListener(this);
			GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
			gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel_4.gridx = 3;
			gbc_lblNewLabel_4.gridy = 0;
			contentPanel.add(btnSnow, gbc_lblNewLabel_4);
		}
		{
			JLabel btnForest = new JLabel("Floresta");
			btnForest.setBorder(new EmptyBorder(10, 15, 10, 15));
			btnForest.setIcon(new ImageIcon("images/forest.png"));
			btnForest.setHorizontalTextPosition(JLabel.CENTER);
			btnForest.setVerticalTextPosition(JLabel.BOTTOM);
			btnForest.addMouseListener(this);
			GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
			gbc_lblNewLabel_2.anchor = GridBagConstraints.NORTH;
			gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel_2.gridx = 4;
			gbc_lblNewLabel_2.gridy = 0;
			contentPanel.add(btnForest, gbc_lblNewLabel_2);
		}
		{
			JLabel btnDelete = new JLabel("Deletar");
			btnDelete.setBorder(new EmptyBorder(10, 15, 10, 15));
			btnDelete.setIcon(new ImageIcon("images/delete.png"));
			btnDelete.setHorizontalTextPosition(JLabel.CENTER);
			btnDelete.setVerticalTextPosition(JLabel.BOTTOM);
			btnDelete.addMouseListener(this);
			GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
			gbc_lblNewLabel_5.insets = new Insets(0, 0, 5, 0);
			gbc_lblNewLabel_5.gridx = 5;
			gbc_lblNewLabel_5.gridy = 0;
			contentPanel.add(btnDelete, gbc_lblNewLabel_5);
		}
		{
			JLabel lblDesert = new JLabel("Deserto");
			lblDesert.setBorder(new EmptyBorder(10, 15, 10, 15));
			lblDesert.setIcon(new ImageIcon("images/desert.png"));
			lblDesert.setHorizontalTextPosition(JLabel.CENTER);
			lblDesert.setVerticalTextPosition(JLabel.BOTTOM);
			lblDesert.addMouseListener(this);
			GridBagConstraints gbc_lblDesert = new GridBagConstraints();
			gbc_lblDesert.insets = new Insets(0, 0, 0, 5);
			gbc_lblDesert.gridx = 0;
			gbc_lblDesert.gridy = 1;
			contentPanel.add(lblDesert, gbc_lblDesert);
		}
	    
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						/*TPLocation loc = null;
						
						for(int i = selStart; i <= selEnd; i++){
							loc = Session.currentTrail.get(i);
							switch(selectedType.getText()){
								case "Trilha":
									loc.setType(LocationType.TRAIL);
									break;
								case "Estrada":
									loc.setType(LocationType.ROAD);
									break;
								case "Rio":
									loc.setType(LocationType.RIVER);
									break;
								case "Neve":
									loc.setType(LocationType.SNOW);
									break;
								case "Floresta":
									loc.setType(LocationType.FOREST);
									break;
								case "Deserto":
									loc.setType(LocationType.DESERT);
									break;
								case "Deletar":
									loc.setType(LocationType.INVALID);
									break;
							}
							
							loc.setSelected(false);
						}
						
						MarkStretchView.this.stretchChangeListener.onStretchTypeChanged(Session.currentTrail.get(0).getType());					
						
						dispose();*/
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

	@Override
	public void mouseClicked(MouseEvent e) {
		if(selectedType != null){
			selectedType.setOpaque(false);
			selectedType.setBackground(null);
		}
		
		JLabel label = (JLabel) e.getSource();
		label.setOpaque(true);
		label.setBackground(selectionColor);
		this.selectedType = label;
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		JLabel label = (JLabel) e.getSource();
		label.setOpaque(true);
		label.setBackground(selectionColor);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		JLabel label = (JLabel) e.getSource();
		
		if(selectedType != null && selectedType.getText().equals(label.getText()))
			return;
		
		label.setOpaque(false);
		label.setBackground(null);
	}
}
