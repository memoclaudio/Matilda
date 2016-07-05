package editor;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Dialog.ModalityType;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

public class OptionPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	PaintArea editor;
	private int width;
	private int height;
	JButton button;
	private double[][][] fibre;
private Image resetIcon=null;
private Image nPointsIcon=null;
private Image rmPointIcon=null;
private Image loadIcon=null;
private Image startIcon=null;
private Image nColorsIcon=null;
private Image vincoliIcon=null;
private Image rectangle=null;

int valueBar = 0;
int fetta;
boolean  stop=false;
	public OptionPanel(int w, int h, PaintArea e, JFrame f) {
		width = w;
		height = h;
		editor = e;
		setSize(100, 100);
		setLayout(null);
		setBackground(new Color(32,32,32));	
		UIManager UI=new UIManager();
		 UI.put("OptionPane.background", new Color(220,20,60));
		 UI.put("Panel.background", new Color(220,20,60));	
		
		
		//icons
	try {
		resetIcon=ImageIO.read(new File("src/images/eraser2.png"));
		nPointsIcon=ImageIO.read(new File("src/images/ellipsis-h.png"));
		rmPointIcon=ImageIO.read(new File("src/images/minus.png"));
		loadIcon=ImageIO.read(new File("src/images/cloud-upload-o.png"));
		nColorsIcon=ImageIO.read(new File("src/images/pencil.png"));
				startIcon=ImageIO.read(new File("src/images/start-cog.png"));
				vincoliIcon=ImageIO.read(new File("src/images/vincoli.png"));
				
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
		
		// reset
		Image newimg =resetIcon.getScaledInstance(30,30,
				java.awt.Image.SCALE_SMOOTH);
			//scaled icon
			ImageIcon newIcon = new ImageIcon(newimg);
		button = new JButton();
		button.setIcon(newIcon);
		button.setLocation((int) ((0.1 * width) / 4)+40, (int) height / 7 - 25);
		button.setSize(new Dimension(30, 30));
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.setBorder(null);
		button.setContentAreaFilled(false);
		//button.setBounds(525, 602, 200, 50);
		
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				editor.clear();
				editor.resetTransformation();

			}
		});
		add(button);
		
		//textField.setLocation((int) (0.2 * width) / 2 - 65,(int) (height / 7) * 2 - 25);
		//add(textField);
		
		newimg =nPointsIcon.getScaledInstance(30, 30,
				java.awt.Image.SCALE_SMOOTH);
			//scaled icon
			newIcon = new ImageIcon(newimg);
		JButton n = new JButton();
		n.setIcon(newIcon);
		n.setBorder(null);
		n.setContentAreaFilled(false);
		n.setCursor(new Cursor(Cursor.HAND_CURSOR));
		n.setSize(30, 30);
		n.setLocation((int) ((0.1 * width) / 4)+40, (int) (height / 7) * 2 - 40);
		n.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// n
			JTextField	textField = new JTextField(20);
				textField.setText(Integer.toString(editor.getNumPoints()));
				textField.setSize(130, 50);
				textField.setHorizontalAlignment(JTextField.CENTER);
				
				

			int choice=	JOptionPane.showConfirmDialog(f,
                        textField,
                        "Number of points",
                        JOptionPane.OK_CANCEL_OPTION);
			if(choice==JOptionPane.OK_OPTION){
				try {
					if (Integer.parseInt(textField.getText()) >= 0)
				editor.setNumPoints(Integer.parseInt(textField.getText()));
				 else {
					JOptionPane.showMessageDialog(null,
							"Inserire un intero positivo");
					textField.setText(Integer.toString(editor
							.getNumPoints()));
				}

			} catch (NumberFormatException exc) {
				textField.setText(Integer.toString(editor.getNumPoints()));

				JOptionPane.showMessageDialog(null,
						"Inserire un intero positivo");
			}

			}
				
			}
		});
		add(n);

		// Remove Last Point
		newimg =rmPointIcon.getScaledInstance(30, 30,
				java.awt.Image.SCALE_SMOOTH);
			//scaled icon
		 newIcon = new ImageIcon(newimg);
		JButton rmvLast = new JButton();
		rmvLast.setIcon(newIcon);
		rmvLast.setBorder(null);
		rmvLast.setCursor(new Cursor(Cursor.HAND_CURSOR));
		rmvLast.setContentAreaFilled(false);
		rmvLast.setLocation((int) ((0.1 * width) / 4)*3 +40,
				(int) height / 7 - 25);
		rmvLast.setSize(new Dimension(30, 30));
		rmvLast.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!(editor.pointsSize() == 0)) {
					if (editor.pointsSize() > 1) {
						editor.removePoint();
					} else if (editor.pointsSize() == 1) {
						editor.clear();
					}
					editor.updatePoints();
					editor.updateGeometry();
				}

			}
		});
		add(rmvLast);

		// caricaFile button
		newimg =loadIcon.getScaledInstance(30, 30,
				java.awt.Image.SCALE_SMOOTH);
			//scaled icon
	newIcon = new ImageIcon(newimg);
		JButton carica = new JButton();
		carica.setIcon(newIcon);
		carica.setBorder(null);
		carica.setCursor(new Cursor(Cursor.HAND_CURSOR));
		carica.setContentAreaFilled(false);
		carica.setLocation((int) ((0.1 * width) / 4)*3 +40,
				(int) (height / 7) * 3 - 40);
		carica.setSize(new Dimension(30, 30));
		carica.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();

				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"tck", "tck");
				chooser.setFileFilter(filter);
				int returnVal = chooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					/*
					 * Gestire eccezioni sollevate da read
					 */

					fibre = TracksReader.read(chooser.getSelectedFile()
							.getPath());
					// System.out.println("Fibre size "+fibre.length);

				}

			}
		});
		add(carica);

		// Start button
		newimg =startIcon.getScaledInstance(80, 80,
				java.awt.Image.SCALE_SMOOTH);
			//scaled icon
		newIcon = new ImageIcon(newimg);
		JButton start = new JButton();
		start.setIcon(newIcon);
		start.setBorder(null);
		start.setCursor(new Cursor(Cursor.HAND_CURSOR));
	start.setContentAreaFilled(false);
		start.setLocation((int) (0.2 * width) / 2 - 40,
				(int) (height / 7) * 5 - 40);
		start.setSize(new Dimension(80, 80));
		add(start);

		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!editor.emptyModel()) {
					if (fibre != null) {
						stop=false;
						valueBar=0;
						JDialog caricamento=new JDialog( f,ModalityType.APPLICATION_MODAL);
					caricamento.setPreferredSize(new Dimension(400, 40));
					//caricamento.setUndecorated(true);
					caricamento.setResizable(false);
					caricamento.validate();
				    caricamento.setLocation(width/2-200, height/2-20);
				    caricamento.setTitle("Computing...");
				  
				    JProgressBar pbar = new JProgressBar(0, 100);
					pbar.setValue(valueBar);
					fetta = (int) (fibre.length * 0.02);
					pbar.setBounds(150, 340, 80, 30);
					pbar.setStringPainted(true);
					pbar.setForeground(new Color(220,20,60));
					pbar.setBackground(new Color(32,32,32));
					pbar.setBorderPainted(false);
				

					caricamento.getContentPane().add(pbar);
					caricamento.addWindowListener(new WindowAdapter() {
						  public void windowClosing(WindowEvent e) {
							  stop=true;
							   
							  }
							});
					pbar.setVisible(true);
					
					caricamento.pack();
						
					

				new Thread(new Runnable() {
					public void run() {
						while(!stop){
							
							
								/*
						 * qua devo scandire tutte le fibre del tck, per ognuna
						 * devo creare la stringa corrispondente e lanciare sbed
						 * con quest ultima e la stringa modello,
						 * successivamente salvare il tutto su un file
						 */
				// editor.getLineColorTransformation();
                           
								
						String model = Converter.convertMatrixToString(
							editor.getModelVertex(), editor.getNumColors());
					//pbar.setVisible(true);
					int soglia=6;
					ArrayList<Fibra> fibreResult = null;
					fibreResult = new ArrayList<Fibra>();
					sbed(fibre, model, editor.constraints,soglia, fibreResult,pbar);
					ResultPanel ResultPanel;
					caricamento.setVisible(false);
					if(!stop){
					if(fibreResult.size()!=0){
					//System.out.println(fibreResult.size());
						ResultPanel=new ResultPanel(fibreResult);
					ResultPanel.start();
					}else
						JOptionPane.showMessageDialog(null, "Non è stato trovato alcun risultato");
					}
					
						
						
						stop=true;
						
						

					
					}//fine while
						
					}
				}).start();
				caricamento.setVisible(true);

					} else
						JOptionPane.showMessageDialog(null,
								"Carica un tck per continuare");

				} else
					JOptionPane.showMessageDialog(null,
							"Disegna un modello per continuare");
			
			}			
		});
		
		
        

					
						
											
						
						
						
				
					
					
				
		
		
		
		
		newimg =nColorsIcon.getScaledInstance(30, 30,
				java.awt.Image.SCALE_SMOOTH);
			//scaled icon
		 newIcon = new ImageIcon(newimg);
		JButton nColors = new JButton();
		nColors.setIcon(newIcon);
		nColors.setBorder(null);
		nColors.setContentAreaFilled(false);
		nColors.setSize(30, 30);
		nColors.setCursor(new Cursor(Cursor.HAND_CURSOR));
		nColors.setLocation((int) ((0.1 * width) / 4)*3 +40,
				 (int) (height / 7) * 2 - 40);

		nColors.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// n
				JTextField nColorsField = new JTextField(20);
				nColorsField.setText(Integer.toString(editor.getNumColors()));
				nColorsField.setSize(130, 50);
				nColorsField.setHorizontalAlignment(JTextField.CENTER);
				
				int choice=	JOptionPane.showConfirmDialog(f,
                        nColorsField,
                        "Number of colors",
                        JOptionPane.OK_CANCEL_OPTION);
				
				if(choice==JOptionPane.OK_OPTION){

						try {
							if (Integer.parseInt(nColorsField.getText()) >= 0
									&& Integer.parseInt(nColorsField.getText()) < 10) {
								editor.setNColors(Integer.parseInt(nColorsField
										.getText()));

							} else {
								JOptionPane.showMessageDialog(null,
										"Inserire un numero da 0 a 9");
								nColorsField.setText(Integer.toString(editor
										.getNumColors()));
							}

						} catch (NumberFormatException exc) {
							nColorsField.setText(Integer.toString(editor.getNumColors()));

							JOptionPane.showMessageDialog(null,
									"Inserire un numero da 0 a 9");
						}
					}

				

				//nColorsField.setLocation((int) (0.2 * width) / 2 - 65,(int) (height / 7) * 5 - 25);
				//add(nColorsField);
				
				
			}});
		
		
		
		
		
		
		
		
		add(nColors);
		
		
		
		
		newimg =vincoliIcon.getScaledInstance(30, 30,
				java.awt.Image.SCALE_SMOOTH);
			//scaled icon
		 newIcon = new ImageIcon(newimg);
		JButton vincoliButton = new JButton();
		vincoliButton.setIcon(newIcon);
		vincoliButton.setBorder(null);
		vincoliButton.setContentAreaFilled(false);
		vincoliButton.setSize(30, 30);
		vincoliButton.setLocation((int) ((0.1 * width) / 4) +40,
				 (int) (height / 7) * 3 - 40);
		vincoliButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		vincoliButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
		VincoliPanel v=new VincoliPanel(editor.getNumColors(),fibre, width, height, f, editor);
			
			
			
			}
		});
		
 add(vincoliButton);
		
		
