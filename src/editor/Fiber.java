/*A Fiber is a matrix of double, where each array of double is a vertex with the three coordinates.
 */
package editor;

public class Fiber {
	private double[][] vertices;

	public Fiber(int dim) {
		vertices = new double[dim][3];

	}

	public Fiber(double[][] v) {
		vertices = v;
	}

	public double[][] getVertex() {
		return vertices;
	}

	public void setVertex(double[][] v) {
		vertices = v;
	}

	public void setPoint(int i, double x, double y, double z) {
		vertices[i][0] = x;
		vertices[i][1] = y;
		vertices[i][2] = z;
	}

	public int size() {
		return vertices.length;
	}

}
