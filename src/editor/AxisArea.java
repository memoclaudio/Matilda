package editor;

import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.tool.AbstractTool;
import de.jreality.ui.viewerapp.actions.edit.AddTool;


public class AxisArea extends AbstractTool {

	// private static Camera camera;
	// SceneGraphPath camPath;
	// final SceneGraphComponent axes;

	Scene scene = Scene.getInstance();

	SceneGraphComponent rootNode;

	SceneGraphComponent axes = new SceneGraphComponent();

	public AxisArea() {

		// INPUT
		// SceneGraphComponent cameraNode = new SceneGraphComponent();

		// SceneGraphComponent lightNode = new SceneGraphComponent();
		// cameraNode.addChild(lightNode);

		// RotateTool rotateTool = new RotateTool();
		// axes.addTool(rotateTool);
		// Light dl = new DirectionalLight();
		// lightNode.setLight(dl);
		//
		// camera = new Camera();
		// cameraNode.setCamera(camera);
		//
		// MatrixBuilder.euclidean().translate(0, 0, 3).assignTo(cameraNode);
		//
		// Appearance rootApp = new Appearance();
		// rootApp.setAttribute(CommonAttributes.BACKGROUND_COLOR, new Color(1f,
		// 1f, 1f));
		//
		//
		//
		// rootNode.setAppearance(rootApp);

		// camPath = new SceneGraphPath();
		// camPath.push(rootNode);
		// camPath.push(cameraNode);
		// camPath.push(camera);


		axes = AxisFactory.getXYZAxes();

		axes.addTool(scene.getRotateTool());
		rootNode = new SceneGraphComponent();
		rootNode.addChild(axes);
		rootNode.addChild(scene.getCameraNode());
		rootNode.setAppearance(scene.rootApp);
		scene.setRootNode(rootNode);
		
	}

}
