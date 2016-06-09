

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTextField;

import de.jreality.geometry.IndexedLineSetFactory;
import de.jreality.geometry.PointSetFactory;
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
import de.jreality.tools.DragEventTool;
import de.jreality.tools.PointDragEvent;
import de.jreality.tools.PointDragListener;
import de.jreality.tools.RotateTool;
import de.jreality.toolsystem.ToolUtility;

public class AxisArea extends AbstractTool {

	

	private static Camera camera;
	
	private static double[][] vertices;
	 SceneGraphComponent rootNode ;
	 
	 SceneGraphPath camPath;
	 final SceneGraphComponent axes;
	
	public AxisArea() {
		
		// INPUT
		
		axes = AxisFactory.getXYZAxes();

		

		rootNode = new SceneGraphComponent();
		SceneGraphComponent cameraNode = new SceneGraphComponent();

		SceneGraphComponent lightNode = new SceneGraphComponent();

		rootNode.addChild(axes);
		rootNode.addChild(cameraNode);
		cameraNode.addChild(lightNode);

//		RotateTool rotateTool = new RotateTool();
//		axes.addTool(rotateTool);

		Light dl = new DirectionalLight();
		lightNode.setLight(dl);

		camera = new Camera();
		cameraNode.setCamera(camera);

		MatrixBuilder.euclidean().translate(0, 0, 3).assignTo(cameraNode);

		Appearance rootApp = new Appearance();
		rootApp.setAttribute(CommonAttributes.BACKGROUND_COLOR, new Color(1f,
				1f, 1f));

		rootNode.setAppearance(rootApp);

		camPath = new SceneGraphPath();
		camPath.push(rootNode);
		camPath.push(cameraNode);
		camPath.push(camera);

		


	}

}

