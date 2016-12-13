package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Date;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import model.StretchType;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.Color;

public class AddEditStretchView extends JDialog implements ActionListener, MouseListener {
	private static final long serialVersionUID = -2394375475185825393L;
	
	private final JPanel contentPanel = new JPanel();
	private JButton btnOk;
	private JButton btnCancel;
	private JPanel fieldColor;
	private JTextField fieldName;

	private StretchType type;
	private JList<StretchType> list;
	
	public AddEditStretchView(StretchType type, JList<StretchType> list) {
		this.type = type;
		this.list = list;
		
		setTitle("Trecho");
		setBounds(100, 100, 180, 229);
		getContentPane().setLayout(new BorderLayout());
		
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblNome = new JLabel("Nome:");
			lblNome.setBounds(10, 11, 46, 14);
			contentPanel.add(lblNome);
		}
		{
			fieldName = new JTextField();
			fieldName.setBounds(10, 26, 140, 20);
			contentPanel.add(fieldName);
			fieldName.setColumns(10);
		}
		{
			JLabel lblCor = new JLabel("Cor:");
			lblCor.setBounds(10, 57, 46, 14);
			contentPanel.add(lblCor);
		}
		{
			fieldColor = new JPanel();
			fieldColor.setBackground(Color.GREEN);
			fieldColor.setBounds(10, 82, 140, 54);
			fieldColor.addMouseListener(this);
			contentPanel.add(fieldColor);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				btnOk = new JButton("OK");
				btnOk.addActionListener(this);
				buttonPane.add(btnOk);
				getRootPane().setDefaultButton(btnOk);
			}
			{
				btnCancel = new JButton("Cancel");
				btnCancel.setActionCommand("Cancel");
				btnCancel.addActionListener(this);
				buttonPane.add(btnCancel);
			}
		}
		
		initializeFields();
	}

	private void initializeFields(){
		if(type != null){
			fieldName.setText(type.getName());
			fieldColor.setBackground(type.getColor());
		}
	}
	
	private void save(){
		if(fieldName.getText().equals("")){
			JOptionPane.showMessageDialog(this.getParent(), "Insira um nome para esse tipo de trecho", "Erro", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		if(type == null){
			Date now = new Date();
			String id = fieldName.getText() + now;
			StretchType newType = new StretchType(id, fieldName.getText(), fieldColor.getBackground());
			
			Session.getInstance().getStretchTypes().put(id, newType);
			DefaultListModel<StretchType> model = (DefaultListModel<StretchType>) list.getModel();
			model.addElement(newType);
		}else{
			type.setName(fieldName.getText());
			type.setColor(fieldColor.getBackground());
			list.setModel((DefaultListModel<StretchType>) list.getModel());
		}
		
		dispose();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("OK")){
			save();
		}
		
		dispose();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Color initColor;
		
		if(type != null)
			initColor = type.getColor();
		else
			initColor = Color.GREEN;
		
		Color c = JColorChooser.showDialog(getParent(), "Cor do trecho", initColor);
		if (c != null)
			fieldColor.setBackground(c);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}
}
