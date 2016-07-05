package editor;





import java.awt.Font;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.SwingConstants;

import de.jreality.geometry.BallAndStickFactory;
import de.jreality.geometry.FrameFieldType;
import de.jreality.geometry.IndexedLineSetFactory;
import de.jreality.geometry.IndexedLineSetUtility;
import de.jreality.geometry.TubeUtility;
import de.jreality.geometry.TubeUtility.FrameInfo;
import de.jreality.math.Matrix;
import de.jreality.math.MatrixBuilder;
import de.jreality.math.P3;
import de.jreality.math.Pn;
import de.jreality.math.Rn;
import de.jreality.scene.Appearance;
import de.jreality.scene.IndexedLineSet;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.Transformation;
import de.jreality.scene.data.Attribute;
import de.jreality.scene.data.StorageModel;
import de.jreality.shader.Color;
import de.jreality.shader.CommonAttributes;
import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.DefaultPointShader;
import de.jreality.shader.DefaultTextShader;
import de.jreality.shader.ShaderUtility;
import de.jreality.util.LoggingSystem;

public  class AxisFactory {

		static int debug = 0; //255;
//		static Logger LoggingSystem.getLogger(this) = LoggingSystem.getLogger(TubeFactory.class);
		
		public double[][] theCurve, 
			userTangents = null,
			userBinormals = null,
			vertexColors, 
			edgeColors, 
			crossSection = TubeUtility.octagonalCrossSection;
		public double[] radii = null;
		public double radius = .05;
		public FrameFieldType frameFieldType = FrameFieldType.PARALLEL;
		public int metric = Pn.EUCLIDEAN;
		public int twists = 0;
		public boolean generateTextureCoordinates = false;
		boolean arcLengthTextureCoordinates = false;
		boolean generateEdges = false;
		boolean matchClosedTwist = false;
		public boolean extendAtEnds = false;
		boolean removeDuplicates = false;
		boolean duplicatesRemoved = false;
		public boolean framesDirty = true;
		protected FrameInfo[] frames = null, userFrames = null;
		protected double[] radiiField = null;
		protected double[] initialBinormal = null;
		
		public boolean closedCurve = false,
			vertexColorsEnabled = false;
		
		public AxisFactory()	{
			this(null);
		}
		
		public AxisFactory(double[][] curve)	{
			super();
			theCurve = curve;
		}
		
		public  void updateFrames() {
			
		}
		
		public FrameInfo[] getFrameField()	{
			return frames;
		}
		/**
		 * Set whether the curve should be considered a closed loop. Default: false.
		 * @param closedCurve
		 */
		public void setClosed(boolean closedCurve) {
			this.closedCurve = closedCurve;
			framesDirty = true;
		}

		/**
		 * A 2D curve in the (x,y) plane (with 3D coordinates!) to be used as the 
		 * cross section of the tube. For flexibility: unless the first and last points are equal, the
		 * curve is considered to be open and the resulting tube will not close up.
		 * Default: an octagon lying on the unit circle.
		 * @param crossSection
		 */
		public void setCrossSection(double[][] crossSection) {
			this.crossSection = crossSection;
		}

		public double[][] getTangents() {
			return userTangents;
		}

		public void setTangents(double[][] tangents) {
			this.userTangents = tangents;
		}

		public double[][] getUserBinormals() {
			return userBinormals;
		}

		public void setUserBinormals(double[][] userBinormals) {
			this.userBinormals = userBinormals;
		}

		public void setFrameField(FrameInfo[] frames)	{
			userFrames = frames;
		}
		
		/**
		 * Should the underlying frame field be generated by {@link TubeUtility#PARALLEL} or
		 * {@link TubeUtility#FRENET} displacement?  Default: {@link TubeUtility#PARALLEL}
		 * @param frameFieldType
		 */public void setFrameFieldType(FrameFieldType frameFieldType) {
			this.frameFieldType = frameFieldType;
		}

		/**
		 * Set the radius of the tube.  To be exact, this is applied as a scale factor to the
		 * cross section before it is swept out to make the tube.
		 * @param radius
		 */
		 public void setRadius(double radius) {
			this.radius = radius;
		}

		 public void setRadii( double[] radii)	{
			 this.radii = radii;
		 }
		/**
		 * Set the metric of the ambient space. See {@link Pn}.
		 * @param metric
		 */
		 public void setMetric(int metric) {
			this.metric = metric;
			framesDirty = true;
		}

