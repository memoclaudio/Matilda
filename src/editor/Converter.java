/* This class convert a matrix of double, that represent vertices of a fiber, into a string
 * using "n" as parameter of discretization. 
 */

package editor;

public class Converter {

	public static String convertMatrixToString(double[][] vertices, int n) {

		int dim = vertices.length;
		int[][] RGB = new int[dim][3];
		int[][] pixels = new int[dim][1];
		int iRGB = 0;
		// conversion into RGB of the first point
		getColorTransformation(0, 1, iRGB, RGB, pixels, vertices);
		iRGB++;
		// conversion into RGB from second to second-last point
		for (int i = 1; i < vertices.length - 1; i++) {
			getColorTransformation(i - 1, i + 1, iRGB, RGB, pixels, vertices);
			iRGB++;
		}

		// conversion into RGB of the last point
		getColorTransformation(vertices.length - 2, vertices.length - 1, iRGB,
				RGB, pixels, vertices);
		iRGB++;

		String myString = "";
		// function that discretize colors
		Quantize.quantizeImage(pixels, n);
		for (int i = 0; i < pixels.length; i++) {
			myString += pixels[i][0];
		}
		return myString;

	}

	private static void getColorTransformation(int index1, int index2,
			int iRGB, int[][] RGB, int[][] pixels, double[][] vertices) {
		double succ[] = vertices[index2];
		double prec[] = vertices[index1];
		double delta[] = new double[3];

		for (int i = 0; i < 3; i++) {
			delta[i] = Math.abs(succ[i] - prec[i]);
		}

		double deltaLength = 0;

		for (int i = 0; i < 3; i++) {
			deltaLength += delta[i] * delta[i];
		}

		deltaLength = Math.sqrt(deltaLength);

		for (int i = 0; i < 3; i++) {
			delta[i] /= deltaLength;
		}

		for (int i = 0; i < 3; i++) {
			// new RGB value
			RGB[iRGB][i] = (int) (delta[i] * 255);

		}
		// used for discretization
		int redC = (int) (Math.round(RGB[iRGB][0])) << 16;
		int greenC = (int) (Math.round(RGB[iRGB][1])) << 8;
		int blueC = (int) (Math.round(RGB[iRGB][2]));
		int pixel = redC + greenC + blueC;
		pixels[iRGB][0] = pixel;

	}
}
