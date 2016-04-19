package jrealityTests;

import de.jreality.geometry.IndexedLineSetFactory;
import de.jreality.geometry.PointSetFactory;
import de.jreality.math.Rn;
import de.jreality.plugin.JRViewer;
import de.jreality.scene.IndexedLineSet;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.data.Attribute;
import de.jreality.scene.data.StorageModel;
import de.jreality.shader.Color;

public class JRealityTest {
	public static boolean stop=false;
 
 public static void main(String[] args) {
	IndexedLineSetFactory psf = new IndexedLineSetFactory();
	 PointSetFactory mp = new PointSetFactory();
	SceneGraphComponent sgc=new SceneGraphComponent(); 
	
   double [][] vertices =new double[100][3];
   double [][] punti={{-3,3,0},{0,-3,0},{3,3,0}};
   int[][] edgeIndices = new int[100][2];
   for(int i=0;i<edgeIndices.length;i++){
	   edgeIndices[i][0]=i;
	   edgeIndices[i][1]=(i+1)%100;
	   
   }
   edgeIndices[99][1]=99;
	
   
   int i=0;
   for(double t=0;  t<=1; t+=0.01,i++){
		double x = (1-t)*(1-t)*punti[0][0] +2*t*(1-t)*punti[1][0] +t*t*punti[2][0];
		double y = (1-t)*(1-t)*punti[0][1] +2*t*(1-t)*punti[1][1] +t*t*punti[2][1];
		double z = (1-t)*(1-t)*punti[0][2] +2*t*(1-t)*punti[1][2] +t*t*punti[2][2];
   vertices[i][0]=x;
   vertices[i][1]=y;
   vertices[i][2]=z;
		   System.out.println("X "+x+"Y "+y+" Z"+z);
	
		   
   
  
   }
   
      //double[][] vertices={{0,0,0},{6,0,0},{-6,0,0},{0,4,0}};
   
   
 
   psf.setVertexCount( vertices.length );
   psf.setVertexCoordinates( vertices );
  psf.setEdgeCount(edgeIndices.length);
   psf.setEdgeIndices(edgeIndices);
   
   psf.update();
   colorVertices(psf.getIndexedLineSet(), new double[] { 0, 0, 1 },new double[] { 0, 1, 0 });
   
   mp.setVertexCount( punti.length );
   mp.setVertexCoordinates( punti );
   Color [] c={Color.RED, Color.RED, Color.RED};
   mp.setVertexColors(c);
   mp.update();
   
   SceneGraphComponent p2=new SceneGraphComponent();
   
   p2.setGeometry(mp.getGeometry());
   
   
   sgc.setGeometry(psf.getGeometry());
   sgc.addChild(p2);
   JRViewer.display(sgc);
 }

 public static Color colore(double x, double y, double z){
	 return new Color((int)(Math.random() * 0x1000000));
 }
 
 public static  void colorVertices(IndexedLineSet ils, double[] color1,
			double[] color2) {
		int nPts = ils.getNumPoints();
		double[][] colors = new double[nPts][3];
		double[][] vertices = ils.getVertexAttributes(Attribute.COORDINATES)
				.toDoubleArrayArray(null);
		for (int i = 1; i < nPts - 1; ++i) {
			double[] v1 = Rn.subtract(null, vertices[i], vertices[i - 1]);
			double t = 10 * Math.sqrt(Math.abs(v1[0] * v1[0] + v1[1] * v1[1]));
			t = t - ((int) t);
			Rn.linearCombination(colors[i], t, color1, 1 - t, color2);
		}
		System.arraycopy(colors[1], 0, colors[0], 0, 3);
		System.arraycopy(colors[nPts - 2], 0, colors[nPts - 1], 0, 3);
		ils.setEdgeAttributes(Attribute.COLORS, StorageModel.DOUBLE_ARRAY
				.array(3).createReadOnly(colors));
	}
 
 
}