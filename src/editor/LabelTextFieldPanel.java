package editor;

import java.awt.Color;
import java.awt.Font;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class LabelTextFieldPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
