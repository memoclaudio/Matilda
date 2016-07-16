package editor;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class TracksReader {

	private static boolean datatype = false; // false = BE true = LE
	private static int offset = 0;
	private static int N = 0;
	private static BufferedInputStream inp = null;
	private static float[][] data;
	private static double[][][] tracks;
	
	public static double[][][] read(String file) {

		// long startTime = System.nanoTime();

		// String path =
		// "C:/Users/Leviathan/Desktop/Experiments/JHU_tracts_on_IIT3_FA_volumes/volume2_fibers.tck";

		readHeader(file);

		readData();

		// System.out.println("K SIZE: ");
		// System.out.println(k.size());

		// float[][] a_track = tracks.get(tracks.size()-1);
		return tracks;

		// long endTime = System.nanoTime();
		//
		// long duration = endTime - startTime;
		//
		// System.out.println(duration);

	}

	public static void readHeader(String file) {

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = null;
			while (!(line = br.readLine()).equals("END")) {
				String[] field = line.split(":");
				if (field[0].equals("datatype") && field[1].contains("LE"))
					datatype = true;

				else if (field[0].equals("file")) {
					String f = field[1].substring(
							field[1].lastIndexOf(' ') + 1, field[1].length());
					offset = Integer.parseInt(f);
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null,
					"Can not find the file specified");
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,"Unable to read file");
		}

		try {

			inp = new BufferedInputStream(new FileInputStream(file));
			// 505(esempio) � il valore di offset da skippare per iniziare a
			// leggere le fibre. Si trova scritto nell'header
			inp.skip(offset);
			// available is the input stream to be read. inp.available/4 because
			// of 4 byte and /3 because of 3D fibers, so /12
			N = Math.round(inp.available() / 12);

		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null,"Unable to read file");
				}
	}

	public static void readData() {

		data = new float[N][3];
		List<Integer> k = new ArrayList<Integer>();

		byte[] buffer = new byte[4];
		// ByteBuffer byteBuffer = ByteBuffer.allocate(4);
		try {
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < 3; j++) {
					// if(inp.available() > 0){ //disabled due to performance
					// issue
					inp.read(buffer);
					int nextInt = 0;
					if (datatype)
						nextInt = (buffer[0] & 0xFF) | (buffer[1] & 0xFF) << 8
								| (buffer[2] & 0xFF) << 16
								| (buffer[3] & 0xFF) << 24;
					else
						nextInt = (buffer[0] & 0xFF) | (buffer[1] & 0xFF) << 24
								| (buffer[2] & 0xFF) << 16
								| (buffer[3] & 0xFF) << 8;

					float nextFloat = Float.intBitsToFloat(nextInt);
					// System.out.println(nextFloat);
					data[i][j] = nextFloat; // contains also NAN values
					if (Float.isNaN(nextFloat) && j == 0) // the other js are
															// NaN too and will
															// be put as a row:
															// NaN NaN NaN
						k.add(i);

					// another way to do it
					// byteBuffer = ByteBuffer.wrap(buffer);
					// data[i][j] =
					// byteBuffer.order(ByteOrder.LITTLE_ENDIAN).getFloat();
					// //contains also NAN values
				}
				// else{
				// System.out.println("EOF");
				// break;
				// }
				// }
			}
			// System.out.println("END");
			// if(inp.available() > 0){
			// System.out.println("THERE'S MORE!");
			// }
			// else{
			// System.out.println("IT'S REALLY THE EOF!");
			// }

			inp.close();
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null,"Unable to read file");
			
		}

		formatTracks(k);

	}

	public static void formatTracks(List<Integer> k) {

		int pk = 0;

		// List<float[][]> tracks = new ArrayList<float[][]>();
		tracks = new double[k.size()][][];

		for (int i = 0; i < k.size(); i++) {
			int it = 0;
			// System.out.println(k.get(i)-pk);

			// float[][] track = new float[k.get(i)-pk][3];
			tracks[i] = new double[k.get(i) - pk][3];
			while (pk < k.get(i)) {
				for (int j = 0; j < 3; j++) {
					tracks[i][it][j] = data[pk][j];
				}
				it++;
				pk++;
			}
			pk++;
		}
	}

	public static void printTrack(float[][] a_track) {

		for (int i = 0; i < a_track.length; i++) {
			for (int j = 0; j < 3; j++) {
				System.out.print(a_track[i][j] + " ");
			}
			System.out.println();
		}
	}

	
	
	
	
	public static boolean isDatatype() {
		return datatype;
	}

	public static void setDatatype(boolean datatype) {
		TracksReader.datatype = datatype;
	}

	public static int getOffset() {
		return offset;
	}

	public static void setOffset(int offset) {
		TracksReader.offset = offset;
	}

	public static int getN() {
		return N;
	}

	public static void setN(int n) {
		N = n;
	}

	public static BufferedInputStream getInp() {
		return inp;
	}

	public static void setInp(BufferedInputStream inp) {
		TracksReader.inp = inp;
	}

	public static float[][] getData() {
		return data;
	}

	public static void setData(float[][] data) {
		TracksReader.data = data;
	}

	public static double[][][] getTracks() {
		return tracks;
	}

	public static void setTracks(double[][][] tracks) {
		TracksReader.tracks = tracks;
	}

}
