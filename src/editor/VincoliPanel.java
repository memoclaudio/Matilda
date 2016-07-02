package editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class VincoliPanel extends JPanel {
	PaintArea p;
	JProgressBar pbar;

	int valueBar = 0;
	int fetta;
	JFrame frame2;
	
	public VincoliPanel(PaintArea p, double[][][] fibre) {
		pbar = new JProgressBar(0, 100);
		pbar.setValue(valueBar);
		pbar.setVisible(false);
		fetta = (int) (fibre.length * 0.02);
		pbar.setBounds(150, 340, 200, 30);
		pbar.setStringPainted(true);
		add(pbar);
		this.p = p;

		setSize(100, 100);
		setLayout(null);

		DefaultTableModel t = new DefaultTableModel();
		MyTable TabellaPrenotati = new MyTable();
		TabellaPrenotati(TabellaPrenotati, t);

		JLabel lblVincoli = new JLabel("VINCOLI DI SIMILARITA'");
		lblVincoli.setForeground(Color.BLACK);
		lblVincoli.setFont(new Font("Kokonor", Font.PLAIN, 30));
		lblVincoli.setBounds(80, 10, 472, 80);
		add(lblVincoli);

		JButton addRowButton = new JButton("+");
		addRowButton.setLocation(30, 100);
		addRowButton.setSize(new Dimension(50, 50));
		addRowButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				addRow(t, TabellaPrenotati);

			}
		});

		addRowButton.setToolTipText("Add constraint");
		add(addRowButton);

		JButton removeRowButton = new JButton("-");
		removeRowButton.setLocation(30, 160);
		removeRowButton.setSize(new Dimension(50, 50));
		removeRowButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				removeSelectedRow(TabellaPrenotati, t);

			}
		});

		removeRowButton.setToolTipText("Remove constraint");
		add(removeRowButton);

		JButton loadButton = new JButton("Load");
		loadButton.setLocation(100, 400);
		loadButton.setSize(new Dimension(100, 50));
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
					// System.out.println("You chose to open this file: " +
					// chooser.getSelectedFile().getPath());

					BufferedReader in = null;
					try {
						in = new BufferedReader(new FileReader(chooser
								.getSelectedFile().getPath()));
						while (t.getRowCount() > 0) {
							t.removeRow(0);
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
									addRowFromFile(TabellaPrenotati, t, splited);
								else {
									JOptionPane
											.showMessageDialog(null,
													"File non valido, verificare il contenuto");
									while (t.getRowCount() > 0) {
										t.removeRow(0);
									}
									break;
								}
							}

							catch (NumberFormatException exc) {
								JOptionPane
										.showMessageDialog(null,
												"File non valido, verificare il contenuto");
								while (t.getRowCount() > 0) {
									t.removeRow(0);
								}
								break;
							}

						}
					} catch (IOException e1) {
						System.out.println("There was a problem: " + e1);
						e1.printStackTrace();
					} finally {
						try {
							in.close();
						} catch (Exception e1) {
						}
					}

				}
			}
		});
		add(loadButton);

		JButton generateButton = new JButton("Generate");
		generateButton.setLocation(250, 400);
		generateButton.setSize(new Dimension(150, 50));
		generateButton.setToolTipText("Get result");
		generateButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				new Thread(new Runnable() {
					public void run() {
						
						/*
						 * qua devo scandire tutte le fibre del tck, per ognuna
						 * devo creare la stringa corrispondente e lanciare sbed
						 * con quest ultima e la stringa modello,
						 * successivamente salvare il tutto su un file
						 */

						if (!p.emptyModel()) {
							String model = Converter.convertMatrixToString(
									p.getModelVertex(), p.getNumColors());
							pbar.setVisible(true);
							int soglia=6;
							ArrayList<Fibra> fibreResult = null;
							fibreResult = new ArrayList<Fibra>();
							sbed(fibre, model, soglia, fibreResult);
							ResultPanel ResultPanel;
							if(fibreResult.size()!=0){
							//System.out.println(fibreResult.size());
								ResultPanel=new ResultPanel(fibreResult);
							ResultPanel.start();}
							
						} else
							System.out.println("Modello da convertire vuoto");

					}

					

				}).start();

			}
		});
		add(generateButton);

	}
	
	
	public void sbed(double[][][] fibre, String model, int soglia, ArrayList<Fibra> fibreResult) {
		 
		FileOutputStream risultati;
		FileOutputStream sbedTmp;
		try {

			risultati = new FileOutputStream("src/results/risultati.txt");
			PrintStream scrivi = new PrintStream(risultati);
			// System.out.println("fibre: " + fibre.length);
			for (int i = 0; i < fibre.length; i++) {
			//	System.out.println("i: " + i);
				// scrivi.println(Converter.convertMatrixToString(fibre[i],6));
				sbedTmp = new FileOutputStream("src/results/sbedTmp.txt");
				PrintStream scriviSbed = new PrintStream(sbedTmp);
				scriviSbed.println(model);
				scriviSbed.println(Converter.convertMatrixToString(fibre[i],
						p.getNumColors()));
				// scriviVincoli();
				/*
				 * ProcessBuilder builder = new ProcessBuilder(new String[] {
				 * "/bin/sh", "-c", "cat sbedTmp.txt | ./SBED" });
				 * 
				 * // se non voglio fare append tolgo Redirect.appendTo
				 * builder.redirectOutput(Redirect.appendTo(new File(
				 * "risultati.txt")));
				 * builder.redirectError(Redirect.appendTo(new File(
				 * "risultati.txt")));
				 * 
				 * try { Process pr = builder.start(); } catch (IOException e) {
				 * e.printStackTrace(); }
				 */

				String[] s = { "/bin/sh", "-c", "cat src/results/sbedTmp.txt | ./src/SBED" };
				Process p = Runtime.getRuntime().exec(s);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						p.getInputStream()));
				String line = in.readLine();
				if (line != null){
					
					if(Integer.parseInt(line)<=soglia){
						fibreResult.add(new Fibra(fibre[i]));
					}
					
					scrivi.println(line);
					}

				if (i == fetta) {

					fetta += (int) (fibre.length * 0.02);

					valueBar += 2;
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							pbar.setValue(valueBar);
							pbar.updateUI();
						}
					});

