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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;

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
import javax.swing.table.DefaultTableModel;

import de.jreality.ui.viewerapp.FileFilter;

public class SettingsPanel extends JPanel {
	private static String OS = System.getProperty("os.name").toLowerCase();
	private static final long serialVersionUID = 1L;
	private DrawingArea drawingArea;
	private int width;
	private int height;
	private double[][][] fibers;

	private int soglia = 30;
	private int valueBar = 0;
	private int fetta;
	private boolean stop = false;
	private DefaultTableModel model;

	public SettingsPanel(int w, int h, DrawingArea e, JFrame f) {

		// Jtable's model
		model = new DefaultTableModel();
		model.addColumn("First Symbol");
		model.addColumn("Second Symbol");

		width = w;
		height = h;
		drawingArea = e;
		setSize(100, 100);
		setLayout(null);
		setBackground(new Color(32, 32, 32));
		UIManager.put("OptionPane.background", new Color(220, 20, 60));
		UIManager.put("Panel.background", new Color(220, 20, 60));
		UIManager.put("Button.background", new Color(32, 32, 32));
		UIManager.put("Button.foreground", new Color(220, 20, 60));

		// icons
		Image resetIcon = null;
		Image nPointsIcon = null;
		Image rmPointIcon = null;
		Image loadIcon = null;
		Image startIcon = null;
		Image nColorsIcon = null;
		Image vincoliIcon = null;
		Image infoIcon = null;
		Image resetCamIcon = null;
		Image sogliaIcon = null;
		Image saveModelIcon = null;
		Image uploadModelIcon = null;
		Image uploadDwi = null;
		try {
			resetIcon = ImageIO.read(new File("src/images/eraser2.png"));
			nPointsIcon = ImageIO.read(new File("src/images/ellipsis-h.png"));
			rmPointIcon = ImageIO.read(new File("src/images/minus.png"));
			loadIcon = ImageIO.read(new File("src/images/cloud-upload-o.png"));
			nColorsIcon = ImageIO.read(new File("src/images/pencil.png"));
			startIcon = ImageIO.read(new File("src/images/start-cog.png"));
			vincoliIcon = ImageIO.read(new File("src/images/vincoli.png"));
			infoIcon = ImageIO.read(new File("src/images/info.png"));
			resetCamIcon = ImageIO.read(new File("src/images/eye.png"));
			sogliaIcon = ImageIO.read(new File("src/images/soglia.png"));
			saveModelIcon = ImageIO.read(new File("src/images/save.png"));
			uploadModelIcon = ImageIO.read(new File("src/images/upload.png"));
			uploadDwi = ImageIO.read(new File("src/images/uploadDwi.png"));

		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null, "Error loading image");
		}

