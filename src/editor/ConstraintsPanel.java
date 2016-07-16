/* This class is a JDialog that allows user to modify constraints
 */
package editor;
 

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class ConstraintsPanel {
	private JDialog dialog;
	private int nSymbols;
	
	private String constraints;
	private DefaultTableModel tableModel;

	public ConstraintsPanel(int num, double[][][] fibre, int width, int height,
			JFrame f, DrawingArea drawingArea, DefaultTableModel m) {
		tableModel = m;
		int widthVincoliPanel = 500;
		int heightVincoliPanel = 500;
		nSymbols = num;
		Image piuIcon = null;
		Image menoIcon = null;
		Image loadFileIcon = null;
		Image okIcon = null;
		try {
			piuIcon = ImageIO.read(new File("src/images/piu.png"));
			menoIcon = ImageIO.read(new File("src/images/meno.png"));
			loadFileIcon = ImageIO.read(new File("src/images/loadFile.png"));
			okIcon = ImageIO.read(new File("src/images/ok.png"));

		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null, "Error loading image");
		}

		dialog = new JDialog(f, ModalityType.APPLICATION_MODAL);
		dialog.setPreferredSize(new Dimension(widthVincoliPanel,
				heightVincoliPanel));
		dialog.validate();
		dialog.setLocation(width / 2 - widthVincoliPanel / 2, height / 2
				- heightVincoliPanel / 2);
		dialog.setLayout(null);
		dialog.getContentPane().setBackground(new Color(32, 32, 32));
		MyTable table = new MyTable();
		table(table, tableModel);
		JLabel lblConstraints = new JLabel("Constraints");
		lblConstraints.setForeground(new Color(220, 20, 60));
		lblConstraints.setFont(new Font("Kokonor", Font.PLAIN, 30));
		lblConstraints.setBounds(160, 10, 472, 80);
		dialog.getContentPane().add(lblConstraints);
		Image newimg = piuIcon.getScaledInstance(30, 30,
				java.awt.Image.SCALE_SMOOTH);
		ImageIcon newIcon = new ImageIcon(newimg);
		JButton addRowButton = new JButton();
		addRowButton.setIcon(newIcon);
		addRowButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		addRowButton.setBorder(null);
		addRowButton.setContentAreaFilled(false);
		addRowButton.setLocation(30, 100);
		addRowButton.setSize(new Dimension(30, 30));
		addRowButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				addRow(tableModel, table);

			}
		});

		addRowButton.setToolTipText("Add constraint");
		dialog.getContentPane().add(addRowButton);

		newimg = menoIcon
				.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
		newIcon = new ImageIcon(newimg);
		JButton removeRowButton = new JButton();
		removeRowButton.setLocation(30, 160);
		removeRowButton.setIcon(newIcon);
		removeRowButton.setBorder(null);
		removeRowButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		removeRowButton.setContentAreaFilled(false);
		removeRowButton.setSize(new Dimension(30, 30));
		removeRowButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				removeSelectedRow(table, tableModel);

			}
		});

		removeRowButton.setToolTipText("Remove constraint");
		dialog.getContentPane().add(removeRowButton);

		newimg = loadFileIcon.getScaledInstance(30, 30,
				java.awt.Image.SCALE_SMOOTH);
		newIcon = new ImageIcon(newimg);
		JButton loadButton = new JButton();
		loadButton.setLocation(30, 220);
		loadButton.setIcon(newIcon);
		loadButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		loadButton.setBorder(null);
		loadButton.setContentAreaFilled(false);
		loadButton.setSize(new Dimension(30, 30));
		loadButton.setToolTipText("Load constraint file");
		loadButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"txt", "txt");
				chooser.setFileFilter(filter);
				int returnVal = chooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					BufferedReader in = null;
					try {
						in = new BufferedReader(new FileReader(chooser
								.getSelectedFile().getPath()));
						while (tableModel.getRowCount() > 0) {
							tableModel.removeRow(0);
						}

						String read = null;
						while ((read = in.readLine()) != null) {
							String[] splited = read.split("\\s+");
							try {
								if (splited.length == 2
										&& Integer.parseInt(splited[0]) >= 0
										&& Integer.parseInt(splited[0]) < 10
										&& Integer.parseInt(splited[1]) >= 0
										&& Integer.parseInt(splited[1]) < 10)
									addRowFromFile(table, tableModel, splited);
								else {
									JOptionPane.showMessageDialog(null,
											"Invalid file, check the contents");
									while (tableModel.getRowCount() > 0) {
										tableModel.removeRow(0);
									}
									break;
								}
							}

							catch (NumberFormatException exc) {
								JOptionPane.showMessageDialog(null,
										"Invalid file, check the contents");
								while (tableModel.getRowCount() > 0) {
									tableModel.removeRow(0);
								}
								break;
							}

						}
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null,"There has been a problem");

					} finally {
						try {
							
							in.close();
						} catch (Exception e1) {}
					}

				}
			}
		});
		dialog.getContentPane().add(loadButton);

		newimg = okIcon.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
		newIcon = new ImageIcon(newimg);
		JButton okButton = new JButton();
		okButton.setLocation(400, 400);
		okButton.setIcon(newIcon);
		okButton.setBorder(null);
		okButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		okButton.setContentAreaFilled(false);
		okButton.setSize(new Dimension(30, 30));
		okButton.setToolTipText("update constraints");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (tableModel.getRowCount() >= 1
						&& (tableModel.getValueAt(tableModel.getRowCount() - 1,
								0).equals("") ^ tableModel.getValueAt(
								tableModel.getRowCount() - 1, 1).equals(""))) {
					JOptionPane
							.showMessageDialog(null, "Complete empty fields");
					return;
				}

				constraints = "";
				for (int i = 0; i < table.getRowCount(); i++)
					if (table.getValueAt(i, 0) != null
							&& !table.getValueAt(i, 0).equals(" ")
							&& table.getValueAt(i, 1) != null
							&& !table.getValueAt(i, 1).equals(" ")) {
						constraints += table.getValueAt(i, 0) + " "
								+ table.getValueAt(i, 1) + "\n";
					}

				drawingArea.setConstraints(constraints);

				dialog.setVisible(false);
			}
		});
		dialog.getContentPane().add(okButton);
		dialog.pack();

		dialog.setVisible(true);
	}

	public void setUpStringColumn(JTable table, TableColumn stringColumn) {
		JComboBox<Integer> comboBox = new JComboBox<Integer>();
		for (int i = 0; i < nSymbols; i++) {
			comboBox.addItem(i);
		}

		stringColumn.setCellEditor(new DefaultCellEditor(comboBox));
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		stringColumn.setCellRenderer(renderer);
	}

	public void table(MyTable table, DefaultTableModel t) {

		table.setModel(t);
		dialog.getContentPane().setLayout(null);
		JScrollPane ScrollCatalogo = new JScrollPane(table);
		table.getTableHeader().setBackground(new Color(32, 32, 32));
		table.getTableHeader().setForeground(new Color(220, 20, 60));
		ScrollCatalogo.getViewport().setBackground(new Color(32, 32, 32));
		ScrollCatalogo.setBounds(100, 100, 300, 200);
		dialog.getContentPane().add(ScrollCatalogo);

	}

	public void addRow(DefaultTableModel t1, JTable t) {
		if (t.getRowCount() >= 1
				&& (t.getValueAt(t.getRowCount() - 1, 0).equals("") || t
						.getValueAt(t.getRowCount() - 1, 1).equals(""))) {
			JOptionPane.showMessageDialog(null,
					"Complete empty fields to add another row");
			return;
		}
		String[] emptyRow = { "", "" };
		setUpStringColumn(t, t.getColumnModel().getColumn(0));
		setUpStringColumn(t, t.getColumnModel().getColumn(1));
		t1.addRow(emptyRow);

	}

	public void addRowFromFile(JTable t, DefaultTableModel t1, String[] s) {
		setUpStringColumn(t, t.getColumnModel().getColumn(0));
		setUpStringColumn(t, t.getColumnModel().getColumn(1));
		t1.addRow(s);

	}

	public void removeSelectedRow(JTable table, DefaultTableModel t) {

		if (table.getSelectedRow() != -1) {

			if (table.isEditing()) {
				table.getCellEditor().stopCellEditing();
				t.removeRow(table.getSelectedRow());
			}
		} else
			JOptionPane.showMessageDialog(null, "Select a constraints");
	}

	class MyTable extends JTable {
		private static final long serialVersionUID = 1L;
		private boolean Editable = true;

		@Override
		public boolean isCellEditable(int row, int column) {

			return Editable;
		}

		public void setEditable() {

			Editable = true;
		}

		public void setNoEditable() {

			Editable = false;
		}

	}

}
