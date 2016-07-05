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

public class VincoliPanel  {
	JDialog frame;
	
	int nSymbols;
	private Image piuIcon=null;
	private Image menoIcon=null;
	private Image loadFileIcon=null;
	private Image okIcon=null;
String constraints;
	public VincoliPanel(int num, double[][][] fibre, int width, int height, JFrame f, PaintArea p) {
		int widthVincoliPanel=500;
		int heightVincoliPanel=500;
		nSymbols=num;
		try {
			piuIcon=ImageIO.read(new File("src/images/piu.png"));
			menoIcon=ImageIO.read(new File("src/images/meno.png"));
			loadFileIcon=ImageIO.read(new File("src/images/loadFile.png"));
			okIcon=ImageIO.read(new File("src/images/ok.png"));
		
					
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		frame=new JDialog( f,ModalityType.APPLICATION_MODAL);
		frame.setPreferredSize(new Dimension(widthVincoliPanel, heightVincoliPanel));
		frame.validate();
		frame.setLocation(width/2-widthVincoliPanel/2, height/2-heightVincoliPanel/2);
		frame.setLayout(null);
frame.getContentPane().setBackground(new Color(32,32,32));

		DefaultTableModel t = new DefaultTableModel();
		MyTable tabella = new MyTable();
		tabella(tabella, t);
	

		JLabel lblVincoli = new JLabel("Constraints");
		lblVincoli.setForeground(new Color(220,20,60));
		lblVincoli.setFont(new Font("Kokonor", Font.PLAIN, 30));
		lblVincoli.setBounds(160, 10, 472, 80);
		frame.getContentPane().add(lblVincoli);

		Image newimg =piuIcon.getScaledInstance(30,30,
				java.awt.Image.SCALE_SMOOTH);
			//scaled icon
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
				addRow(t, tabella);

			}
		});

		addRowButton.setToolTipText("Add constraint");
		frame.getContentPane().add(addRowButton);

		newimg =menoIcon.getScaledInstance(30,30,
				java.awt.Image.SCALE_SMOOTH);
			//scaled icon
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
				removeSelectedRow(tabella, t);

			}
		});

		removeRowButton.setToolTipText("Remove constraint");
		frame.getContentPane().add(removeRowButton);

		
		newimg =loadFileIcon.getScaledInstance(30,30,
				java.awt.Image.SCALE_SMOOTH);
			//scaled icon
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
									addRowFromFile(tabella, t, splited);
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
		frame.getContentPane().add(loadButton);
		
		
		newimg =okIcon.getScaledInstance(30,30,
				java.awt.Image.SCALE_SMOOTH);
			//scaled icon
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
				
				if(t.getRowCount()>=1 && (t.getValueAt(t.getRowCount()-1,0).equals("")^ t.getValueAt(t.getRowCount()-1,1).equals("")))
				{
					JOptionPane.showMessageDialog(null, "Completare i campi vuoti");
				return;
				}
                
										constraints="";
							for(int i=0;i<tabella.getRowCount();i++)
									if(tabella.getValueAt(i,0)!=null && !tabella.getValueAt(i,0).equals(" ") && tabella.getValueAt(i,1)!=null && !tabella.getValueAt(i,1).equals(" ") )
									{
										constraints+=tabella.getValueAt(i,0)+" "+tabella.getValueAt(i,1)+"\n";
									}
									
						p.constraints=constraints;
							
						frame.setVisible(false);
							}
		});
		frame.getContentPane().add(okButton);
		frame.pack();
		
		frame.setVisible(true);
	}
	
	
	
	
	
	
	
	public void setUpStringColumn(JTable table, TableColumn stringColumn) {
		// Set up the editor for the sport cells.

		JComboBox<Integer> comboBox = new JComboBox<Integer>();
		for(int i=0;i<nSymbols;i++){
			comboBox.addItem(i);
		}
	
		stringColumn.setCellEditor(new DefaultCellEditor(comboBox));

		// Set up tool tips for the sport cells.
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setToolTipText("Click for combo box");
		stringColumn.setCellRenderer(renderer);
	}
	
	

	public void tabella(MyTable tabella, DefaultTableModel t) {
		t.addColumn("First Symbol");
		t.addColumn("Second Symbol");

		tabella.setModel(t);
		frame.getContentPane().setLayout(null);

		JScrollPane ScrollCatalogo = new JScrollPane(tabella);
		tabella.getTableHeader().setBackground(new Color(32,32,32));
		tabella.getTableHeader().setForeground(new Color(220,20,60));
		ScrollCatalogo.getViewport().setBackground(new Color(32,32,32));
		ScrollCatalogo.setBounds(100, 100, 300, 200);
		frame.getContentPane().add(ScrollCatalogo);

	}

	public void addRow(DefaultTableModel t1, JTable t) {
		if(t.getRowCount()>=1 && (t.getValueAt(t.getRowCount()-1,0).equals("")|| t.getValueAt(t.getRowCount()-1,1).equals("")))
		{
			JOptionPane.showMessageDialog(null, "Completare i campi vuoti per aggiungere un'altra riga");
		return;
		}
			String[] emptyRow = { "","" };
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

		if (table.getSelectedRow() != -1){
			
			if (table.isEditing()) {
			    table.getCellEditor().stopCellEditing();
			    t.removeRow(table.getSelectedRow());
			}	
		}
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