		/**
		 * Set an integer number of twists to apply to the cross section as it is
		 * swept along the curve.  This twisting is done proportional to the arc length of
		 * the curve. For creating exotic shapes. Default: 0
		 * @param twists
		 */
		 public void setTwists(int twists) {
			this.twists = twists;
		}
		
		/**
		 * Whether to apply vertex colors to the output tube. See {@link #setVertexColors(double[][])}.
		 * @param vertexColorsEnabled
		 */
		 public void setVertexColorsEnabled(boolean vertexColorsEnabled) {
			this.vertexColorsEnabled = vertexColorsEnabled;
		}

		/**
		 * Apply colors to faces of output tube, one for each tube segment. 
		 * @param edgeColors	double[n][]	  where n is number of segments in curve
		 */
		public void setEdgeColors(double[][] edgeColors) {
			this.edgeColors = edgeColors;
		}

		/**
		 * Apply colors to vertices of output tube, one for each tube cross section.
		 * @param vertexColors double[n][]	  where n is number of vertices in curve
		 */
		public void setVertexColors(double[][] vertexColors) {
			this.vertexColors = vertexColors;
		}


		public boolean isGenerateTextureCoordinates() {
			return generateTextureCoordinates;
		}

		/**
		 * Whether the output geometry should have automatic texture coordinates. If true,
		 * texture coordinates will be based on
		 * the tubing parameters: u is the cross section parameter, v is the length of the tube.
		 * 
		 * @return
		 * @see #setArcLengthTextureCoordinates(boolean)
		 */
		public void setGenerateTextureCoordinates(boolean generateTextureCoordinates) {
			this.generateTextureCoordinates = generateTextureCoordinates;
		}

		public boolean isArcLengthTextureCoordinates() {
			return arcLengthTextureCoordinates;
		}

		/**
		 * If true, force the generated v- texture coordinates to run from 0 to 1 proportional
		 * to the (discrete) arc length of the curve.
		 * @param arcLengthTextureCoordinates
		 */
		public void setArcLengthTextureCoordinates(boolean arcLengthTextureCoordinates) {
			this.arcLengthTextureCoordinates = arcLengthTextureCoordinates;
		}
			
		public boolean isExtendAtEnds() {
			return extendAtEnds;
		}

		public void setExtendAtEnds(boolean extendAtEnds) {
			this.extendAtEnds = extendAtEnds;
			framesDirty = true;
		}

		public boolean isRemoveDuplicates() {
			return removeDuplicates;
		}

		public void setRemoveDuplicates(boolean removeDuplicates) {
			this.removeDuplicates = removeDuplicates;
		}

		public boolean isGenerateEdges() {
			return generateEdges;
		}

		public void setGenerateEdges(boolean generateEdges) {
			this.generateEdges = generateEdges;
		}

		public boolean isMatchClosedTwist() {
			return matchClosedTwist;
		}

		public void setMatchClosedTwist(boolean matchClosedTwist) {
			this.matchClosedTwist = matchClosedTwist;
		}

		public double[] getInitialBinormal() {
			return initialBinormal;
		}

		public void setInitialBinormal(double[] initialBinormal) {
			this.initialBinormal = initialBinormal;
		}

		public SceneGraphComponent getFramesSceneGraphRepresentation()	{
			return AxisFactory.getSceneGraphRepresentation(frames);
		}
		
		public  void update()		{
			if (removeDuplicates && !duplicatesRemoved)	{
				theCurve = removeDuplicates(theCurve);
				duplicatesRemoved = true;
			}
//			System.err.println("tube style = "+frameFieldType);
		}
		
