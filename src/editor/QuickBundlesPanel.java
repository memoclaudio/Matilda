package editor;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.GroupLayout.Alignment;
import javax.swing.filechooser.FileNameExtensionFilter;

public class QuickBundlesPanel {

	private JDialog dialog;
	private int widthDwi = 500;
	private int heightDwi = 500;
	private File dwi, tck = null;
	private String dwiPath, tckPath;
	private Integer downsampleSize, threshold;
	private SettingsPanel settingsPanel;
	private int valueBar = 0;
	private boolean stop = false;

	public QuickBundlesPanel(JFrame f, DrawingArea drawingAream, int width, int height, SettingsPanel settingsPanel) {

		dialog = new JDialog(f, ModalityType.APPLICATION_MODAL);

		JLabel lblTitle = new JLabel("QuickBundles Clustering", SwingConstants.CENTER);
		lblTitle.setForeground(new Color(220, 20, 60));
		lblTitle.setFont(new Font("Kokonor", Font.PLAIN, 30));
		//lblTitle.setBounds(160, 10, 472, 80);

		dialog.getContentPane().add(lblTitle);

		LabelTextButtonPanel dwiPanel = new LabelTextButtonPanel("Load dwi", "Load dwi");
		LabelTextButtonPanel tckPanel = new LabelTextButtonPanel("Load tck", "Load tck");

		LabelTextFieldPanel thresholdPanel = new LabelTextFieldPanel("Threshold", 40);
		LabelTextFieldPanel downsampleSizePanel = new LabelTextFieldPanel("Downsample size", 40);

		dwiPanel.getJbutton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				JFileChooser chooser = new JFileChooser();

				FileNameExtensionFilter filter = new FileNameExtensionFilter("nii", "nii");
				chooser.setFileFilter(filter);
				int returnVal = chooser.showDialog(chooser, "Select dwi");

				if (returnVal == JFileChooser.APPROVE_OPTION) {

					dwi = chooser.getSelectedFile();
					dwiPath = dwi.getPath();
					dwiPanel.getTextField().setText(dwiPath);

				}

			}
		});

		tckPanel.getJbutton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("tck", "tck");
				chooser.setFileFilter(filter);

				int returnVal = chooser.showDialog(chooser, "Select tck file");

				if (returnVal == JFileChooser.APPROVE_OPTION) {

					tck= chooser.getSelectedFile();
					tckPath = tck.getPath();
					tckPanel.getTextField().setText(tckPath);

				}
			}
		});

		

		JButton submit = new JButton("Submit");

		submit.setCursor(new Cursor(Cursor.HAND_CURSOR));
		submit.setBorderPainted(true);
		submit.setFocusPainted(false);
		submit.setContentAreaFilled(false);
		submit.setSize(new Dimension(100, 80));
		
		submit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				
				int threshold = Integer.parseInt(thresholdPanel.getTextField().getText());
				int downsample = Integer.parseInt(downsampleSizePanel.getTextField().getText());
				
				String tckFolder = tck.getParentFile().getAbsolutePath();
				System.out.println("tckFolder: "+tckFolder);
				String trk=tckPath.substring(0,tckPath.length()-2)+"rk";
				String[] s = new String[] { "./step1.sh", dwiPath, tckFolder};
				String[] s2 = new String[] { "python","step2.py",trk, tckFolder,threshold+"",downsample+"",dwiPath};
				dialog.dispose();

				try {
					ProcessBuilder pb = new ProcessBuilder(s); 
					pb.directory(new File(System.getProperty("user.dir")));
					ProcessBuilder pb2 = new ProcessBuilder(s2); 
					pb2.directory(new File(System.getProperty("user.dir")));
					pb2.redirectErrorStream(true);
				
					
					valueBar = 0;
					JDialog caricamento = new JDialog(f, ModalityType.APPLICATION_MODAL);
					caricamento.setPreferredSize(new Dimension(400, 40));
					caricamento.setResizable(false);
					caricamento.setLocation(width / 2 - 200, height / 2 - 20);
					caricamento.setTitle("Computing...");

					JProgressBar pbar = new JProgressBar(0, 100);
					pbar.setValue(valueBar);
					pbar.setBounds(150, 340, 80, 30);
					pbar.setStringPainted(true);
					pbar.setForeground(new Color(220, 20, 60));
					pbar.setBackground(new Color(32, 32, 32));
					pbar.setBorderPainted(false);

					caricamento.getContentPane().add(pbar);
					caricamento.addWindowListener(new WindowAdapter() {
						public void windowClosing(WindowEvent e) {
							stop = true;

						}
					});

					caricamento.validate();

					pbar.setVisible(true);

					caricamento.pack();
					
					Process p = pb.start();

					//BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

					//BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
					String string = null;

					// read the output from the command
					//System.out.println("Here is the standard output of the command:\n");

					
				//	while ((string = stdError.readLine()) != null) {
					//	System.out.println(string);
					//}
					
					Process p1 = pb2.start();
					
					
					//BufferedReader stdErrorStep2 = new BufferedReader(new InputStreamReader(p1.getErrorStream()));
					
										
					BufferedReader stdInputStep2 = new BufferedReader(new InputStreamReader(p1.getInputStream()));
					
					
					
					
					
					
					
					new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub

							String string = null;

							// read the output from the command
							System.out.println("Here is the standard output of the command:\n");
							int steps=-1;
							try {
								while ((string = stdInputStep2.readLine()) != null) {
									System.out.println(string);
									if(string.matches("\\d+")) {
										if(steps==-1) {
											steps=Integer.parseInt(string);
										}
										else {
											String current = string;
											int stepsFinal = steps;
											SwingUtilities.invokeLater(new Runnable() {
												
												public void run() {
													pbar.setValue((Integer.parseInt(current)+1)*100/stepsFinal);
													pbar.updateUI();
													if(pbar.getValue()==100) {
														caricamento.setVisible(false);
													}
												}
											});
										}
									}
								}
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

						

						}
					}).start();
					/*
					new Thread(new Runnable() {

						@Override
						public void run() {
							while (pbar.getValue() <60) {
								try {
									Thread.sleep(10000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								SwingUtilities.invokeLater(new Runnable() {
									public void run() {

										pbar.setValue(pbar.getValue() + 1);
										System.out.println("pbar" + pbar.getValue());
										pbar.updateUI();
									}
								});

							}
						}
					}).start();
					*/

					caricamento.setVisible(true);

				} catch (IOException e1) { // TODO Auto-generated catch block
					e1.printStackTrace();
				}


			}

		});
		
		dialog.getContentPane().setLayout(new GridLayout(6, 1));

		GroupLayout layout = new GroupLayout(dialog.getContentPane());
		dialog.getContentPane().setLayout(layout);
		layout.setVerticalGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup().addGap(100).addComponent(lblTitle)).addComponent(dwiPanel)
				.addComponent(tckPanel).addComponent(downsampleSizePanel).addComponent(thresholdPanel).addComponent(submit));
		layout.setHorizontalGroup(layout.createParallelGroup(Alignment.CENTER).addGroup(layout.createSequentialGroup().addGap(60).addComponent(lblTitle))
				.addComponent(dwiPanel).addComponent(tckPanel).addComponent(downsampleSizePanel)
				.addComponent(thresholdPanel).addComponent(submit));

		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);

		dialog.setResizable(false);
		dialog.setPreferredSize(new Dimension(widthDwi, heightDwi));
		dialog.setLocation(width / 2 - widthDwi / 2, height / 2 - heightDwi / 2);
		dialog.validate();
		dialog.getContentPane().setBackground(new Color(32, 32, 32));

		dialog.pack();
		dialog.setVisible(true);

	

		}
		
	}
