package editor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class TckWriter {

	public void writeToFile(File out, ArrayList<Fiber> fibers) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		FileChannel wChannel = null;
		try {
			wChannel = new FileOutputStream(out).getChannel();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String header = "mrtrix tracks\ndatatype: Float32LE\ncount: " + fibers.size() * 3 + "\n";

		try {
			ByteBuffer buffer = ByteBuffer.wrap(header.getBytes());
			buffer.order(ByteOrder.LITTLE_ENDIAN);
			wChannel.write(buffer);
			long offset = wChannel.position() + 20;
			String headerEnd = "file: . " + offset + "\nEND\n";
			buffer = ByteBuffer.wrap(headerEnd.getBytes());
			buffer.order(ByteOrder.LITTLE_ENDIAN);
			wChannel.write(buffer);

			long endOffset = wChannel.position();
			byte[] zeros = new byte[(int) (offset - endOffset)];

			buffer = ByteBuffer.wrap(zeros);
			buffer.order(ByteOrder.LITTLE_ENDIAN);
			wChannel.write(buffer);

			for (Fiber f : fibers) {
				for (int i = 0; i < f.getVertex().length; i++) {
					for (int j = 0; j < 3; j++) {

						writeFloat(wChannel, (float) f.getVertex()[i][j]);

					}
				}

				for (int j = 0; j < 3; j++) {

					writeFloat(wChannel, Float.NaN);

				}
			}
			for (int j = 0; j < 3; j++) {

				writeFloat(wChannel, Float.POSITIVE_INFINITY);

			}

			wChannel.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public void writeFloat(FileChannel wChannel, float value) {

		ByteBuffer floatBuffer = ByteBuffer.allocate(4);
		floatBuffer.order(ByteOrder.LITTLE_ENDIAN);
		floatBuffer.putFloat(value);
		floatBuffer.flip();
		try {
			wChannel.write(floatBuffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		new TckWriter().writeToFile(new File("prova.tck"), new ArrayList<>());
	}
}
