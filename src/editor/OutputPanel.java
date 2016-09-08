package editor;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import de.jreality.geometry.IndexedLineSetFactory;
import de.jreality.jogl.JOGLViewer;
import de.jreality.math.MatrixBuilder;
import de.jreality.scene.Appearance;
import de.jreality.scene.Camera;
import de.jreality.scene.DirectionalLight;
import de.jreality.scene.Light;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.SceneGraphPath;
import de.jreality.scene.data.Attribute;
import de.jreality.shader.Color;
import de.jreality.shader.CommonAttributes;
import de.jreality.tools.RotateTool;
import de.jreality.toolsystem.ToolSystem;

public class OutputPanel extends Thread implements KeyListener {

	private static Camera camera;
	SceneGraphComponent rootNode;
	SceneGraphPath camPath;
	private SceneGraphComponent cmp;
	private SceneGraphComponent cameraNode;
	private boolean stopRequested = false;
	int xCamPosition = 0;
	int yCamPosition = 0;
	int zCamPosition = 15;
	int step = 2;
private ArrayList<Fiber>fibre;
	public OutputPanel(ArrayList<Fiber> fibre) {
		this.fibre=fibre;
		// INPUT
		cmp = new SceneGraphComponent();
		Appearance a = new Appearance();
		a.setAttribute(CommonAttributes.DIFFUSE_COLOR, new Color(220, 20, 60));
		cmp.setAppearance(a);

		for (int k = 0; k < fibre.size(); k++) {

			int dim = fibre.get(k).getVertex().length;

			IndexedLineSetFactory lsf = new IndexedLineSetFactory();
			lsf.setVertexCount(dim);
			lsf.setVertexCoordinates(fibre.get(k).getVertex());

			double size[] = new double[dim];
			for (int i = 0; i < dim; i++) {
				size[i] = 0.1;
			}

			lsf.setVertexAttribute(Attribute.POINT_SIZE, size);

			int[][] idx = new int[dim - 1][2];
			for (int i = 1; i < dim; i++) {
				idx[i - 1][0] = i - 1;
				idx[i - 1][1] = i;
			}

			lsf.setEdgeCount(dim - 1);
			lsf.setEdgeIndices(idx);
			lsf.update();

			SceneGraphComponent Fiber = new SceneGraphComponent();
			Fiber.setGeometry(lsf.getGeometry());
			cmp.addChild(Fiber);

		}

		rootNode = new SceneGraphComponent();
		cameraNode = new SceneGraphComponent();
		SceneGraphComponent lightNode = new SceneGraphComponent();
		rootNode.addChild(cmp);
		rootNode.addChild(cameraNode);
		cameraNode.addChild(lightNode);
		RotateTool rotateTool = new RotateTool();
		cmp.addTool(rotateTool);
		Light dl = new DirectionalLight();
		lightNode.setLight(dl);
		camera = new Camera();
		MatrixBuilder.euclidean().translate(xCamPosition, yCamPosition, zCamPosition).assignTo(cameraNode);
		Appearance rootApp = new Appearance();
		rootApp.setAttribute(CommonAttributes.BACKGROUND_COLOR, Color.WHITE);
		rootNode.setAppearance(rootApp);

		cameraNode.setCamera(camera);
		// camera.setPerspective(false);

		camPath = new SceneGraphPath();
		camPath.push(rootNode);
		camPath.push(cameraNode);
		camPath.push(camera);

		camera.setFar(4000);

	}

	public void run() {
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		int widthFrame = 600;
		int heightFrame = 600;
		JFrame frame = new JFrame();

		frame.setSize(widthFrame, heightFrame);
		frame.setLocation(width / 2 - widthFrame / 2, height / 2 - heightFrame / 2);

		frame.validate();

		JOGLViewer viewer = new JOGLViewer();
		viewer.setSceneRoot(rootNode);
		viewer.setCameraPath(camPath);
		ToolSystem toolSystem = ToolSystem.toolSystemForViewer(viewer);
		toolSystem.initializeSceneTools();
		JPanel panel = (JPanel) viewer.getViewingComponent();
		// panel.setLayout(null);

		panel.setSize((int) (0.8 * width), height);
		panel.setLocation(width - (int) (0.8 * width), 0);
		frame.add(panel);
		panel.addKeyListener(this);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				stopRequested = true;

			}
		});

		frame.setVisible(true);
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				if (JOptionPane.showConfirmDialog(frame, "Do you want to save tck?", "Save tck to file",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
					JFileChooser chooser = new JFileChooser();
					FileNameExtensionFilter filter = new FileNameExtensionFilter("tck", "tck");
					chooser.setFileFilter(filter);
					chooser.setCurrentDirectory(new File("./"));
					int actionDialog = chooser.showSaveDialog(frame);
					if (actionDialog == JFileChooser.APPROVE_OPTION) {
						File fileName = new File(chooser.getSelectedFile() + ".tck");
						if (fileName.exists()) {
							actionDialog = JOptionPane.showConfirmDialog(frame, "Replace existing file?");
							if (actionDialog == JOptionPane.NO_OPTION)
								return;

						}

						new TckWriter().writeToFile(fileName, fibre);
					}

				}
			}
		});

		while (!stopRequested) {
			viewer.render();

			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {

			}
		}

	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyCode() == KeyEvent.VK_RIGHT) {
			xCamPosition -= step;
		}
		if (arg0.getKeyCode() == KeyEvent.VK_LEFT) {
			xCamPosition += step;

		}
		if (arg0.getKeyCode() == KeyEvent.VK_UP) {
			zCamPosition -= step;

		}
		if (arg0.getKeyCode() == KeyEvent.VK_DOWN) {
			zCamPosition += step;

		}

		MatrixBuilder.euclidean().translate(xCamPosition, yCamPosition, zCamPosition).assignTo(cameraNode);

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