		/**
		 * A hack to deal with a situation where vertices were repeated.
		 * @param cc
		 * @return
		 */
		protected static double[][] removeDuplicates(double[][] cc)	{
			int n = cc.length;
			Vector<double[]> v = new Vector<double[]>();
			double[] currentPoint = cc[0], nextPoint;
			v.add(currentPoint);
			int i = 1;
			double d;
			do {
				do {
					nextPoint = cc[i];
					d = Rn.euclideanDistance(currentPoint, nextPoint);
					i++;
				} while (d < 10E-16 && i < n);
				if (i==n) break;
				currentPoint = nextPoint;
				v.add(currentPoint);
			} while(i<n);
			double[][] newcurve = new double[v.size()][];
			v.toArray(newcurve);
			return newcurve;
		}
		static double[] px1 = {0,0,-.5,1};
		static double[] px2 = {0,0,.5,1};
		private double[][] tangentField;
		private double[][] frenetNormalField;
		private double[][] parallelNormalField;
		private double[][] binormalField;
		private FrameInfo[] frameInfo;
		public  FrameInfo[] makeFrameField(double[][] polygon, FrameFieldType type, int metric)		{
			if (frames!= null && !framesDirty) return frames;
		 	int numberJoints = polygon.length;
		 	double[][] polygonh;
		 	// to simplify life, convert all points to homogeneous coordinates
		 	if (polygon[0].length == 3) {
		 		polygonh = Pn.homogenize(null, polygon);
		 		Pn.normalize(polygonh, polygonh, metric);
		 	}
			else if (polygon[0].length == 4)	
				polygonh = Pn.normalize(null, polygon, metric);
			else {
				throw new IllegalArgumentException("Points must have dimension 4");
			}
		 	if ((debug & 1) != 0)	
		 		LoggingSystem.getLogger(this).log(Level.FINER,"Generating frame field for metric "+metric);
		 	if (tangentField == null || tangentField.length != (numberJoints-2))	{
				tangentField = new double[numberJoints-2][4];
				frenetNormalField = new double[numberJoints-2][4];
				parallelNormalField = new double[numberJoints-2][4];
				binormalField = new double[numberJoints-2][4];
		 	}
			frameInfo = new FrameInfo[numberJoints-2];		 		
			double[] d  = new double[numberJoints-2];			// distances between adjacent points
			if ((debug & 32) != 0)	{
				for (int i = 0; i<numberJoints; ++i)	{
					LoggingSystem.getLogger(this).log(Level.FINER,"Vertex "+i+" : "+Rn.toString(polygonh[i]));
				}
			}
			double[] frame = new double[16];
			double totalLength = 0.0;
			for (int i = 1; i<numberJoints-1; ++i)	{
				d[i-1] = (totalLength += Pn.distanceBetween(polygonh[i-1], polygonh[i], metric));
			}
			totalLength = 1.0/totalLength;
			// Normalize the distances between points to have total sum 1.
			for (int i = 1; i<numberJoints-1; ++i)	d[i-1] *= totalLength;

			for (int i = 1; i<numberJoints-1; ++i)	{
				
				/*
				 * calculate the binormal from the osculating plane
				 */
				double theta = 0.0, phi=0.0;
				boolean collinear = false;
				double[] polarPlane = Pn.polarizePoint(null, polygonh[i], metric);
				if ((debug & 2) != 0) LoggingSystem.getLogger(this).log(Level.FINER,"Polar plane is: "+Rn.toString(polarPlane));					
				
				double[] osculatingPlane = P3.planeFromPoints(null, polygonh[i-1], polygonh[i], polygonh[i+1]);
				double size = Rn.euclideanNormSquared(osculatingPlane);
				if (size < 10E-16)	{			// collinear points!
					collinear = true;
					if ((debug & 2) != 0) LoggingSystem.getLogger(this).log(Level.FINER,"degenerate binormal");
					if (i == 1)		binormalField[i-1] = getInitialBinormal(polygonh, metric);
					else Pn.projectToTangentSpace(binormalField[i-1], polygonh[i], binormalField[i-2], metric);
				} else
					Pn.polarizePlane(binormalField[i-1], osculatingPlane,metric);
				if (userBinormals != null) 
					System.arraycopy(userBinormals[i-1], 0, binormalField[i-1], 0, userBinormals[i-1].length);
				Pn.setToLength(binormalField[i-1], binormalField[i-1], 1.0, metric);
				if (i>1 && metric == Pn.ELLIPTIC)	{
					double foo = Pn.angleBetween(binormalField[i-2], binormalField[i-1], metric);
					if (Math.abs(foo) > Math.PI/2)  Rn.times(binormalField[i-1], -1, binormalField[i-1]);
				}
				if ((debug & 2) != 0) LoggingSystem.getLogger(this).log(Level.FINER,"Binormal is "+Rn.toString(binormalField[i-1]));
//				System.err.println("Binormal field = "+Rn.toString(binormalField[i-1]));

				/*
				 * Next try to calculate the tangent as a "mid-plane" if the three points are not collinear
				 */
				double[] midPlane = null, plane1 = null, plane2 = null;
				if (!collinear)	{
					plane1 = P3.planeFromPoints(null, binormalField[i-1], polygonh[i], polygonh[i-1]);
					plane2 = P3.planeFromPoints(null, binormalField[i-1], polygonh[i], polygonh[i+1]);
					midPlane = Pn.midPlane(null, plane1, plane2, metric);
					size = Rn.euclideanNormSquared(midPlane);
					if ((debug & 2) != 0) LoggingSystem.getLogger(this).log(Level.FINER,"tangent norm squared is "+size);					
					theta = Pn.angleBetween(plane1, plane2, metric);
				}
				/*
				 * if this is degenerate, then the curve must be collinear at this node
				 * get the tangent by projecting the line into the tangent space at this point
				 */ 
				if (collinear || size < 10E-16)	{
					// the three points must be collinear
					if ((debug & 2) != 0) LoggingSystem.getLogger(this).log(Level.FINER,"degenerate Tangent vector");
					// if the two vertices in the following call are swapped
					double[] pseudoT = P3.lineIntersectPlane(null, polygonh[i-1], polygonh[i+1], polarPlane);	
					if ((debug & 2) != 0) LoggingSystem.getLogger(this).log(Level.FINE,"pseudo-Tangent vector is "+Rn.toString(pseudoT));
					// more euclidean/noneuclidean trouble
					// we want the plane equation of the midplane 
					if (metric != Pn.EUCLIDEAN)	{
						midPlane = Pn.polarizePoint(null, pseudoT, metric);
					} else {
						// has to be flipped in this case but not in the non-euclidean case
						midPlane = Rn.times(null, -1.0, pseudoT);
//						midPlane = pseudoT;
						// the euclidean polar of a point is the plane at infinity: we want something
						// much more specific: 
						// we assume the polygonal data is dehomogenized (last coord = 1)
						midPlane[3] = -Rn.innerProduct(midPlane, polygonh[i], 3);						
					}	
					theta = Math.PI;
				}
				//System.err.println("calc'ed midplane is "+Rn.toString(midPlane));
				if ((debug & 2) != 0) LoggingSystem.getLogger(this).log(Level.FINE,"Midplane is "+Rn.toString(midPlane));
					Pn.polarizePlane(tangentField[i-1], midPlane, metric);						
				if (userTangents == null){
				} else {
					System.arraycopy(userTangents[i-1], 0, tangentField[i-1], 0, userTangents[i-1].length);
					midPlane = Rn.planeParallelToPassingThrough(null, userTangents[i-1], polygonh[i]);
					//System.err.println("given midplane is "+Rn.toString(midPlane));
					if (i>1) theta = Pn.angleBetween(userTangents[i-2], userTangents[i-1], metric);
					else theta = 0;
				}
				// This is a hack to try to choose the correct version of the tangent vector:
				// since we're in projective space, t and -t are equivalent but only one
				// "points" in the correct direction.  Deserves further study!
				double[] diff = Rn.subtract(null, polygonh[i], polygonh[i-1]);
				if (Rn.innerProduct(diff, tangentField[i-1]) < 0.0)  
					Rn.times(tangentField[i-1], -1.0, tangentField[i-1]);

				Pn.setToLength(tangentField[i-1], tangentField[i-1], 1.0, metric);
//				System.err.println("Tangent field = "+Rn.toString(tangentField[i-1]));
				//System.err.println("tangent is "+Rn.toString(tangentField[i-1]));
				// finally calculate the normal vector
				Pn.polarizePlane(frenetNormalField[i-1], P3.planeFromPoints(null,binormalField[i-1], tangentField[i-1],  polygonh[i]),metric);					
				Pn.setToLength(frenetNormalField[i-1], frenetNormalField[i-1], 1.0, metric);
				if ((debug & 2) != 0) LoggingSystem.getLogger(this).log(Level.FINE,"frenet normal is "+Rn.toString(frenetNormalField[i-1]));
				if (type == FrameFieldType.PARALLEL)	{
					// get started 
					if (i == 1)		System.arraycopy(frenetNormalField[0], 0, parallelNormalField[0], 0, 4);		
					else 	{
						double[] nPlane = P3.planeFromPoints(null, polygonh[i], polygonh[i-1], parallelNormalField[i-2]);
						double[] projectedN = P3.pointFromPlanes(null, nPlane, midPlane, polarPlane );
						if (Rn.euclideanNormSquared(projectedN) < 10E-16)	{
							LoggingSystem.getLogger(this).log(Level.FINE,"degenerate normal");
							projectedN = parallelNormalField[i-2];		// try something!
						}
						parallelNormalField[i-1] = Pn.normalizePlane(null, projectedN, metric);
						if ((debug & 128) != 0)	LoggingSystem.getLogger(this).log(Level.FINE,"Parallel normal is "+Rn.toString(parallelNormalField[i-1]));
					
					}
//					if (size < 10E-16)	{
//						if ((debug & 2) != 0) LoggingSystem.getLogger(this).log(Level.FINE,"degenerate parallel normal");
//						if (i > 1) parallelNormalField[i-1] = parallelNormalField[i-2];
//					}
					if (parallelNormalField[i-1] == null)	{
						parallelNormalField[i-1] = parallelNormalField[i-2];
//						throw new IllegalStateException("Null vector");
					} else 
						Pn.setToLength(parallelNormalField[i-1], parallelNormalField[i-1], 1.0, metric);
					phi = Pn.angleBetween(frenetNormalField[i-1],parallelNormalField[i-1],metric);
					if (metric == Pn.ELLIPTIC)	{
						if (phi > Math.PI/2) phi = phi - Math.PI;
						else if (phi < -Math.PI/2) phi = phi + Math.PI;
					}
					double a = Pn.angleBetween(parallelNormalField[i-1],binormalField[i-1],metric);
					if (a > Math.PI/2) phi = -phi;
				} 
//				size = Rn.euclideanNormSquared(pN[i-1]);
				else phi = 0.0;
				
				System.arraycopy(frenetNormalField[i-1], 0, frame, 0, 4);
				System.arraycopy(binormalField[i-1], 0, frame, 4, 4);
				System.arraycopy(tangentField[i-1], 0, frame, 8, 4);
				System.arraycopy(polygonh[i], 0, frame, 12, 4);
				   	
				if ((debug & 4) != 0) LoggingSystem.getLogger(this).log(Level.FINE,"determinant is:\n"+Rn.determinant(frame));
				frameInfo[i-1] = new FrameInfo(Rn.transpose(null, frame),d[i-1],theta, phi);
				if ((debug & 16) != 0) LoggingSystem.getLogger(this).log(Level.FINE,"Frame "+(i-1)+": "+frameInfo[i-1].toString());
			}
			framesDirty = false;
			return frameInfo;
		 }

