package editor;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import de.jreality.geometry.IndexedLineSetFactory;
import de.jreality.jogl.JOGLViewer;
import de.jreality.math.MatrixBuilder;
import de.jreality.scene.Appearance;
import de.jreality.scene.Camera;
import de.jreality.scene.DirectionalLight;
import de.jreality.scene.Light;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.SceneGraphPath;
import de.jreality.scene.Transformation;
import de.jreality.scene.data.Attribute;
import de.jreality.shader.Color;
import de.jreality.shader.CommonAttributes;
import de.jreality.tools.RotateTool;
import de.jreality.toolsystem.ToolSystem;

public class ResultPanel2 extends Thread{

	Scene scene = Scene.getInstance();

	SceneGraphComponent rootNode;

	SceneGraphComponent fibers = new SceneGraphComponent();


private  boolean stopRequested = false;



	public ResultPanel2(ArrayList<Fibra> fibre) {
		
		
		// INPUT
		fibers = new SceneGraphComponent();
		Appearance a = new Appearance();
		a.setAttribute(CommonAttributes.DIFFUSE_COLOR, new Color(220,20,60));
		fibers.setAppearance(a);
		
		for(int k=0; k<fibre.size();k++){
			
			int dim=fibre.get(k).getVertex().length;
			
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
		
			SceneGraphComponent fibra=new SceneGraphComponent();
			fibra.setGeometry(lsf.getGeometry());
			fibers.addChild(fibra);
		
		}
			
		double axesDeltaZ = 4;
		double[] matrixCopy = new double[16];
		for (int i = 0; i < 16; i++) {
			
			matrixCopy[i] = 0;
		
			if (i == 11) {
				matrixCopy[i] = axesDeltaZ;
			}
			if(i%5==0) {
				matrixCopy[i] = 1;
			}
		}
		fibers.setTransformation(new Transformation(matrixCopy));

		
		fibers.addTool(scene.getRotateTool());
			rootNode = new SceneGraphComponent();

			
			rootNode.addChild(fibers);
			
			rootNode.addChild(scene.getCameraNode());
			rootNode.setAppearance(scene.rootApp);
			
			scene.setRootNode(rootNode);
		
		
		
	}
	
	public void run(){
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		int widthFrame=600;
		int heightFrame=600;
		JFrame frame=new JFrame();
		
		frame.	setSize(widthFrame, heightFrame);
		frame.setLocation(width/2-widthFrame/2, height/2-heightFrame/2);
		
		frame.validate();
		
		JOGLViewer	viewer = new JOGLViewer();
		viewer.setSceneRoot(rootNode);
		viewer.setCameraPath(scene.getCamPath());
		ToolSystem toolSystem = ToolSystem.toolSystemForViewer(viewer);
		toolSystem.initializeSceneTools();
		JPanel panel = (JPanel) viewer.getViewingComponent();
		// panel.setLayout(null);

		panel.setSize((int) (0.8 * width), height);
		panel.setLocation(width - (int) (0.8 * width), 0);
	frame.add(panel);
	frame.addWindowListener(new WindowAdapter() {
		  public void windowClosing(WindowEvent e) {
			  stopRequested=true;
			   
			  }
			});

frame.	setVisible(true);

	while (!stopRequested) {
		viewer.render();
			
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		
	}
	
	
	
}