vincoliButton.setToolTipText("Add constraints");
		n.setToolTipText("Number of polygon points");
nColors.setToolTipText("Number of colors used for quantization");
		button.setToolTipText("Clear the scene");
		rmvLast.setToolTipText("Remove last point added");
		carica.setToolTipText("Upload file");
		start.setToolTipText("Start with research");
		
		
				

	}
	public void sbed(double[][][] fibre, String model,String constraints, int soglia, ArrayList<Fibra> fibreResult, JProgressBar pbar) {
		FileOutputStream sbedTmp = null;
		PrintStream scriviSbed=null;
		try {

		//	risultati = new FileOutputStream("src/risultati.txt");
		//	PrintStream scrivi = new PrintStream(risultati);
			// System.out.println("fibre: " + fibre.length);
			for (int i = 0; i < fibre.length; i++) {
				sbedTmp = new FileOutputStream("src/sbedTmp.txt");
				 scriviSbed = new PrintStream(sbedTmp);
			
				if(stop){
				
					break;
				}
				
				scriviSbed.println(model);
				scriviSbed.println(Converter.convertMatrixToString(fibre[i],
						editor.getNumColors()));
				scriviSbed.print(constraints);
		
				String[] s = { "/bin/sh", "-c", "cat src/sbedTmp.txt | ./src/SBED" };
				Process p = Runtime.getRuntime().exec(s);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						p.getInputStream()));
				String line = in.readLine();
				if (line != null){
					
					if(Integer.parseInt(line)<=soglia){
						fibreResult.add(new Fibra(fibre[i]));
					}
					
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

				}

				

			}
			
			if(scriviSbed!=null)
			scriviSbed.close();
			File toDelete = new File("src/sbedTmp.txt");
			if(toDelete.exists())
			toDelete.delete();


		//	pbar.setVisible(false);

		} catch (IOException ex) {
			System.out.println("Errore: " + ex);
			System.exit(1);
		}
	}

	
	 public void paintComponent(Graphics g) {
		    int width = getWidth();
		    int height = getHeight();
		    g.setColor(new Color(32,32,32));
		    g.fillRect(0, 0, width, height);
		    g.setColor(new Color(220,20,60));
		    g.drawRect(50,50, 145,300);
		  }
		
}