		static double[] B = new double[] {Math.random(), Math.random(), Math.random(), 1.0};
		/**
		 * 
		 * @param polygon
		 * @param metric
		 * @return
		 */
		 protected  double[] getInitialBinormal(double[][] polygon, int metric)	{
			int n = polygon.length;
			for (int i = 1; i<n-1; ++i)	{
				double[] bloop = 
				Pn.polarize(null, P3.planeFromPoints(null, polygon[i-1], polygon[i], polygon[i+1]),metric);	
				if (Rn.euclideanNormSquared(bloop) > 10E-16) {
					Pn.dehomogenize(bloop,bloop);
//					boolean flip = false;
//					if (bloop[0] <0) flip = true;
//					else if (bloop[0] == 0.0 && bloop[1] < 0) flip = true;
//					else if (bloop[1] == 0.0 && bloop[2] < 0) flip = true;
//					if (flip) for (int j = 0; j<3; ++j) bloop[j] = -bloop[j];
					return bloop;
				}
			}
			// all points are collinear, choose a random plane through the first two points and polarize it to get a point
			return Pn.polarizePlane(null,  initialBinormal != null ?  initialBinormal : P3.planeFromPoints(null, B, polygon[1], polygon[2]),metric);
		}

		 static double[][] axes = {{0,0,0},{1,0,0},{0,1,0},{0,0,1}};
		 static int[][] axesIndices = {{0,1},{0,2},{0,3}};
		 static Color[] axesColors = {Color.red, Color.green, Color.blue};
		 static String [] assi={"X","Y","Z"};

