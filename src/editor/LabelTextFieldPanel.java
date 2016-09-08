package editor;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
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

public class LabelTextFieldPanel extends JPanel {

	private JLabel label;
	private JTextField textField;

	public LabelTextFieldPanel(String text, int textFieldHorizontalSize) {
		label = new JLabel(text);
		textField = new JTextField();

		label.setForeground(new Color(220, 20, 60));
		label.setFont(new Font("Kokonor", Font.PLAIN, 18));
		setBorder(new EmptyBorder(10, 60, 10, 10));

	
		
		textField.setForeground(new Color(220, 20, 60));
		textField.setFont(new Font("Kokonor", Font.PLAIN, 12));
		GroupLayout groupLayout=new GroupLayout(this);
		groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup().addComponent(label).addGap(40).addComponent(textField,textFieldHorizontalSize,textFieldHorizontalSize,textFieldHorizontalSize));
		
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup().addComponent(label).addComponent(textField,25,25,25));
		
		groupLayout.setAutoCreateGaps(true);
		groupLayout.setAutoCreateContainerGaps(true);
		
		this.setLayout(groupLayout);
		setBackground(new Color(32, 32, 32));
		setSize(100,100);
		
		
		this.setVisible(true);

	}

	public JTextField getTextField() {
		return textField;
	}

}
