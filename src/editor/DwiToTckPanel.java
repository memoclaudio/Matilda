package editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;

import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;

import jdk.nashorn.internal.runtime.regexp.joni.ast.CClassNode.CCStateArg;

public class DwiToTckPanel {

	private JDialog dialog;
	private int widthDwi = 500;
	private int heightDwi = 500;
	private File dwi, bvecs, bvals = null;
	private String dwiPath, bvecsPath, bvalsPath, fileTck;
	private Integer numberOfTracks;
	private SettingsPanel settingsPanel;
	private int valueBar = 0;
	private boolean stop = false;

	public DwiToTckPanel(JFrame f, DrawingArea drawingAream, int width, int height, SettingsPanel settingsPanel) {

		dialog = new JDialog(f, ModalityType.APPLICATION_MODAL);

		JLabel lblTitle = new JLabel("DwiToTck", SwingConstants.CENTER);
		lblTitle.setForeground(new Color(220, 20, 60));
		lblTitle.setFont(new Font("Kokonor", Font.PLAIN, 30));
		lblTitle.setBounds(160, 10, 472, 80);

		dialog.getContentPane().add(lblTitle);

		LabelTextButtonPanel dwiPanel = new LabelTextButtonPanel("Load dwi", "Load dwi");
		LabelTextButtonPanel bvecsPanel = new LabelTextButtonPanel("Load bvecs", "Load bvecs");
		LabelTextButtonPanel bvalsPanel = new LabelTextButtonPanel("Load bvals", "Load bvals");
		LabelTextFieldPanel tckOutPanel = new LabelTextFieldPanel("Tck file name", 150);
		LabelTextFieldPanel numberOfTracksPanel = new LabelTextFieldPanel("Number of tracks", 40);

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

		bvecsPanel.getJbutton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				JFileChooser chooser = new JFileChooser();

				chooser.setFileFilter(null);

				int returnVal = chooser.showDialog(chooser, "Select bvecs file");

				if (returnVal == JFileChooser.APPROVE_OPTION) {

					bvecs = chooser.getSelectedFile();
					bvecsPath = bvecs.getPath();
					bvecsPanel.getTextField().setText(bvecsPath);

				}
			}
		});

		bvalsPanel.getJbutton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				JFileChooser chooser = new JFileChooser();

				int returnVal = chooser.showDialog(chooser, "Select bvals");

				if (returnVal == JFileChooser.APPROVE_OPTION) {

					bvals = chooser.getSelectedFile();
					bvalsPath = bvals.getPath();
					bvalsPanel.getTextField().setText(bvalsPath);

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

				fileTck = tckOutPanel.getTextField().getText();
				numberOfTracks = Integer.parseInt(numberOfTracksPanel.getTextField().getText());

				String[] s = new String[] { "./script.sh", dwiPath, bvecsPath, bvalsPath,
						Integer.toString(numberOfTracks), fileTck };

				dialog.dispose();

				try {
					ProcessBuilder pb = new ProcessBuilder(s); //
					pb.directory(new File(System.getProperty("user.dir")));
					System.out.println(pb.directory());

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

					BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

					BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

					new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub

							String string = null;

							// read the output from the command
							System.out.println("Here is the standard output of the command:\n");

							try {
								while ((string = stdInput.readLine()) != null) {
									if (string.equals("step1")) {
										SwingUtilities.invokeLater(new Runnable() {
											public void run() {
												pbar.setValue(15);
												pbar.updateUI();
											}
										});

									}

									if (string.equals("step2")) {
										SwingUtilities.invokeLater(new Runnable() {
											public void run() {
												pbar.setValue(50);
												pbar.updateUI();
											}
										});

									}
									if (string.equals("step3")) {
										SwingUtilities.invokeLater(new Runnable() {
											public void run() {
												pbar.setValue(60);
												pbar.updateUI();
											}
										});

									}

									if (string.equals("step4")) {
										SwingUtilities.invokeLater(new Runnable() {
											public void run() {
												pbar.setValue(100);
												pbar.updateUI();
												caricamento.setVisible(false);
											}
										});

									}

								}
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

							// read any errors from the attempted command
							System.out.println("Here is the standard error of the command (if any):\n");
							try {
								while ((string = stdError.readLine()) != null) {
									System.out.println(string);
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					}).start();

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

					caricamento.setVisible(true);

				} catch (IOException e1) { // TODO Auto-generated catch block
					e1.printStackTrace();
				}

				settingsPanel.setFibers(TracksReader.read("output_tck/" + fileTck + ".tck"));

			}

		});

		dialog.getContentPane().setLayout(new GridLayout(6, 1));

		GroupLayout layout = new GroupLayout(dialog.getContentPane());
		dialog.getContentPane().setLayout(layout);
		layout.setVerticalGroup(layout.createSequentialGroup().addComponent(lblTitle).addComponent(dwiPanel)
				.addComponent(bvecsPanel).addComponent(bvalsPanel).addComponent(tckOutPanel)
				.addComponent(numberOfTracksPanel).addComponent(submit));
		layout.setHorizontalGroup(layout.createParallelGroup(Alignment.CENTER).addComponent(lblTitle)
				.addComponent(dwiPanel).addComponent(bvecsPanel).addComponent(bvalsPanel).addComponent(tckOutPanel)
				.addComponent(numberOfTracksPanel).addComponent(submit));

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
