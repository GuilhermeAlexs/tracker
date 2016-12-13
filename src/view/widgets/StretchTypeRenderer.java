package view.widgets;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import model.StretchType;

public class StretchTypeRenderer extends JPanel implements ListCellRenderer<StretchType>  {
	private static final long serialVersionUID = -3685338588444744397L;
	private JLabel name;
	private JPanel colorView;
	
	public StretchTypeRenderer() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		name = new JLabel();
		colorView = new JPanel();
		add(colorView);
		add(name);
	}
	
	public Component getListCellRendererComponent(JList list, StretchType value,
	  int index, boolean isSelected, boolean cellHasFocus) {
	
	  StretchType entry = (StretchType) value;

	  name.setText(entry.getName());

	  colorView.setPreferredSize(new Dimension(80,5));
	  colorView.setBackground(entry.getColor());
	  
      if (isSelected) {
          setBackground(list.getSelectionBackground());
          setForeground(list.getSelectionForeground());
      } else {
          setBackground(list.getBackground());
          setForeground(list.getForeground());
      }
	  
	  return this;
	}
}
