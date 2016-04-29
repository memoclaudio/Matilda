
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
import de.jreality.toolsystem.ToolUtility;

public class PaintTool3D extends AbstractTool {


	List<double[]> points = new ArrayList<double[]>();
	PointSetFactory mp = new PointSetFactory();
	IndexedLineSetFactory lsf = new IndexedLineSetFactory();
	
	double offset = 5;
	
	public PaintTool3D() {
		// two initial points:
		addCurrentSlot(InputSlot.SHIFT_LEFT_BUTTON, "add a new point");
		
		updatePoints();
	}

	@Override
	public void perform(ToolContext tc) {
		if (!tc.getAxisState(InputSlot.SHIFT_LEFT_BUTTON).isPressed()||points.size()==3) return;
		
		// determine the pointer transformation:
		// translation is the mouse pointer on the near clipping plane
		// z-axis is the direction of the mouse ray out of the screen
		// for a 6DOF input device, it is the position/orientation of the device
		Matrix m = new Matrix(tc.getTransformationMatrix(InputSlot.POINTER_TRANSFORMATION));
				
		// we compute the coordinates of the new point in world coordinates
		double[] foot = m.getColumn(3);
		double[] dir = m.getColumn(2);
		double[] offset = Rn.times(null, -5, dir);
		double[] newPoint = Rn.add(null, foot, offset);
		
		// now we transform the world coordinates to the coordinate system of the tool component
		points.add(ToolUtility.worldToLocal(tc, newPoint));
		
		if(points.size()<=3 && points.size()>0){
			updatePoints();
		// set new vertices
		
		} 
		if(points.size()>2)
		updateGeometry();
		
	}
	
	private void updatePoints(){
		if(points.size()>0){
		mp.setVertexCount(points.size());
		mp.setVertexCoordinates(points.toArray(new double[0][]));
		
		
			
		
	Color clr[]=new Color[points.size()];
	for(int i=0;i<clr.length;i++){
		clr[i]=Color.RED;
	}
		   mp.setVertexColors(clr);
		mp.update();}
	}

	private void updateGeometry() {
		
		
		int n=points.size();
		int dim=100;
			double [][] vertices =new double[dim][3];
			
			 int i1=0;
			   for(double t=0;  t<=1; t+=1.0/dim,i1++){
					double x = (1-t)*(1-t)*points.get(0)[0] +2*t*(1-t)*points.get(1)[0] +t*t*points.get(2)[0];
					double y = (1-t)*(1-t)*points.get(0)[1] +2*t*(1-t)*points.get(1)[1] +t*t*points.get(2)[1];
					double z = (1-t)*(1-t)*points.get(0)[2] +2*t*(1-t)*points.get(1)[2] +t*t*points.get(2)[2];
			   vertices[i1][0]=x;
			   vertices[i1][1]=y;
			   vertices[i1][2]=z;
			   }
			
			   lsf.setVertexCount(vertices.length);
			 lsf.setVertexCoordinates( vertices );
			// compute and set new edge indices:
			int[][]  idx = new int[dim-1][2];
			for (int i=1; i<dim; i++) {
				idx[i-1][0] = i-1;
				idx[i-1][1] = i;
			}
	
			lsf.setEdgeCount(dim-1);
			lsf.setEdgeIndices(idx);
				
		lsf.update();
	}
	
	public static void main(String[] args) {

		PaintTool3D example = new PaintTool3D();
		
		SceneGraphComponent cmp = new SceneGraphComponent();
		SceneGraphComponent cmp2 = new SceneGraphComponent();
		SceneGraphComponent cmp3 = new SceneGraphComponent();
		cmp2.setGeometry(example.lsf.getGeometry());
		cmp3.setGeometry(example.mp.getGeometry());
		//cmp.setGeometry(example.lsf.getGeometry());
		cmp.addChild(cmp2);
		cmp.addChild(cmp3);
		
		cmp.addTool(example);
		
		JRViewer.display(cmp);
	}
	
}
