package view;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import database.DatabaseManager;
import model.Configurations;
import utils.NetworkUtils;

import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.border.BevelBorder;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.JCheckBox;

public class ConfigurationView extends JFrame implements ActionListener, MouseListener {
	private static final long serialVersionUID = 2241763796533710077L;

	private JPanel contentPane;
	private JSpinner spnMinimumSpeed;
	private JSpinner spnMaximumSpeed;
	private JSpinner spnSteps;
	private JPanel panelElevationColor;
	private JPanel panelSpeedcolor;
	private JPanel panelSelectionColor;
	private JButton btnApply;
	private JButton btnCancel;
	private JTextField fieldUser;
	private JTextField fieldPassword;
	private JTextField fieldAddress;
	private JTextField fieldPort;
	private JSpinner spnRestTime;
	private JCheckBox checkSmooth;

	private void initializeFields(){
		Configurations conf = Configurations.getInstance();

		spnMinimumSpeed.setValue(conf.getMinimumSpeed() * 3.6f);
		spnMaximumSpeed.setValue(conf.getMaximumSpeed() * 3.6f);
		spnRestTime.setValue(conf.getRestTime() / 60d);
		spnSteps.setValue(conf.getSteps());

		panelElevationColor.setBackground(conf.getElevationGraphColor());
		panelSpeedcolor.setBackground(conf.getSpeedGraphColor());
		panelSelectionColor.setBackground(conf.getSelectionColor());

		fieldUser.setText(conf.getProxyUser());
		fieldPassword.setText(conf.getProxyPassword());
		fieldAddress.setText(conf.getProxyAddress());
		fieldPort.setText(conf.getProxyPort());

		checkSmooth.setSelected(conf.isSmooth());
	}

