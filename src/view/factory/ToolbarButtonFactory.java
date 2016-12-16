package view.factory;

import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class ToolbarButtonFactory {
	public static JButton makeNavigationButton(String imageName, String actionCommand, String toolTipText, ActionListener listener) {
		String imgLocation = "images/" + imageName + ".png";
		
		JButton button = new JButton();
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
		button.addActionListener(listener);
		button.setIcon(new ImageIcon(imgLocation));
		button.setFocusPainted(false);

		return button;
	}
}