//					System.out.println("sono entrato con i= " + i
//							+ " e pbar Value: " + pbar.getValue());

				}

				if (i == fibre.length - 1) {
					File toDelete = new File("src/results/sbedTmp.txt");
					toDelete.delete();
				}

			}

			pbar.setVisible(false);

		} catch (IOException ex) {
			System.out.println("Errore: " + ex);
			System.exit(1);
		}
	}

	
		
	
	
	
	public void setUpStringColumn(JTable table, TableColumn stringColumn) {
		// Set up the editor for the sport cells.
		JComboBox comboBox = new JComboBox();
		comboBox.addItem("0");
		comboBox.addItem("1");
		comboBox.addItem("2");
		comboBox.addItem("3");
		comboBox.addItem("4");
		comboBox.addItem("5");
		comboBox.addItem("6");
		comboBox.addItem("7");
		comboBox.addItem("8");
		comboBox.addItem("9");
		stringColumn.setCellEditor(new DefaultCellEditor(comboBox));

		// Set up tool tips for the sport cells.
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setToolTipText("Click for combo box");
		stringColumn.setCellRenderer(renderer);
	}

	public void TabellaPrenotati(MyTable TabellaPrenotati, DefaultTableModel t) {
		t.addColumn("First String");
		t.addColumn("Second String");

		TabellaPrenotati.setModel(t);
		setLayout(null);

		JScrollPane ScrollCatalogo = new JScrollPane(TabellaPrenotati);
		ScrollCatalogo.setBounds(100, 100, 300, 200);
		add(ScrollCatalogo);

	}

	public void addRow(DefaultTableModel t1, JTable t) {
		String[] emptyRow = { "" };
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

		if (table.getSelectedRow() != -1)
			t.removeRow(table.getSelectedRow());
		else
			JOptionPane.showMessageDialog(null, "Seleziona un vincolo");
	}

	class MyTable extends JTable {

		/**
		 * 
		 */
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
