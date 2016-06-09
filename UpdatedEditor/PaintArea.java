import java.awt.Toolkit;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTextField;

import de.jreality.geometry.IndexedLineSetFactory;
import de.jreality.geometry.PointSetFactory;
import de.jreality.geometry.Primitives;
import de.jreality.math.Matrix;
import de.jreality.math.MatrixBuilder;
import de.jreality.math.Rn;
import de.jreality.scene.Appearance;
import de.jreality.scene.Camera;
import de.jreality.scene.DirectionalLight;
import de.jreality.scene.Light;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.SceneGraphPath;
import de.jreality.scene.data.Attribute;
import de.jreality.scene.tool.AbstractTool;
import de.jreality.scene.tool.InputSlot;
import de.jreality.scene.tool.ToolContext;
import de.jreality.shader.Color;
import de.jreality.shader.CommonAttributes;
import de.jreality.shader.ImageData;
import de.jreality.shader.TextureUtility;
import de.jreality.tools.AnimatedRotateTool;
import de.jreality.tools.DragEventTool;
import de.jreality.tools.PointDragEvent;
import de.jreality.tools.PointDragListener;
import de.jreality.tools.RotateTool;
import de.jreality.toolsystem.ToolUtility;
import de.jreality.util.Input;

public class PaintArea extends AbstractTool {

	static JTextField textField = new JTextField(20);

	static List<double[]> points = new ArrayList<double[]>();
	static IndexedLineSetFactory lsf = new IndexedLineSetFactory();
	static PointSetFactory pointSet = new PointSetFactory();
	private static Camera camera;
	static DragEventTool dragEventTool;
	static boolean mouseInScreen;
	static int dim;// num of points vertices;
	static int n; // number of colors for quantization

	static double[][] RGB;

	private static double[][] vertices;
	private static int[][] pixels;
	SceneGraphComponent rootNode;

	SceneGraphPath camPath;
	SceneGraphComponent cmp;
	SceneGraphComponent cameraNode;

	public PaintArea() {
		dim = 100;
		n = 6;
		RGB = new double[dim][3];
		pixels = new int[dim][1];
		// INPUT
		addCurrentSlot(InputSlot.SHIFT_LEFT_BUTTON, "add a new point");

		dragEventTool = new DragEventTool();
		cmp = new SceneGraphComponent();
		SceneGraphComponent cmplsf = new SceneGraphComponent();
		SceneGraphComponent cmpPointSet = new SceneGraphComponent();
		cmplsf.setGeometry(lsf.getGeometry());
		cmpPointSet.setGeometry(pointSet.getGeometry());
		cmp.addChild(cmplsf);
		cmp.addChild(cmpPointSet);
		cmp.addTool(this);
		draggedPoint();
		cmpPointSet.addTool(dragEventTool);

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
		cameraNode.setCamera(camera);

		MatrixBuilder.euclidean().translate(0, 0, 3).assignTo(cameraNode);
		MatrixBuilder.euclidean().assignTo(cmp);

		Appearance rootApp = new Appearance();
		rootApp.setAttribute(CommonAttributes.BACKGROUND_COLOR, new Color(1f,
				1f, 1f));
		double[] defaultPoints = { -10, -10, -5, 10, -10, -5, 10, 10, -5, -10,
				10, -5 };
		rootNode.setGeometry(Primitives.texturedQuadrilateral(defaultPoints));
		try {
			TextureUtility.createTexture(rootApp,
					CommonAttributes.POLYGON_SHADER,
					ImageData.load(Input.getInput("qBUev.jpg")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		rootNode.setAppearance(rootApp);

		camPath = new SceneGraphPath();
		camPath.push(rootNode);
		camPath.push(cameraNode);
		camPath.push(camera);

	}

	public static void draggedPoint() {
		dragEventTool.addPointDragListener(new PointDragListener() {

			@Override
			public void pointDragged(PointDragEvent e) {
				if (!mouseInScreen)
					return;
				points.set(e.getIndex(), e.getPosition());
				updatePoints();
				updateGeometry();

			}

			@Override
			public void pointDragStart(PointDragEvent e) {

			}

			@Override
			public void pointDragEnd(PointDragEvent e) {

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

	public static void updatePoints() {

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

	public int pointsSize() {
		return points.size();
	}

	public void removePoint() {
		points.remove(points.size() - 1);
	}

	public static void updateGeometry() {

		int n = points.size();

		if (n >= 2) {

			vertices = new double[dim][3];
			int j = 0;
			double[] tmp;
			for (double t = 0; j < dim; t += 1.0 / (dim - 1), j++) {

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

	public void setNumPoints(int d) {
		dim = d;
		RGB = new double[dim][3];
		pixels = new int[dim][1];
		updatePoints();
		updateGeometry();

	}

	public void setNColors(int n) {
		this.n = n;
	}

	public int getNumPoints() {
		return dim;
	}

	public int getNumColors() {
		return n;
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

	public static void clear() {
		points.clear();
		lsf.setVertexCount(0);
		pointSet.setVertexCount(0);
		pointSet.update();
		lsf.update();

	}

	public static void getColorTransformation(int index1, int index2, int iRGB) {
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
			// System.out.println(delta[i]*255);
			RGB[iRGB][i] = delta[i] * 255;

		}
		int redC = (int) (Math.round(RGB[iRGB][0])) << 16;
		int greenC = (int) (Math.round(RGB[iRGB][1])) << 8;
		int blueC = (int) (Math.round(RGB[iRGB][2]));
		int pixel = redC + greenC + blueC;
		pixels[iRGB][0] = pixel;

	}

	public static void getLineColorTransformation() {
		if (lsf.getVertexCount() > 0) {

			int iRGB = 0;
			// System.out.println("Colors: ");
			getColorTransformation(0, 1, iRGB);
			iRGB++;
			for (int i = 1; i < vertices.length - 1; i++) {
				getColorTransformation(i - 1, i + 1, iRGB);
				iRGB++;
			}
			getColorTransformation(vertices.length - 2, vertices.length - 1,
					iRGB);
			iRGB++;
			// System.out.println("Sto stampando la matrice RGB2ind");
			// int[][] matrice=rgb.rgb2ind(pixels);
			// for (int i = 0; i < matrice.length; i++){
			// for (int j = 0; j < matrice[0].length; j++) {
			// System.out.print(matrice[i][j]+" ");
			// }
			// System.out.println("");}

			//
			Quantize quantize = new Quantize();

			int[] indexedImage = quantize.quantizeImage(pixels, n);
			System.out.println("pixels after quantize with n=" + n);
			for (int i = 0; i < pixels.length; i++) {
				System.out.println(pixels[i][0]);
			}
			System.out.println("");
		}
	}

	public void setMouseInScreen(boolean v) {
		mouseInScreen = v;
	}
}
