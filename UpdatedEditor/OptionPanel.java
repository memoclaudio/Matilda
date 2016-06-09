
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class OptionPanel extends JPanel {

	PaintArea editor;
	private int width;
	private int height;
	JButton button;

	JTextField textField;

	public OptionPanel(int w, int h, PaintArea e) {
		width = w;
		height = h;
		editor = e;
		setSize(100, 100);
		setLayout(null);
		// panel2.setBackground(java.awt.Color.BLACK);

		// reset
		button = new JButton("Reset");
		button.setLocation((int) (0.2 * width) / 2 - 65, (int) height / 7 - 25);
		button.setSize(new Dimension(130, 50));
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				editor.clear();

			}
		});
		add(button);
		// n
		textField = new JTextField(20);
		textField.setText(Integer.toString(editor.getNumPoints()));
		textField.setSize(130, 50);
		textField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				editor.setNumPoints(Integer.parseInt(textField.getText()));

			}
		});

		textField.setLocation((int) (0.2 * width) / 2 - 65,
				(int) (height / 7) * 2 - 25);
		add(textField);

		// Remove Last Point
		JButton rmvLast = new JButton("Remove Point");
		rmvLast.setLocation((int) (0.2 * width) / 2 - 65,
				(int) (height / 7) * 3 - 25);
		rmvLast.setSize(new Dimension(130, 50));
		rmvLast.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (editor.pointsSize() != 0)
					editor.removePoint();
				if (editor.pointsSize() == 1)
					editor.clear();
				editor.updatePoints();
				editor.updateGeometry();

			}
		});
		add(rmvLast);

		// caricaFile button
		JButton carica = new JButton("Upload");
		carica.setLocation((int) (0.2 * width) / 2 - 65,
				(int) (height / 7) * 4 - 25);
		carica.setSize(new Dimension(130, 50));
		add(carica);

		// Start button
		JButton start = new JButton("Start");
		start.setLocation((int) (0.2 * width) / 2 - 65,
				(int) (height / 7) * 6 - 25);
		start.setSize(new Dimension(130, 50));
		add(start);

		start.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				editor.getLineColorTransformation();

			}
		});
		JLabel n = new JLabel("n");
		n.setSize(50, 50);
		n.setLocation((int) (0.2 * width) / 2 - 80, (int) (height / 7) * 2 - 40);

		add(n);

		// n
		JTextField nColorsField = new JTextField(20);
		nColorsField.setText(Integer.toString(editor.getNumColors()));
		nColorsField.setSize(130, 50);
		nColorsField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				editor.setNColors(Integer.parseInt(nColorsField.getText()));

			}
		});

		nColorsField.setLocation((int) (0.2 * width) / 2 - 65,
				(int) (height / 7) * 5 - 25);
		add(nColorsField);

		JLabel nColors = new JLabel("Colors");
		nColors.setSize(50, 50);
		nColors.setLocation((int) (0.2 * width) / 2 - 120,
				(int) (height / 7) * 5 - 25);

		add(nColors);

		n.setToolTipText("Number of polygon points");

		textField.setHorizontalAlignment(JTextField.CENTER);
		nColorsField.setHorizontalAlignment(JTextField.CENTER);
		button.setToolTipText("Clear the scene");
		rmvLast.setToolTipText("Remove last point added");
		carica.setToolTipText("Upload file");
		start.setToolTipText("Start with research");

	}

}