		// reset
		Image newimg = resetIcon.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
		// scaled icon
		ImageIcon newIcon = new ImageIcon(newimg);
		JButton resetButton = new JButton();
		resetButton.setIcon(newIcon);
		resetButton.setLocation((int) ((0.1 * width) / 4) + 40, (int) height / 7 - 25);
		resetButton.setSize(new Dimension(30, 30));
		resetButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		resetButton.setBorder(null);
		resetButton.setContentAreaFilled(false);
		resetButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DrawingArea.clear();
				drawingArea.resetTransformation();

			}
		});
		add(resetButton);

		newimg = nPointsIcon.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
		newIcon = new ImageIcon(newimg);
		JButton n = new JButton();
		n.setIcon(newIcon);
		n.setBorder(null);
		n.setContentAreaFilled(false);
		n.setCursor(new Cursor(Cursor.HAND_CURSOR));
		n.setSize(30, 30);
		n.setLocation((int) ((0.1 * width) / 4) + 40, (int) (height / 7) * 2 - 40);
		n.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// n
				JTextField textField = new JTextField(20);
				textField.setText(Integer.toString(drawingArea.getNumPoints()));
				textField.setSize(130, 50);
				textField.setHorizontalAlignment(JTextField.CENTER);

				int choice = JOptionPane.showConfirmDialog(f, textField, "Number of points",
						JOptionPane.OK_CANCEL_OPTION);
				if (choice == JOptionPane.OK_OPTION) {
					try {
						if (Integer.parseInt(textField.getText()) >= 0)
							drawingArea.setNumPoints(Integer.parseInt(textField.getText()));
						else {
							JOptionPane.showMessageDialog(null, "Enter a positive number");
							textField.setText(Integer.toString(drawingArea.getNumPoints()));
						}

					} catch (NumberFormatException exc) {
						textField.setText(Integer.toString(drawingArea.getNumPoints()));

						JOptionPane.showMessageDialog(null, "Enter a positive number");
					}

				}

			}
		});
		add(n);

		// Remove Last Point
		newimg = rmPointIcon.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
		newIcon = new ImageIcon(newimg);
		JButton rmvLast = new JButton();
		rmvLast.setIcon(newIcon);
		rmvLast.setBorder(null);
		rmvLast.setCursor(new Cursor(Cursor.HAND_CURSOR));
		rmvLast.setContentAreaFilled(false);
		rmvLast.setLocation((int) ((0.1 * width) / 4) * 3 + 40, (int) height / 7 - 25);
		rmvLast.setSize(new Dimension(30, 30));
		rmvLast.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!(drawingArea.pointsSize() == 0)) {
					if (drawingArea.pointsSize() > 1) {
						drawingArea.removePoint();
					} else if (drawingArea.pointsSize() == 1) {
						DrawingArea.clear();
					}
					DrawingArea.updatePoints();
					DrawingArea.updateGeometry();
				}

			}
		});
		add(rmvLast);

		// loadFile button
		newimg = loadIcon.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
		newIcon = new ImageIcon(newimg);
		JButton uploadTck = new JButton();
		uploadTck.setIcon(newIcon);
		uploadTck.setBorder(null);
		uploadTck.setCursor(new Cursor(Cursor.HAND_CURSOR));
		uploadTck.setContentAreaFilled(false);
		uploadTck.setLocation((int) ((0.1 * width) / 4) * 3 + 40, (int) (height / 7) * 3 - 40);
		uploadTck.setSize(new Dimension(30, 30));
		uploadTck.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();

				FileNameExtensionFilter filter = new FileNameExtensionFilter("tck", "tck");
				chooser.setFileFilter(filter);
				int returnVal = chooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {

					fibers = TracksReader.read(chooser.getSelectedFile().getPath());

				}

			}
		});
		add(uploadTck);

		newimg = uploadDwi.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
		newIcon = new ImageIcon(newimg);
		JButton dwi = new JButton();
		dwi.setIcon(newIcon);
		dwi.setBorderPainted(false);
		dwi.setCursor(new Cursor(Cursor.HAND_CURSOR));
		dwi.setContentAreaFilled(false);
		dwi.setLocation((int) ((0.1 * width) / 4) + 40, (int) (height / 7) * 3 - 40);
		dwi.setSize(new Dimension(30, 30));
		dwi.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				

				File dwi = null;
				File bvecs = null;
				File bvals = null;

				String path = null;

				/*
				JFileChooser chooser = new JFileChooser();

				FileNameExtensionFilter filter = new FileNameExtensionFilter("nii", "nii");
				chooser.setFileFilter(filter);
				int returnVal = chooser.showDialog(SettingsPanel.this, "Select dwi");

				if (returnVal == JFileChooser.APPROVE_OPTION) {

					dwi = chooser.getSelectedFile();

				}

				chooser.setFileFilter(null);

				returnVal = chooser.showDialog(SettingsPanel.this, "Select bvecs file");

				if (returnVal == JFileChooser.APPROVE_OPTION) {

					bvecs = chooser.getSelectedFile();

				}

				returnVal = chooser.showDialog(SettingsPanel.this, "Select bvals file");

				if (returnVal == JFileChooser.APPROVE_OPTION) {

					bvals = chooser.getSelectedFile();

				}

				*/
				//String[] s = new String[]{"/bin/sh","-c","./dwiScript.pl"};//dwi.getPath()+" "+bvecs.getPath()+" "+bvals.getAbsolutePath()};
				//
				//String[] s = new String[]{"/home/manu/mrtrix3/scripts/dwi2response", "tournier", "dwi.nii", "response.txt", "-fslgrad", "bvecs", "bvals", "-force"};
				String[] s = new String[]{"make"};
				//System.out.println(s[2]+" "+ s[3]+" "+ s[4]);
				try {
					ProcessBuilder pb = new ProcessBuilder(s);
					//pb.directory(new File(System.getProperty("user.dir")));
					System.out.println(pb.directory());
					Process p = pb.start();

					BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

					BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

					// read the output from the command
					System.out.println("Here is the standard output of the command:\n");
					String string = null;
					while ((string = stdInput.readLine()) != null) {
						System.out.println(string);
					}

					// read any errors from the attempted command
					System.out.println("Here is the standard error of the command (if any):\n");
					while ((string = stdError.readLine()) != null) {
						System.out.println(string);
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		add(dwi);

		// Start button
		newimg = startIcon.getScaledInstance(80, 80, java.awt.Image.SCALE_SMOOTH);
		newIcon = new ImageIcon(newimg);
		JButton start = new JButton();
		start.setIcon(newIcon);
		start.setBorder(null);
		start.setCursor(new Cursor(Cursor.HAND_CURSOR));
		start.setContentAreaFilled(false);
		start.setLocation((int) (0.2 * width) / 2 - 45, (int) (height / 7) * 6 - 55);
		start.setSize(new Dimension(80, 80));
		add(start);

		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!drawingArea.emptyModel()) {
					if (fibers != null) {
						stop = false;
						valueBar = 0;
						JDialog caricamento = new JDialog(f, ModalityType.APPLICATION_MODAL);
						caricamento.setPreferredSize(new Dimension(400, 40));
						caricamento.setResizable(false);
						caricamento.setLocation(width / 2 - 200, height / 2 - 20);
						caricamento.setTitle("Computing...");

						JProgressBar pbar = new JProgressBar(0, 100);
						pbar.setValue(valueBar);
						fetta = (int) (fibers.length * 0.02);
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

						new Thread(new Runnable() {
							public void run() {
								while (!stop) {

									String model = Converter.convertMatrixToString(drawingArea.getModelVertex(),
											drawingArea.getNumColors());
									ArrayList<Fiber> fibersResult = null;
									fibersResult = new ArrayList<Fiber>();
									FileOutputStream sbedTmp = null;
									PrintStream scriviSbed = null;

									try {
										for (int i = 0; i < fibers.length; i++) {
											sbedTmp = new FileOutputStream("src/sbedTmp.txt");
											scriviSbed = new PrintStream(sbedTmp);

											if (stop)
												break;

											scriviSbed.println(model);
											scriviSbed.println(Converter.convertMatrixToString(fibers[i],
													drawingArea.getNumColors()));
											scriviSbed.print(drawingArea.getConstraints());

											String[] s = new String[3];
											if (isUnix()) {
												s[0] = "/bin/sh";
												s[1] = "-c";
												s[2] = "cat src/sbedTmp.txt | ./src/SBED";
											} else if (isWindows()) {
												s[0] = "cmd.exe";
												s[1] = "/c";
												s[2] = "type src\\sbedTmp.txt | c:src\\SBEDWin.exe";
											}
											Process p = Runtime.getRuntime().exec(s);
											BufferedReader in = new BufferedReader(
													new InputStreamReader(p.getInputStream()));
											String line = in.readLine();
											if (line != null) {

												if (Integer.parseInt(line) <= soglia) {
													fibersResult.add(new Fiber(fibers[i]));
												}

											}

											if (i == fetta) {

												fetta += (int) (fibers.length * 0.02);

												valueBar += 2;
												SwingUtilities.invokeLater(new Runnable() {
													public void run() {
														pbar.setValue(valueBar);
														pbar.updateUI();
													}
												});

											}

										}

										if (scriviSbed != null)
											scriviSbed.close();
										File toDelete = new File("src/sbedTmp.txt");
										if (toDelete.exists())
											toDelete.delete();
									} catch (IOException ex) {
										JOptionPane.showMessageDialog(null, "Error");
									}
									OutputPanel outputPanel;
									caricamento.setVisible(false);
									if (!stop) {
										if (fibersResult.size() != 0) {
											outputPanel = new OutputPanel(fibersResult);
											outputPanel.start();
										} else
											JOptionPane.showMessageDialog(null, "No result");
									}

									stop = true;

								}
							}
						}).start();
						caricamento.setVisible(true);

					} else
						JOptionPane.showMessageDialog(null, "Upload a tractography to continue");

				} else
					JOptionPane.showMessageDialog(null, "Draw or upload a model to continue");

			}
		});

		newimg = infoIcon.getScaledInstance(15, 15, java.awt.Image.SCALE_SMOOTH);
		newIcon = new ImageIcon(newimg);

		JButton infoButton = new JButton();
		infoButton.setIcon(newIcon);
		infoButton.setBorder(null);
		infoButton.setContentAreaFilled(false);
		infoButton.setSize(15, 15);
		infoButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		infoButton.setLocation((int) ((0.1 * width) / 4) * 2 + 100, (int) (height / 7) * 6 - 10);
		infoButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JDialog frame = new JDialog(f, ModalityType.APPLICATION_MODAL);
				int heightPanel = 500;
				int widthPanel = 500;
				frame.setPreferredSize(new Dimension(widthPanel, heightPanel));
				frame.validate();
				frame.setLocation(width / 2 - widthPanel / 2, height / 2 - heightPanel / 2);
				frame.setLayout(null);
				frame.getContentPane().setBackground(new Color(32, 32, 32));

				JLabel title = new JLabel("Three steps:");
				JLabel instruction1 = new JLabel("a- Draw a model");
				JLabel instruction2 = new JLabel("b- Upload a file.tck");
				JLabel instruction3 = new JLabel("c- Define constraints (optional)");
				JLabel start = new JLabel("...and start");

				title.setForeground(new Color(220, 20, 60));
				title.setFont(new Font("Kokonor", Font.PLAIN, 40));
				title.setBounds(160, 10, 300, 80);
				title.setLocation(130, 20);
				frame.getContentPane().add(title);

				instruction1.setForeground(new Color(220, 20, 60));
				instruction1.setFont(new Font("Kokonor", Font.PLAIN, 25));
				instruction1.setBounds(160, 10, 472, 80);
				instruction1.setLocation(30, 120);

				frame.getContentPane().add(instruction1);

				instruction2.setForeground(new Color(220, 20, 60));
				instruction2.setFont(new Font("Kokonor", Font.PLAIN, 25));
				instruction2.setBounds(160, 10, 472, 80);
				instruction2.setLocation(30, 180);

				frame.getContentPane().add(instruction2);

				instruction3.setForeground(new Color(220, 20, 60));
				instruction3.setFont(new Font("Kokonor", Font.PLAIN, 25));
				instruction3.setBounds(160, 10, 472, 80);
				instruction3.setLocation(30, 240);
				frame.getContentPane().add(instruction3);

				start.setForeground(new Color(220, 20, 60));
				start.setFont(new Font("Kokonor", Font.PLAIN, 30));
				start.setBounds(160, 10, 472, 80);
				start.setLocation(300, 400);

				frame.getContentPane().add(start);

				frame.pack();

				frame.setVisible(true);
			}
		});
		add(infoButton);

		newimg = sogliaIcon.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
		newIcon = new ImageIcon(newimg);

		JButton changeThreshold = new JButton();
		changeThreshold.setIcon(newIcon);
		changeThreshold.setBorder(null);
		changeThreshold.setContentAreaFilled(false);
		changeThreshold.setSize(30, 30);
		changeThreshold.setCursor(new Cursor(Cursor.HAND_CURSOR));
		changeThreshold.setLocation((int) ((0.1 * width) / 4) + 40, (int) (height / 7) * 4 - 40);

		changeThreshold.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JTextField textField = new JTextField(20);
				textField.setText(Integer.toString(soglia));
				textField.setSize(130, 50);
				textField.setHorizontalAlignment(JTextField.CENTER);

				int choice = JOptionPane.showConfirmDialog(f, textField, "Value of threshold",
						JOptionPane.OK_CANCEL_OPTION);
				if (choice == JOptionPane.OK_OPTION) {
					try {
						if (Integer.parseInt(textField.getText()) >= 0)
							soglia = Integer.parseInt(textField.getText());
						else {
							JOptionPane.showMessageDialog(null, "Enter a positive number");
							textField.setText(Integer.toString(soglia));
						}

					} catch (NumberFormatException exc) {
						textField.setText(Integer.toString(soglia));

						JOptionPane.showMessageDialog(null, "Enter a positive number");
					}

				}

			}
		});
		add(changeThreshold);

		newimg = resetCamIcon.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
		newIcon = new ImageIcon(newimg);

		JButton resetCamera = new JButton();
		resetCamera.setIcon(newIcon);
		resetCamera.setBorder(null);
		resetCamera.setContentAreaFilled(false);
		resetCamera.setSize(30, 30);
		resetCamera.setCursor(new Cursor(Cursor.HAND_CURSOR));
		resetCamera.setLocation((int) ((0.1 * width) / 4) * 3 + 40, (int) (height / 7) * 4 - 40);

		resetCamera.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				drawingArea.resetTransformation();
			}
		});
		add(resetCamera);

		newimg = nColorsIcon.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
		newIcon = new ImageIcon(newimg);
		JButton nColors = new JButton();
		nColors.setIcon(newIcon);
		nColors.setBorder(null);
		nColors.setContentAreaFilled(false);
		nColors.setSize(30, 30);
		nColors.setCursor(new Cursor(Cursor.HAND_CURSOR));
		nColors.setLocation((int) ((0.1 * width) / 4) * 3 + 40, (int) (height / 7) * 2 - 40);

		nColors.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JTextField nColorsField = new JTextField(20);
				nColorsField.setText(Integer.toString(drawingArea.getNumColors()));
				nColorsField.setSize(130, 50);
				nColorsField.setHorizontalAlignment(JTextField.CENTER);

				int choice = JOptionPane.showConfirmDialog(f, nColorsField, "Number of colors",
						JOptionPane.OK_CANCEL_OPTION);

				if (choice == JOptionPane.OK_OPTION) {

					try {
						if (Integer.parseInt(nColorsField.getText()) >= 0
								&& Integer.parseInt(nColorsField.getText()) < 10) {
							drawingArea.setNColors(Integer.parseInt(nColorsField.getText()));

						} else {
							JOptionPane.showMessageDialog(null, "Enter a number from 0 to 9");
							nColorsField.setText(Integer.toString(drawingArea.getNumColors()));
						}

					} catch (NumberFormatException exc) {
						nColorsField.setText(Integer.toString(drawingArea.getNumColors()));

						JOptionPane.showMessageDialog(null, "Enter a number from 0 to 9");
					}
				}

			}
		});

		add(nColors);

		newimg = vincoliIcon.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
		newIcon = new ImageIcon(newimg);
		JButton vincoliButton = new JButton();
		vincoliButton.setIcon(newIcon);
		vincoliButton.setBorder(null);
		vincoliButton.setContentAreaFilled(false);
		vincoliButton.setSize(30, 30);
		vincoliButton.setLocation((int) ((0.1 * width) / 4) + 70, (int) (height / 7) * 3 + 10);
		vincoliButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		vincoliButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new ConstraintsPanel(drawingArea.getNumColors(), fibers, width, height, f, drawingArea, model);

			}
		});

		add(vincoliButton);

		newimg = saveModelIcon.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
		newIcon = new ImageIcon(newimg);
		JButton saveModelButton = new JButton();
		saveModelButton.setIcon(newIcon);
		saveModelButton.setBorder(null);
		saveModelButton.setContentAreaFilled(false);
		saveModelButton.setSize(30, 30);
		saveModelButton.setLocation((int) ((0.1 * width) / 4) + 40, (int) (height / 7) * 5 - 40);
		saveModelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		saveModelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				if (drawingArea.getNPolygonPoints() > 0) {
					FileNameExtensionFilter filter = new FileNameExtensionFilter("txt", "txt");
					chooser.setFileFilter(filter);
					chooser.setCurrentDirectory(new File("./"));
					int actionDialog = chooser.showSaveDialog(f);
					if (actionDialog == JFileChooser.APPROVE_OPTION) {
						File fileName = new File(chooser.getSelectedFile() + ".txt");
						if (fileName.exists()) {
							actionDialog = JOptionPane.showConfirmDialog(f, "Replace existing file?");
							if (actionDialog == JOptionPane.NO_OPTION)
								return;

						}
						BufferedWriter outFile = null;
						try {
							outFile = new BufferedWriter(new FileWriter(fileName));
							Iterator<double[]> it = drawingArea.getPolygonPoints().iterator();
							while (it.hasNext()) {
								double[] vertice = it.next();
								outFile.write(vertice[0] + " " + vertice[1] + " " + vertice[2] + "\n");
							}

							outFile.flush();
							outFile.close();

						} catch (IOException e1) {
							JOptionPane.showMessageDialog(null, "Unable to save model");
						}

					}

				} else
					JOptionPane.showMessageDialog(null, "Unable to save: empty model");
			}
		});

		add(saveModelButton);

		newimg = uploadModelIcon.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
		newIcon = new ImageIcon(newimg);
		JButton uploadModelButton = new JButton();
		uploadModelButton.setIcon(newIcon);
		uploadModelButton.setBorder(null);
		uploadModelButton.setContentAreaFilled(false);
		uploadModelButton.setSize(30, 30);
		uploadModelButton.setLocation((int) ((0.1 * width) / 4) * 3 + 40, (int) (height / 7) * 5 - 40);
		uploadModelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		uploadModelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();

				FileNameExtensionFilter filter = new FileNameExtensionFilter("txt", "txt");
				chooser.setFileFilter(filter);
				int returnVal = chooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					BufferedReader br = null;

					try {

						String sCurrentLine;

						br = new BufferedReader(new FileReader(chooser.getSelectedFile().getPath()));
						DrawingArea.clear();
						while ((sCurrentLine = br.readLine()) != null) {
							String[] var = sCurrentLine.split(" ");
							double[] point = new double[3];
							for (int i = 0; i < point.length; i++) {
								point[i] = Double.parseDouble(var[i]);

							}
							drawingArea.getPolygonPoints().add(point);
						}

						drawingArea.resetTransformation();
						DrawingArea.updatePoints();
						DrawingArea.updateGeometry();

					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null, "Failed to load the model");
					} finally {
						try {
							if (br != null)
								br.close();
						} catch (IOException ex) {
							JOptionPane.showMessageDialog(null, "Error closing file");
						}
					}

				}

			}
		});

		add(uploadModelButton);

		vincoliButton.setToolTipText("Add constraints");
		n.setToolTipText("Number of polygon points");
		nColors.setToolTipText("Number of colors used for quantization");
		resetButton.setToolTipText("Clear the scene");
		rmvLast.setToolTipText("Remove last point added");
		uploadTck.setToolTipText("Upload file");
		start.setToolTipText("Start with research");
		resetCamera.setToolTipText("Reset the angle of view");
		infoButton.setToolTipText("Help");
		changeThreshold.setToolTipText("Change value of threshold");
		saveModelButton.setToolTipText("Save model");
		uploadModelButton.setToolTipText("Upload a model");
		dwi.setToolTipText("Upload Dwi");

	}

	private static boolean isWindows() {
		return (OS.indexOf("win") >= 0);
	}

	@SuppressWarnings("unused")
	private static boolean isMac() {
		return (OS.indexOf("mac") >= 0);
	}

	private static boolean isUnix() {
		return (OS.indexOf("nux") >= 0);
	}

	public void paintComponent(Graphics g) {
		int width = getWidth();
		int height = getHeight();
		g.setColor(new Color(32, 32, 32));
		g.fillRect(0, 0, width, height);
		g.setColor(new Color(220, 20, 60));
		g.drawRect(50, 50, 145, 500);
	}
}
