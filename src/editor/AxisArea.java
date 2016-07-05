package editor;

import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.tool.AbstractTool;


public class AxisArea {


	Scene scene = Scene.getInstance();

	SceneGraphComponent rootNode;

	SceneGraphComponent axes = new SceneGraphComponent();

	public AxisArea() {

		axes = AxisFactory.getXYZAxes();
		axes.addTool(scene.getRotateTool());
		rootNode = new SceneGraphComponent();
		rootNode.addChild(axes);
		rootNode.addChild(scene.getCameraNode());
		rootNode.setAppearance(scene.rootApp);
		scene.setRootNode(rootNode);
			
	}

}