	public ConfigurationView() {
		setTitle("Configurações");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(new ImageIcon("images/logo.png").getImage());
		setBounds(100, 100, 497, 324);
		setResizable(false);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		tabbedPane.addTab("Limites e Cálculos", null, panel, null);
		panel.setLayout(null);

		JLabel lblVelocidadeMnima = new JLabel("Velocidade M\u00EDnima:");
		lblVelocidadeMnima.setBounds(12, 23, 169, 15);
		panel.add(lblVelocidadeMnima);

		spnMinimumSpeed = new JSpinner(new SpinnerNumberModel(0, 0, 5000, 0.1));
		spnMinimumSpeed.setBounds(12, 50, 120, 20);
		panel.add(spnMinimumSpeed);

		JLabel lblVelocidadeMxima = new JLabel("Velocidade M\u00E1xima:");
		lblVelocidadeMxima.setBounds(12, 82, 150, 15);
		panel.add(lblVelocidadeMxima);

		spnMaximumSpeed = new JSpinner(new SpinnerNumberModel(0, 0, 5000, 0.1));
		spnMaximumSpeed.setBounds(12, 109, 120, 20);
		panel.add(spnMaximumSpeed);

		JLabel lblIntervalosDeInclinao = new JLabel("Intervalos de Inclina\u00E7\u00E3o:");
		lblIntervalosDeInclinao.setBounds(12, 141, 195, 15);
		panel.add(lblIntervalosDeInclinao);

		spnSteps = new JSpinner(new SpinnerNumberModel(1d, 1d, 10d, 1d));
		spnSteps.setBounds(12, 168, 120, 20);
		panel.add(spnSteps);

		JLabel lblTempoDeDescanso = new JLabel("Tempo de Descanso:");
		lblTempoDeDescanso.setBounds(288, 23, 169, 15);
		panel.add(lblTempoDeDescanso);

		spnRestTime = new JSpinner(new SpinnerNumberModel(1d, 1d, 60d, 1d));
		spnRestTime.setBounds(288, 50, 120, 20);
		panel.add(spnRestTime);

		checkSmooth = new JCheckBox("Suavizar Altitude");
		checkSmooth.setSelected(true);
		checkSmooth.setBounds(288, 107, 169, 23);
		panel.add(checkSmooth);

		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("Aparência", null, panel_2, null);
		panel_2.setLayout(null);

		JLabel lblGrficoDeElevao = new JLabel("Gr\u00E1fico de Eleva\u00E7\u00E3o");
		lblGrficoDeElevao.setBounds(12, 23, 153, 15);
		panel_2.add(lblGrficoDeElevao);

		panelElevationColor = new JPanel();
		panelElevationColor.addMouseListener(this);
		panelElevationColor.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelElevationColor.setBackground(new Color(221, 141, 22));
		panelElevationColor.setBounds(12, 50, 133, 48);
		panel_2.add(panelElevationColor);

		JLabel lblGrficoDeVelocidade = new JLabel("Gr\u00E1fico de Velocidade");
		lblGrficoDeVelocidade.setBounds(12, 122, 166, 15);
		panel_2.add(lblGrficoDeVelocidade);

		panelSpeedcolor = new JPanel();
		panelSpeedcolor.addMouseListener(this);
		panelSpeedcolor.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelSpeedcolor.setBackground(new Color(163, 194, 224, 90));
		panelSpeedcolor.setBounds(12, 149, 133, 48);
		panel_2.add(panelSpeedcolor);

		JLabel lblSeleo = new JLabel("Sele\u00E7\u00E3o");
		lblSeleo.setBounds(273, 23, 70, 15);
		panel_2.add(lblSeleo);

		panelSelectionColor = new JPanel();
		panelSelectionColor.addMouseListener(this);
		panelSelectionColor.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelSelectionColor.setBackground(Color.RED);
		panelSelectionColor.setBounds(273, 49, 133, 48);
		panel_2.add(panelSelectionColor);

		JPanel panel_6 = new JPanel();
		tabbedPane.addTab("Proxy", null, panel_6, null);
		panel_6.setLayout(null);

		JLabel lblUsurio = new JLabel("Usu\u00E1rio:");
		lblUsurio.setBounds(12, 24, 70, 15);
		panel_6.add(lblUsurio);

		fieldUser = new JTextField();
		fieldUser.setBounds(12, 39, 114, 19);
		panel_6.add(fieldUser);
		fieldUser.setColumns(10);

		JLabel lblSenha = new JLabel("Senha:");
		lblSenha.setBounds(12, 70, 70, 15);
		panel_6.add(lblSenha);

		fieldPassword = new JTextField();
		fieldPassword.setBounds(12, 85, 114, 19);
		panel_6.add(fieldPassword);
		fieldPassword.setColumns(10);

		JLabel lblEndereo = new JLabel("Endere\u00E7o:");
		lblEndereo.setBounds(12, 118, 114, 15);
		panel_6.add(lblEndereo);

		fieldAddress = new JTextField();
		fieldAddress.setBounds(12, 134, 114, 19);
		panel_6.add(fieldAddress);
		fieldAddress.setColumns(10);

		JLabel lblPorta = new JLabel("Porta:");
		lblPorta.setBounds(12, 163, 70, 15);
		panel_6.add(lblPorta);

		fieldPort = new JTextField();
		fieldPort.setBounds(12, 179, 114, 19);
		panel_6.add(fieldPort);
		fieldPort.setColumns(10);

		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

		btnApply = new JButton("Aplicar");
		panel_1.add(btnApply);
		btnApply.addActionListener(this);

		btnCancel = new JButton("Cancelar");
		panel_1.add(btnCancel);
		btnCancel.addActionListener(this);

		initializeFields();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()){
			case "Aplicar":
				Configurations conf = Configurations.getInstance();

				Double min = (Double) spnMinimumSpeed.getValue();
				Double max = (Double) spnMaximumSpeed.getValue();
				Double restTime = (Double) spnRestTime.getValue();
				Double steps = (Double) spnSteps.getValue();

				Color elevColor = panelElevationColor.getBackground();
				Color speedColor = panelSpeedcolor.getBackground();
				Color selectionColor = panelSelectionColor.getBackground();

				String address = fieldAddress.getText();
				String port = fieldPort.getText();
				String user = fieldUser.getText();
				String pass = fieldPassword.getText();

				if(min != null && min > 0)
					conf.setMinimumSpeed(min / 3.6f);

				if(max != null && min > 0)
					conf.setMaximumSpeed(max / 3.6f);

				if(steps != null && min > 0)
					conf.setSteps(steps);

				if(restTime != null && restTime > 0)
					conf.setRestTime(restTime * 60f);

				conf.setElevationGraphColor(elevColor);
				conf.setSpeedGraphColor(speedColor);
				conf.setSelectionColor(selectionColor);
				conf.setProxyAddress(address);
				conf.setProxyUser(user);
				conf.setProxyPassword(pass);
				conf.setProxyPort(port);
				conf.setSmooth(checkSmooth.isSelected());

				NetworkUtils.useProxy(conf.getProxyAddress(), conf.getProxyPort(), 
						conf.getProxyUser(), conf.getProxyPassword());

				DatabaseManager.getInstance().saveConfigurations(conf);

				dispose();
				break;
			case "Cancelar":
				dispose();
				break;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Configurations conf = Configurations.getInstance();
		
		if(e.getSource().equals(panelElevationColor)){
			Color initColor = conf.getElevationGraphColor();
			
			Color c = JColorChooser.showDialog(getParent(), "Cor do Gráfico de Elevação", initColor);
			if (c != null)
				panelElevationColor.setBackground(c);
		}else if(e.getSource().equals(panelSpeedcolor)){
			Color initColor = conf.getSpeedGraphColor();
			
			Color c = JColorChooser.showDialog(getParent(), "Cor do Gráfico de Velocidade", initColor);
			if (c != null)
				panelSpeedcolor.setBackground(c);
		}else if(e.getSource().equals(panelSelectionColor)){
			Color initColor = conf.getSelectionColor();
			
			Color c = JColorChooser.showDialog(getParent(), "Cor da Seleção", initColor);
			if (c != null)
				panelSelectionColor.setBackground(c);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
}
