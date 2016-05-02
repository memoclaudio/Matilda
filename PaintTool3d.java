import java.util.ArrayList;
import java.util.List;
import de.jreality.geometry.IndexedLineSetFactory;
import de.jreality.geometry.PointSetFactory;
import de.jreality.math.Matrix;
import de.jreality.math.Rn;
import de.jreality.plugin.JRViewer;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.tool.AbstractTool;
import de.jreality.scene.tool.InputSlot;
import de.jreality.scene.tool.ToolContext;
import de.jreality.shader.Color;
import de.jreality.tools.DragEventTool;
import de.jreality.tools.PointDragEvent;
import de.jreality.tools.PointDragListener;
import de.jreality.toolsystem.ToolUtility;

public class PaintTool3d extends AbstractTool {

	// Lista dei punti di controllo del poligono
	static List<double[]> points = new ArrayList<double[]>();

	// Geometria dei punti di controllo
	static PointSetFactory pointSet = new PointSetFactory();

	// Geometria del poligono di Bézier
	static IndexedLineSetFactory polygon = new IndexedLineSetFactory();

	// costruttore nel quale abilitiamo la pressione del tasto "Shift+sinistro"
	// per aggiungere un nuovo punto alla scena
	public PaintTool3d() {
		addCurrentSlot(InputSlot.SHIFT_LEFT_BUTTON, "add a new point");
	}

	@Override
	public void perform(ToolContext tc) {
		if (!tc.getAxisState(InputSlot.SHIFT_LEFT_BUTTON).isPressed())
			return;

		// Matrice di trasformazione usata per rappresentare il punto
		Matrix m = new Matrix(
				tc.getTransformationMatrix(InputSlot.POINTER_TRANSFORMATION));

		// coordinate del punto
		double[] foot = m.getColumn(3);
		double[] dir = m.getColumn(2);
		double[] offset = Rn.times(null, -5, dir);
		double[] newPoint = Rn.add(null, foot, offset);

		// trasformazione delle coordinate del punto nelle coordinate del
		// sistema
		points.add(ToolUtility.worldToLocal(tc, newPoint));

		updatePoints();
		updateGeometry();

	}

	// updatePoints() aggiorna la geometria dei punti di controllo
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

	// updateGeometry() aggiorna la geometria del poligono	
	private static void updateGeometry() {

		int n = points.size();
		if (n >= 2) {
			int dim = 100;
			double[][] vertices = new double[dim][3];
			int i1 = 0;
			double[] tmp;
			for (double t = 0; t <= 1; t += 1.0 / dim, i1++) {
				tmp = getCasteljauPoint(points.size() - 1, 0, t);
				vertices[i1][0] = tmp[0];
				vertices[i1][1] = tmp[1];
				vertices[i1][2] = tmp[2];
			}

			polygon.setVertexCount(vertices.length);
			polygon.setVertexCoordinates(vertices);
			//calcola i bordi
			int[][] idx = new int[dim - 1][2];
			for (int i = 1; i < dim; i++) {
				idx[i - 1][0] = i - 1;
				idx[i - 1][1] = i;
			}
			polygon.setEdgeCount(dim - 1);
			polygon.setEdgeIndices(idx);
			polygon.update();
		}

	}

	//algoritmo di Casteljau
	private static double[] getCasteljauPoint(int r, int i, double t) {
		if (r == 0)
			return points.get(i);
		double[] p1 = getCasteljauPoint(r - 1, i, t);
		double[] p2 = getCasteljauPoint(r - 1, i + 1, t);
		double[] d = { (1 - t) * p1[0] + t * p2[0],
				(1 - t) * p1[1] + t * p2[1], (1 - t) * p1[2] + t * p2[2] };
		return d;
	}

	public static void main(String[] args) {
		PaintTool3d scena = new PaintTool3d();
		SceneGraphComponent cmp = new SceneGraphComponent();
		SceneGraphComponent cmp2 = new SceneGraphComponent();
		SceneGraphComponent cmp3 = new SceneGraphComponent();
		cmp2.setGeometry(scena.polygon.getGeometry());
		cmp3.setGeometry(scena.pointSet.getGeometry());
		cmp.addChild(cmp2);
		cmp.addChild(cmp3);
		//per spostare i punti di controllo e modellare il poligono
		DragEventTool t = new DragEventTool();
		t.addPointDragListener(new PointDragListener() {

			public void pointDragStart(PointDragEvent e) {
			}

			@Override
			public void pointDragEnd(PointDragEvent e) {
			}

			@Override
			public void pointDragged(PointDragEvent e) {
				points.set(e.getIndex(), e.getPosition());
				updatePoints();
				updateGeometry();
			}

		});

		cmp.addTool(t);
		cmp.addTool(scena);
		JRViewer.display(cmp);
	}

}
