package editor;

import java.io.IOException;

import de.jreality.geometry.Primitives;
import de.jreality.math.MatrixBuilder;
import de.jreality.scene.Appearance;
import de.jreality.scene.Camera;
import de.jreality.scene.DirectionalLight;
import de.jreality.scene.Geometry;
import de.jreality.scene.Light;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.SceneGraphPath;
import de.jreality.shader.Color;
import de.jreality.shader.CommonAttributes;
import de.jreality.shader.ImageData;
import de.jreality.shader.TextureUtility;
import de.jreality.tools.RotateTool;
import de.jreality.util.Input;

public class Scene {

	
	Appearance rootApp = new Appearance();
	private static Camera camera = new Camera();
	private SceneGraphPath camPath = new SceneGraphPath();
	private static SceneGraphComponent cameraNode = new SceneGraphComponent();
	private SceneGraphComponent lightNode = new SceneGraphComponent();
	private RotateTool rotateTool = new RotateTool();
	private SceneGraphComponent rootNode = new SceneGraphComponent();

	private Geometry g;

	public Geometry getG() {
		return g;
	}

	Light dl = new DirectionalLight();

	private static Scene instance;

	public static Scene getInstance() {
		if (instance == null)
			instance = new Scene();
		return instance;
	}

	public Scene() {
		// toolSystemAxes.initializeSceneTools();
		cameraNode.setCamera(camera);
		camPath.push(rootNode);
		camPath.push(cameraNode);
		camPath.push(camera);
		lightNode.setLight(dl);
		cameraNode.addChild(lightNode);
		camera.setPerspective(false);

		
		
		rootApp.setAttribute(CommonAttributes.BACKGROUND_COLOR,Color.WHITE);
		rootApp.setAttribute(CommonAttributes.DIFFUSE_COLOR,Color.CYAN);
		
		
		MatrixBuilder.euclidean().translate(0, 0, 3).assignTo(cameraNode);

		double[] defaultPoints = { -4, -4, -4, 4, -4, -4, 4, 4, -4, -4, 4, -4 };

		g = (Primitives.texturedQuadrilateral(defaultPoints));

		try {
			TextureUtility.createTexture(rootApp,CommonAttributes.POLYGON_SHADER,ImageData.load(Input.getInput("images/gridImage2.jpg")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	

	}

	public static SceneGraphComponent getCameraNode() {
		return cameraNode;
	}

	public static Camera getCamera() {
		return camera;
	}

	public SceneGraphPath getCamPath() {
		return camPath;
	}

	public void setRootNode(SceneGraphComponent rootNode) {
		this.rootNode = rootNode;
	}

	public RotateTool getRotateTool() {
		return rotateTool;
	}

	public SceneGraphComponent getRootNode() {
		return rootNode;
	}

}
