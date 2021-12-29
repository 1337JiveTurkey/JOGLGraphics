package ninja.cardcarrying.opengl;

public class Buffer {
	/**
	 * Triangles comprising a 2x2x2 cube with one triangle per line.
	 */
	public static final float[] cubeConstant = {
			-1f,  1f, -1f, -1f, -1f, -1f,  1f, -1f, -1f,
			 1f, -1f, -1f,  1f,  1f, -1f, -1f,  1f, -1f,
			 1f, -1f, -1f,  1f, -1f,  1f,  1f,  1f, -1f,
			 1f, -1f,  1f,  1f,  1f,  1f,  1f,  1f, -1f, // End of page 73
			 1f, -1f,  1f, -1f, -1f,  1f,  1f,  1f,  1f, // Begin of page 74
			-1f, -1f,  1f, -1f,  1f,  1f,  1f,  1f,  1f,
			-1f, -1f,  1f, -1f, -1f, -1f, -1f,  1f,  1f,
			-1f, -1f,  1f,  1f, -1f,  1f,  1f, -1f, -1f,
			 1f, -1f, -1f, -1f, -1f, -1f, -1f, -1f,  1f,
			-1f,  1f, -1f,  1f,  1f, -1f,  1f,  1f,  1f,
			 1f,  1f,  1f, -1f,  1f,  1f, -1f,  1f, -1f
	};
}
