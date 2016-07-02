package editor;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.jreality.geometry.IndexedLineSetFactory;
import de.jreality.geometry.PointSetFactory;
import de.jreality.jogl.JOGLViewer;
import de.jreality.math.Matrix;
import de.jreality.math.MatrixBuilder;
import de.jreality.math.Rn;
import de.jreality.scene.Appearance;
import de.jreality.scene.Camera;
import de.jreality.scene.DirectionalLight;
import de.jreality.scene.Light;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.SceneGraphPath;
import de.jreality.scene.Viewer;
import de.jreality.scene.data.Attribute;
import de.jreality.scene.tool.AbstractTool;
import de.jreality.scene.tool.InputSlot;
import de.jreality.scene.tool.ToolContext;
import de.jreality.shader.Color;
import de.jreality.shader.CommonAttributes;
import de.jreality.tools.AnimatedRotateTool;
import de.jreality.tools.DragEventTool;
import de.jreality.tools.PointDragEvent;
import de.jreality.tools.PointDragListener;
import de.jreality.tools.RotateTool;
import de.jreality.toolsystem.ToolSystem;
import de.jreality.toolsystem.ToolUtility;

public class Editor {

	public static void main(String[] args) {
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;

		JFrame frame = new JFrame();
		frame.setSize(width, height);
		frame.validate();


		PaintArea editor = new PaintArea();
		// ToolSystemViewer viewer = new ToolSystemViewer(new Viewer());
		JOGLViewer viewer = new JOGLViewer();
		viewer.setSceneRoot(editor.rootNode);
		viewer.setCameraPath(editor.camPath);
		// viewer.initializeTools();
		ToolSystem toolSystem = ToolSystem.toolSystemForViewer(viewer);
		toolSystem.initializeSceneTools();
		

		// Component cmp = ((Component) viewer.getViewingComponent());
		// cmp.addKeyListener(new KeyAdapter() {
		// public void keyPressed(KeyEvent input) {
		//
		// switch(input.getKeyCode()) {
		//
		// case KeyEvent.VK_0 :
		// MatrixBuilder m =
		// MatrixBuilder.euclidean(editor.cmp.getTransformation());
		// m.rotate(0.05*0.007, new double[]{0,1,0});
		// m.assignTo(editor.cmp);
		// break;
		// default:
		// }}});
		//

		// JReality Panel
		JPanel panel = (JPanel) viewer.getViewingComponent();
		// panel.setLayout(null);

		panel.setSize((int) (0.8 * width), height);
		panel.setLocation(width - (int) (0.8 * width), 0);
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

		// panel di sfondo con le opzioni
		OptionPanel panel2 = new OptionPanel(width, height, editor, frame);
		panel2.setLocation(0, 0);

		AxisArea Axes = new AxisArea();
		// ToolSystemViewer viewer = new ToolSystemViewer(new Viewer());
		JOGLViewer viewerAxes = new JOGLViewer();
		viewerAxes.setSceneRoot(Axes.rootNode);
		viewerAxes.setCameraPath(Axes.camPath);
		// viewerAxes.initializeTools();
		ToolSystem toolSystemAxes = ToolSystem.toolSystemForViewer(viewerAxes);
		toolSystemAxes.initializeSceneTools();

		// SimultaneousRotationTool rotateTool = new SimultaneousRotationTool();
		// rotateTool.setComponents(editor.cmp,Axes.axes);
		//
		// editor.cmp.addTool(rotateTool);

		// pannello assi
		JPanel panelAssi = (JPanel) viewerAxes.getViewingComponent();
		panelAssi.setSize(300, 300);
		// panelAssi.setBackground(java.awt.Color.black);
		panelAssi.setLocation(width - 300, height - 300);

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
