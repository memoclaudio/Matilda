package editor;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class LabelTextButtonPanel extends JPanel {

	private JLabel label;
	private JTextField textField;
	private JButton jbutton;

	public LabelTextButtonPanel(String text, String toolTip) {
		label = new JLabel(text);
		textField = new JTextField();
		jbutton = new JButton();
		

		setBorder(new EmptyBorder(10, 60, 10, 10));

		label.setForeground(new Color(220, 20, 60));
		label.setFont(new Font("Kokonor", Font.PLAIN, 18));

		Image okIcon = null;
		try {

			okIcon = ImageIO.read(new File("src/images/ok.png"));

		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null, "Error loading image");
		}
		
		Image newimg = okIcon.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
		ImageIcon newIcon = new ImageIcon(newimg);

		jbutton.setIcon(newIcon);
		jbutton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		jbutton.setBorder(null);
		jbutton.setContentAreaFilled(false);
		jbutton.setSize(new Dimension(30, 30));
		

		jbutton.setToolTipText(toolTip);

		textField.setForeground(new Color(220, 20, 60));
		textField.setFont(new Font("Kokonor", Font.PLAIN, 12));
		GroupLayout groupLayout=new GroupLayout(this);
		groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup().addGroup(groupLayout.createParallelGroup().addComponent(label).addComponent(textField,120,170,220)).addGap(40).addComponent(jbutton));
		
		
		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup().addComponent(label).addGroup(groupLayout.createParallelGroup().addComponent(textField,25,25,25).addComponent(jbutton)));
				
		this.setLayout(groupLayout);
		setBackground(new Color(32, 32, 32));
		setSize(100,100);
		
		
		this.setVisible(true);

	}

	public JButton getJbutton() {
		return jbutton;
	}

	public JTextField getTextField() {
		return textField;
	}

}
