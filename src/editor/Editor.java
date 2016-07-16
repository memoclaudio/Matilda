/*This is the main JFrame that contains two panels: the settings panel and the drawing panel
 */
package editor;

import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import de.jreality.jogl.JOGLViewer;
import de.jreality.scene.Transformation;
import de.jreality.scene.event.TransformationEvent;
import de.jreality.scene.event.TransformationListener;
import de.jreality.toolsystem.ToolSystem;

public class Editor {

	public static void main(String[] args) {
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;

		JFrame frame = new JFrame();
		frame.setSize(width, height);
		frame.validate();
		DrawingArea drawingArea = new DrawingArea();
		JOGLViewer viewer = new JOGLViewer();
		viewer.setSceneRoot(drawingArea.getRootNode());
		viewer.setCameraPath(drawingArea.getCamPath()); // viewer.initializeTools();
		ToolSystem toolSystem = ToolSystem.toolSystemForViewer(viewer);
		toolSystem.initializeSceneTools();
		double axesDeltaX = -2.3;
		double axesDeltaY = -1.3;
		double[] matrixCopy = new double[16];
		for (int i = 0; i < 16; i++) {

			matrixCopy[i] = 0;
			if (i == 3) {
				matrixCopy[i] = axesDeltaX;
			}
			if (i == 7) {
				matrixCopy[i] = axesDeltaY;
			}
			if (i % 5 == 0) {
				matrixCopy[i] = 1;
			}
		}
		drawingArea.getAxes().setTransformation(new Transformation(matrixCopy));

		drawingArea.getSpline().getTransformation()
				.addTransformationListener(new TransformationListener() {
					@Override
					public void transformationMatrixChanged(
							TransformationEvent event) {
						double[] matrixCopy = new double[16];
						for (int i = 0; i < 16; i++) {
							matrixCopy[i] = event.getTransformation()
									.getMatrix()[i];
							if (i == 3) {
								matrixCopy[i] = axesDeltaX;
							}
							if (i == 7) {
								matrixCopy[i] = axesDeltaY;
							}
						}
						drawingArea.getAxes().setTransformation(
								new Transformation(matrixCopy));
					}
				});
		// JReality Panel
		JPanel drawingPanel = (JPanel) viewer.getViewingComponent();
		drawingPanel.setSize((int) (0.8 * width), height - 100);
		drawingPanel.setLocation(width - (int) (0.8 * width), 20);
		drawingPanel.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent arg0) {

				drawingArea.setMouseInScreen((arg0.getX() > 10
						&& arg0.getX() < drawingPanel.getWidth() - 10
						&& arg0.getY() > 10 && arg0.getY() < drawingPanel
						.getHeight() - 10));

			}

			@Override
			public void mouseMoved(MouseEvent e) {

			}
		});

		SettingsPanel settingsPanel = new SettingsPanel(width, height,
				drawingArea, frame);
		settingsPanel.setLocation(0, 0);
		frame.getContentPane().add(drawingPanel);
		frame.getContentPane().add(settingsPanel);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				System.exit(0);
			}
		});

		while (true) {
			viewer.render();
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				
			}
		}
	}

}
