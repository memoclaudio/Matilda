package editor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.jreality.geometry.IndexedLineSetFactory;
import de.jreality.geometry.PointSetFactory;
import de.jreality.geometry.Primitives;
import de.jreality.math.Matrix;
import de.jreality.math.MatrixBuilder;
import de.jreality.math.Rn;
import de.jreality.scene.Appearance;
import de.jreality.scene.Camera;
import de.jreality.scene.DirectionalLight;
import de.jreality.scene.Geometry;
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
import de.jreality.tools.DragEventTool;
import de.jreality.tools.PointDragEvent;
import de.jreality.tools.PointDragListener;
import de.jreality.tools.RotateTool;
import de.jreality.toolsystem.ToolUtility;
import de.jreality.util.DefaultMatrixSupport;
import de.jreality.util.Input;

public class PaintArea extends AbstractTool {
	
	Appearance rootApp = new Appearance();
	private static Camera camera = new Camera();
	SceneGraphPath camPath = new SceneGraphPath();
	static SceneGraphComponent cameraNode = new SceneGraphComponent();
	private SceneGraphComponent lightNode = new SceneGraphComponent();
	private RotateTool rotateTool = new RotateTool();
	
	private Geometry g;

	Light dl = new DirectionalLight();



	static List<double[]> points = new ArrayList<double[]>();
	static IndexedLineSetFactory lsf = new IndexedLineSetFactory();
	static PointSetFactory pointSet = new PointSetFactory();
	// private static Camera camera;
	static DragEventTool dragEventTool;
	static boolean mouseInScreen;
	private static int dim;// num of points model;
	private static int n; // number of colors for quantization
	private static Fibra model;
	SceneGraphComponent rootNode;
	// SceneGraphPath camPath;
	SceneGraphComponent cmp;
	String constraints = "";

	// private SceneGraphComponent cameraNode;
	SceneGraphComponent axes = new SceneGraphComponent();

	public PaintArea() {

		dim = 30;
		n = 6;
		// INPUT
		addCurrentSlot(InputSlot.SHIFT_LEFT_BUTTON, "add a new point");
		dragEventTool = new DragEventTool();
		rootNode = new SceneGraphComponent();
		

		
		
		cameraNode.setCamera(camera);
		camPath.push(rootNode);
		camPath.push(cameraNode);
		camPath.push(camera);
		lightNode.setLight(dl);
		cameraNode.addChild(lightNode);
		camera.setPerspective(false);

		
		
		rootApp.setAttribute(CommonAttributes.BACKGROUND_COLOR,new Color(40,40,40));
		rootApp.setAttribute(CommonAttributes.DIFFUSE_COLOR,Color.white);
		rootApp.setAttribute("transparencyEnabled", true);
	      rootApp.setAttribute("transparency", 0.7);
//		
		MatrixBuilder.euclidean().translate(0, 0, 3).assignTo(cameraNode);

		double[] defaultPoints = { -4, -4, -4, 4, -4, -4, 4, 4, -4, -4, 4, -4 };

		g = (Primitives.texturedQuadrilateral(defaultPoints));

		try {
			TextureUtility.createTexture(rootApp,CommonAttributes.POLYGON_SHADER,ImageData.load(Input.getInput("images/redGrid.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		cmp = new SceneGraphComponent();
		SceneGraphComponent cmplsf = new SceneGraphComponent();
		SceneGraphComponent cmpPointSet = new SceneGraphComponent();
		cmplsf.setGeometry(lsf.getGeometry());
		cmpPointSet.setGeometry(pointSet.getGeometry());
		cmp.addChild(cmplsf);
		cmp.addChild(cmpPointSet);
		cmp.addTool(this);

		Appearance a = new Appearance();
		a.setAttribute(CommonAttributes.DIFFUSE_COLOR, new Color(220, 20, 60));
		a.setAttribute("transparencyEnabled", false);

		cmp.setAppearance(a);

		draggedPoint();

		cmpPointSet.addTool(dragEventTool);
		
		rootNode.addChild(cmp);
		rootNode.addChild(cameraNode);

		cmp.addTool(rotateTool);
		MatrixBuilder.euclidean().assignTo(cmp);
		rootNode.setAppearance(rootApp);
		rootNode.setGeometry(g);
		DefaultMatrixSupport.getSharedInstance().storeDefaultMatrices(rootNode);

		axes = AxisFactory.getXYZAxes();
		rootNode.addChild(axes);
		rootNode.addChild(cameraNode);
		rootNode.setAppearance(rootApp);

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
			double pdim[]=new double[points.size()];
			Color clr[] = new Color[points.size()];
			for (int i = 0; i < clr.length; i++) {
				clr[i] = Color.BLUE;
				pdim[i]=1.01;
			}
			pointSet.setVertexColors(clr);
			pointSet.setVertexAttribute(Attribute.RELATIVE_RADII,pdim);
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
		if (n == 1) {
			lsf.setVertexCount(0);
			lsf.update();
			lsf.setEdgeCount(0);

		}
		if (n >= 2) {

			model = new Fibra(dim);
			int j = 0;
			double[] tmp;
			for (double t = 0; j < dim; t += 1.0 / (dim - 1), j++) {

				tmp = getCasteljauPoint(points.size() - 1, 0, t);
				model.setPoint(j, tmp[0], tmp[1], tmp[2]);
			}

			lsf.setVertexCount(model.size());
			lsf.setVertexCoordinates(model.getVertex());

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
		updatePoints();
		updateGeometry();

	}

	public void setNColors(int n) {
		this.n = n;
	}

	public String getMatrixView(){
		return DefaultMatrixSupport.getSharedInstance().toString();
	}
	public int getNumPoints() {
		return dim;
	}

	public boolean emptyModel() {
		return lsf.getVertexCount() == 0;
	}

	public int getNumColors() {
		return n;
	}

	public double[][] getModelVertex() {
		return model.getVertex();
	}
	
	public  List<double[]> getPolygonPoints() {
		return points;
	}
	public  int getNPolygonPoints() {
		return points.size();
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

	public void resetTransformation() {
		DefaultMatrixSupport.getSharedInstance().restoreDefaultMatrices(
				rootNode, false);
	}

	public void setMouseInScreen(boolean v) {
		mouseInScreen = v;
	}
}