		public static SceneGraphComponent getSceneGraphRepresentation(FrameInfo[] frames)	{
			return getSceneGraphRepresentation(frames, .02);
		}

		public static SceneGraphComponent getSceneGraphRepresentation(FrameInfo[] frames, double scale)	{
			SceneGraphComponent result = new SceneGraphComponent();
			IndexedLineSet ils;
			SceneGraphComponent geometry = getXYZAxes();
			MatrixBuilder.euclidean().scale(scale).assignTo(geometry);
			double[][] verts = new double[frames.length][];
			int i = 0;
			for (FrameInfo f : frames)	{
				SceneGraphComponent foo = new SceneGraphComponent();
				double[] scaledFrame = Rn.times(null, f.frame, 	P3.makeRotationMatrixZ(null, f.phi));
				Transformation t = new Transformation(scaledFrame);
				foo.setTransformation(t);
				foo.addChild(geometry);
				result.addChild(foo);
				verts[i++] = (new Matrix(f.frame)).getColumn(3);
			}
			SceneGraphComponent sgc = new SceneGraphComponent();
			Appearance ap = new Appearance();
			ap.setAttribute(CommonAttributes.LINE_SHADER+"."+CommonAttributes.TUBE_RADIUS, .005);
			ap.setAttribute(CommonAttributes.LINE_SHADER+"."+CommonAttributes.TUBES_DRAW, false);
			ap.setAttribute(CommonAttributes.LINE_SHADER+"."+CommonAttributes.DIFFUSE_COLOR, new Color(100, 200, 200));
			ap.setAttribute(CommonAttributes.LINE_SHADER+"."+"polygonShader.diffuseColor", new Color(100, 200, 200));
			sgc.setAppearance(ap);
			ils = IndexedLineSetUtility.createCurveFromPoints(verts, false);
			sgc.setGeometry(ils);
			result.addChild(sgc);
			return result;
		}

