package ninja.cardcarrying;

import javax.swing.*;
import static com.jogamp.opengl.GL4.*;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.glsl.ShaderUtil;
import ninja.cardcarrying.opengl.Program;
import ninja.cardcarrying.opengl.Shader;
import org.joml.Matrix4f;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.FloatBuffer;
import java.nio.file.Path;

public class Graphics extends JFrame implements GLEventListener {

	public static final float[] vertexPositions = {
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

	/**
	 * Location of the shader files relative to project root.
	 */
	private static final Path shaderPath = Path.of("src", "main", "resources", "shaders");
	private static final Path vertexShaderPath = shaderPath.resolve("trivialVertex.shader");
	private static final Path fragmentShaderPath = shaderPath.resolve("trivialFragment.shader");

	private Program program;
	private final GLCanvas canvas;
	private final int[] vao = new int[1];
	private final int[] vbo = new int[2];

	private float cameraX, cameraY, cameraZ;
	private float cubeLocX, cubeLocY, cubeLocZ;

	// For matrices
	private final FloatBuffer vals = Buffers.newDirectFloatBuffer(16);

	private final Matrix4f pMat = new Matrix4f(); // Perspective
	private final Matrix4f vMat = new Matrix4f(); // View
	private final Matrix4f mMat = new Matrix4f(); // Model
	private final Matrix4f mvMat = new Matrix4f();// Model-View

	public Graphics() {
		setTitle("Graphics test");
		setSize(1600, 1200);
		setLocation(1000, 800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GLProfile profile = GLProfile.get(GLProfile.GL4);
		GLCapabilities capabilities = new GLCapabilities(profile);
		canvas = new GLCanvas(capabilities);
		canvas.addGLEventListener(this);
	}

	public void init(GLAutoDrawable drawable) {
		var gl = GL4();
		program = new Program();
		Shader vertexShader = program.getShader(Shader.Type.Vertex).withResource("shaders/simpleVertex.shader");
		Shader fragmentShader = program.getShader(Shader.Type.Fragment).withResource("shaders/simpleFragment.shader");
		program.compile();
		cameraX = 0.0f;
		cameraY = 0.0f;
		cameraZ = 8.0f;
		cubeLocX = 0.0f;
		cubeLocY = -2.0f;
		cubeLocZ = 0.0f;
		setupVertices();
	}

	private void setupVertices() {
		var gl = GL4();

		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
		gl.glGenBuffers(vbo.length, vbo, 0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		FloatBuffer vertBuff = Buffers.newDirectFloatBuffer(vertexPositions);
		gl.glBufferData(GL_ARRAY_BUFFER, vertBuff.limit() * 4L, vertBuff, GL_STATIC_DRAW);
	}

	public void dispose(GLAutoDrawable drawable) {
		try {
			program.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void display(GLAutoDrawable drawable) {
		var gl = GL4();
		program.use();


		matrixTransforms();

		int mvLoc = program.getUniformLocation("mv_matrix");
		gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));

		int projLoc = program.getUniformLocation("proj_matrix");
		gl.glUniformMatrix4fv(projLoc, 1, false, pMat.get(vals));

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		gl.glClear(GL_DEPTH_BUFFER_BIT);
		gl.glClear(GL_COLOR_BUFFER_BIT);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
		gl.glDrawArrays(GL_TRIANGLES, 0, 36);
	}

	private void matrixTransforms() {

		vMat.translation(-cameraX, -cameraY, -cameraZ);
		mMat.translation(cubeLocX, cubeLocY, cubeLocZ);

		mvMat.identity();
		mvMat.mul(vMat);
		mvMat.mul(mMat);
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		float aspect = (float) width / (float) height;
		pMat.setPerspective((float) Math.toRadians(60.0), aspect, 0.1f, 1000.0f);
	}

	private GL4 GL4() {
		return GLContext.getCurrentGL().getGL4();
	}

	public static void main(String... argv) {
		try {
			EventQueue.invokeAndWait(() -> {
				var g = new Graphics();
				g.add(g.canvas);
				g.setVisible(true);
				g.requestFocus();
			});
		} catch (InterruptedException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
