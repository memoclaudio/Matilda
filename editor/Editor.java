package editor;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
import de.jreality.tools.DragEventTool;
import de.jreality.tools.PointDragEvent;
import de.jreality.tools.PointDragListener;
import de.jreality.tools.RotateTool;
import de.jreality.toolsystem.ToolSystem;
import de.jreality.toolsystem.ToolUtility;

public class Editor extends AbstractTool {

	static int width = Toolkit.getDefaultToolkit().getScreenSize().width;
	static int height = Toolkit.getDefaultToolkit().getScreenSize().height;

	static JTextField textField = new JTextField(20);

	static List<double[]> points = new ArrayList<double[]>();
	static IndexedLineSetFactory lsf = new IndexedLineSetFactory();
	static PointSetFactory pointSet = new PointSetFactory();

	private static Camera camera;
	static DragEventTool dragEventTool;

	private static double[][] vertices;

	public Editor() {
		// INPUT
		addCurrentSlot(InputSlot.SHIFT_LEFT_BUTTON, "add a new point");

		dragEventTool = new DragEventTool();

	}

	public static void draggedPoint() {
		dragEventTool.addPointDragListener(new PointDragListener() {

			@Override
			public void pointDragged(PointDragEvent e) {

				points.set(e.getIndex(), e.getPosition());
				updatePoints();
				updateGeometry();

			}

			@Override
			public void pointDragStart(PointDragEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void pointDragEnd(PointDragEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public void perform(ToolContext tc) {

		if (!tc.getAxisState(InputSlot.SHIFT_LEFT_BUTTON).isPressed())
			return;

		Matrix m = new Matrix(
				tc.getTransformationMatrix(InputSlot.POINTER_TRANSFORMATION));

		double[] foot = m.getColumn(3);
		double[] dir = m.getColumn(2);
		double[] offset = Rn.times(null, -5, dir);
		double[] newPoint = Rn.add(null, foot, offset);

		points.add(ToolUtility.worldToLocal(tc, newPoint));

		updatePoints();
		updateGeometry();

	}

	private static void updatePoints() {

		if (points.size() > 0) {
			pointSet.setVertexCount(points.size());
			pointSet.setVertexCoordinates(points.toArray(new double[0][]));
			Color clr[] = new Color[points.size()];
			for (int i = 0; i < clr.length; i++) {
				clr[i] = Color.RED;
			}
			pointSet.setVertexColors(clr);
			pointSet.update();
		}
	}

	private static void updateGeometry() {

		int n = points.size();

		if (n >= 2) {
			int dim = Integer.parseInt(textField.getText());
			vertices = new double[dim][3];
			int j = 0;
			double[] tmp;
			for (double t = 0; j<dim; t += 1.0 / (dim - 1), j++) {

				tmp = getCasteljauPoint(points.size() - 1, 0, t);
				vertices[j][0] = tmp[0];
				vertices[j][1] = tmp[1];
				vertices[j][2] = tmp[2];
			}

			lsf.setVertexCount(vertices.length);
			lsf.setVertexCoordinates(vertices);

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
		}
	}

	// algoritmo di Casteljau
	private static double[] getCasteljauPoint(int r, int i, double t) {
		if (r == 0)
			return points.get(i);
		double[] p1 = getCasteljauPoint(r - 1, i, t);
		double[] p2 = getCasteljauPoint(r - 1, i + 1, t);
		double[] d = { (1 - t) * p1[0] + t * p2[0],
				(1 - t) * p1[1] + t * p2[1], (1 - t) * p1[2] + t * p2[2] };
		return d;
	}

	private static void clear() {
		points.clear();
		lsf.setVertexCount(0);
		pointSet.setVertexCount(0);
		pointSet.update();
		lsf.update();

	}

	public static void main(String[] args) {

		Editor editor = new Editor();
		SceneGraphComponent cmp = new SceneGraphComponent();
		SceneGraphComponent cmplsf = new SceneGraphComponent();
		SceneGraphComponent cmpPointSet = new SceneGraphComponent();
		cmplsf.setGeometry(editor.lsf.getGeometry());
		cmpPointSet.setGeometry(editor.pointSet.getGeometry());
		cmp.addChild(cmplsf);
		cmp.addChild(cmpPointSet);
		cmp.addTool(editor);
		draggedPoint();
		cmpPointSet.addTool(dragEventTool);

		final SceneGraphComponent axes = AxisFactory.getXYZAxes();

		cmp.addChild(axes);

		SceneGraphComponent rootNode = new SceneGraphComponent();
		SceneGraphComponent cameraNode = new SceneGraphComponent();

		SceneGraphComponent lightNode = new SceneGraphComponent();

		rootNode.addChild(cmp);
		rootNode.addChild(cameraNode);
		cameraNode.addChild(lightNode);

		RotateTool rotateTool = new RotateTool();
		cmp.addTool(rotateTool);

		Light dl = new DirectionalLight();
		lightNode.setLight(dl);

		camera = new Camera();
		cameraNode.setCamera(camera);

		MatrixBuilder.euclidean().translate(0, 0, 3).assignTo(cameraNode);

		Appearance rootApp = new Appearance();
		rootApp.setAttribute(CommonAttributes.BACKGROUND_COLOR, new Color(1f,
				1f, 1f));

		rootNode.setAppearance(rootApp);

		SceneGraphPath camPath = new SceneGraphPath();
		camPath.push(rootNode);
		camPath.push(cameraNode);
		camPath.push(camera);

		// ToolSystemViewer viewer = new ToolSystemViewer(new Viewer());
		JOGLViewer viewer = new JOGLViewer();
		viewer.setSceneRoot(rootNode);
		viewer.setCameraPath(camPath);
		// viewer.initializeTools();
		ToolSystem toolSystem = ToolSystem.toolSystemForViewer(viewer);
		toolSystem.initializeSceneTools();

		// JReality Panel
		JPanel panel = (JPanel) viewer.getViewingComponent();
		// panel.setLayout(null);

		panel.setSize((int) (0.8 * width), height);
		panel.setLocation(width - (int) (0.8 * width), 0);

		// panel di sfondo
		JPanel panel2 = new JPanel();
		panel2.setLayout(null);
		// panel2.setSize(100, 100);

		// reset
		JButton button = new JButton("Reset");
		button.setLocation((int) (0.2 * width) / 2 - 65, (int) height / 6 - 25);
		button.setSize(new Dimension(130, 50));
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				clear();

			}
		});
		panel2.add(button);

		// n
		textField.setText("100");
		textField.setSize(130, 50);
		textField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				updatePoints();
				updateGeometry();

			}
		});