		public static SceneGraphComponent getXYZAxes() {
			IndexedLineSetFactory ilsf = new IndexedLineSetFactory();
			ilsf.setVertexCount(4);
		
			ilsf.setVertexCoordinates(axes);
			ilsf.setEdgeCount(3);
			ilsf.setEdgeIndices(axesIndices);
			ilsf.setEdgeColors(axesColors);
			ilsf.update();
			
			
			
			IndexedLineSet ils = ilsf.getIndexedLineSet();
			String labels[] = {"","x","y","z"};
			//int n=ils.getNumEdges();
			ils.setVertexAttributes(Attribute.LABELS, StorageModel.STRING_ARRAY.createReadOnly(labels));

			
			BallAndStickFactory basf = new BallAndStickFactory(ils);
			basf.setShowArrows(true);
			basf.setArrowPosition(1.0);
			basf.setStickRadius(.01);
			basf.setArrowScale(.05);
			basf.setArrowSlope(2.0);
			basf.setShowBalls(false);
			basf.setShowSticks(true);
			basf.update();
			SceneGraphComponent geometry2 = basf.getSceneGraphComponent();
			SceneGraphComponent geometry = new SceneGraphComponent();
			
			Appearance a = new Appearance();
		    geometry.setAppearance(a);
		    geometry.setGeometry(ils);
		    
		    DefaultGeometryShader dgs = ShaderUtility.createDefaultGeometryShader(a, false);
			dgs.setShowPoints(true);
		    DefaultTextShader pts = (DefaultTextShader) ((DefaultPointShader)dgs.getPointShader()).getTextShader();
		    
		    pts.setDiffuseColor(Color.blue);
		    
		    // scale the label
		    Double scale = new Double(0.01);
		    pts.setScale(.5*scale);
		    // apply a translation to the position of the label in camera coordinates (-z away from camera)
		    double[] offset = new double[]{-.1,0.05,0.3};
		    pts.setOffset(offset);
		    
		    // the alignment specifies a direction in which the label will be shifted in the 2d-plane of the billboard
		    pts.setAlignment(SwingConstants.NORTH_WEST);
		    
		    // here you can specify any available Java font
		    Font f = new Font("Arial Bold", Font.ITALIC, 20);
		    pts.setFont(f);
			SceneGraphComponent sgc= new SceneGraphComponent();
			sgc.addChild(geometry2);
			sgc.addChild(geometry);
			return sgc;
		}
}
