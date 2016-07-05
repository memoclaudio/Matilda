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

	public String constraints="";
	public static void main(String[] args) {
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;

		JFrame frame = new JFrame();
		frame.setSize(width, height);
		frame.validate();


		PaintArea editor = new PaintArea();
		JOGLViewer viewer = new JOGLViewer();
		viewer.setSceneRoot(editor.rootNode);
		viewer.setCameraPath(editor.scene.getCamPath());		// viewer.initializeTools();
		ToolSystem toolSystem = ToolSystem.toolSystemForViewer(viewer);
		toolSystem.initializeSceneTools();
				AxisArea Axes = new AxisArea();

		
		editor.cmp.getTransformation().addTransformationListener(new TransformationListener() {
			@Override
			public void transformationMatrixChanged(TransformationEvent event) {
				double[] matrixCopy = new double[16];
				for(int i=0;i<16;i++) {
					matrixCopy[i] = event.getTransformation().getMatrix()[i];
					if(i%4==3&&i!=15) {
						matrixCopy[i]=0;
					}
				}			
				Axes.axes.setTransformation(new Transformation(matrixCopy));
			}
		});
		// JReality Panel
		JPanel panel = (JPanel) viewer.getViewingComponent();
		
		panel.setSize((int) (0.8 * width), height-100);
		panel.setLocation(width - (int) (0.8 * width), 20);
		panel.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent arg0) {

				editor.setMouseInScreen((arg0.getX() > 10
						&& arg0.getX() < panel.getWidth() - 10
						&& arg0.getY() > 10 && arg0.getY() < panel.getHeight() - 10));

			}

			@Override
			public void mouseMoved(MouseEvent e) {

			}
		});

		OptionPanel panel2 = new OptionPanel(width, height, editor, frame);
		panel2.setLocation(0, 0);

		JOGLViewer viewerAxes = new JOGLViewer();
		viewerAxes.setSceneRoot(Axes.rootNode);
		viewerAxes.setCameraPath(Axes.scene.getCamPath());
		ToolSystem toolSystemAxes = ToolSystem.toolSystemForViewer(viewerAxes);
		toolSystemAxes.initializeSceneTools();
		// pannello assi
		JPanel panelAssi = (JPanel) viewerAxes.getViewingComponent();
		panelAssi.setSize(200, 200);
		panelAssi.setLocation(width - 200, height - 200);

		// costruzione del JFrame

		
		
		frame.add(panelAssi);
		frame.add(panel);

		frame.add(panel2);

		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				System.exit(0);
			}
		});

		while (true) {
			viewer.render();
			viewerAxes.render();
			
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