		textField.setLocation((int) (0.2 * width) / 2 - 65,
				(int) (height / 6) * 2 - 25);
		panel2.add(textField);

		// Remove Last Point
		JButton rmvLast = new JButton("Remove Point");
		rmvLast.setLocation((int) (0.2 * width) / 2 - 65,
				(int) (height / 6) * 3 - 25);
		rmvLast.setSize(new Dimension(130, 50));
		rmvLast.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (points.size() != 0)
					points.remove(points.size() - 1);
				if (points.size() == 1)
					clear();
				updatePoints();
				updateGeometry();

			}
		});
		panel2.add(rmvLast);

		// caricaFile button
		JButton carica = new JButton("Upload");
		carica.setLocation((int) (0.2 * width) / 2 - 65,
				(int) (height / 6) * 4 - 25);
		carica.setSize(new Dimension(130, 50));
		panel2.add(carica);

		// Start button
		JButton start = new JButton("Start");
		start.setLocation((int) (0.2 * width) / 2 - 65,
				(int) (height / 6) * 5 - 25);
		start.setSize(new Dimension(130, 50));
		panel2.add(start);

		start.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getLineColorTransformation();

			}
		});
		JLabel n = new JLabel("n");
		n.setSize(50, 50);
		n.setLocation((int) (0.2 * width) / 2 - 80, (int) (height / 6) * 2 - 40);
		n.setToolTipText("Number of polygon points");

		textField.setHorizontalAlignment(JTextField.CENTER);
		button.setToolTipText("Clear the scene");
		rmvLast.setToolTipText("Remove last point added");
		carica.setToolTipText("Upload file");
		start.setToolTipText("Start with research");

		panel2.add(n);

		// costruzione del JFrame

		JFrame frame = new JFrame();
		frame.setSize(width, height);

		frame.add(panel);
		frame.add(panel2);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.validate();
		System.out.println(viewer.getViewingComponentSize());
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
				e.printStackTrace();
			}
		}
	}

	public static void getColorTransformation(int index1, int index2) {

		double succ[] = vertices[index2];
		double prec[] = vertices[index1];
		double delta[] = new double[3];

		for (int i = 0; i < 3; i++) {
			delta[i] = Math.abs(succ[i] - prec[i]);
		}

		double deltaLength = 0;

		for (int i = 0; i < 3; i++) {
			deltaLength += delta[i] * delta[i];
		}

		deltaLength = Math.sqrt(deltaLength);

		for (int i = 0; i < 3; i++) {
			delta[i] /= deltaLength;
		}

		for (int i = 0; i < 3; i++) {
			System.out.print(delta[i] * 255 + " ");
		}
		System.out.println();

	}

	public static void getLineColorTransformation() {
		System.out.println("Colors: ");
		getColorTransformation(0, 1);
		for (int i = 1; i < vertices.length - 1; i++) {
			getColorTransformation(i - 1, i + 1);
		}
		getColorTransformation(vertices.length - 2, vertices.length - 1);
	}

}
