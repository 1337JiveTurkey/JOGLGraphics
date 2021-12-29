package ninja.cardcarrying.opengl;

import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.util.glsl.ShaderUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Convenient API for specifying shaders in a JOGL program.
 *
 * @author Paul Setzer
 */
public class Shader implements AutoCloseable {
	private static final Logger log = Logger.getLogger(Shader.class.getName());

	/**
	 * Called name since OpenGL uses that terminology for its numeric handles.
	 */
	private final int shaderName;
	/**
	 * The type of this shader, determines how it slots into a program.
	 */
	private final Type type;

	/**
	 * Store the source code before compiled in a resizable arraylist.
	 */
	private final List<String> sourceLines = new ArrayList<>();

	/**
	 * Keep track of the path of any loaded shaders for debugging purposes.
	 */
	private Path sourcePath = null;

	protected Shader(Type type) {
		this.type = type;
		shaderName = GL4().glCreateShader(type.glConst);
	}

	private GL4 GL4() {
		return GLContext.getCurrentGL().getGL4();
	}

	public Shader withLines(String... lines) {
		Collections.addAll(sourceLines, lines);
		return this;
	}

	/**
	 * This method loads a shader's source code from an arbitrary path located at runtime.
	 *
	 * @param sourcePath The path of the shader's source code.
	 * @return The shader for call chaining.
	 */
	public Shader withSource(Path sourcePath) {
		try {
			this.sourcePath = sourcePath;
			if (!Files.isReadable(sourcePath)) {
				throw new FileNotFoundException("\"" + sourcePath + "\" doesn't match a readable file name.");
			}
			Files.lines(sourcePath).map(line -> line + "\n").forEach(sourceLines::add);
		} catch (IOException e) {
			log.log(Level.SEVERE, "Error reading in source for " + type + " shader.", e);
		}
		return this;
	}

	/**
	 * This method loads a shader's source code from a resource located within
	 * the program's classpath. For this program this will typically be a file
	 * located within "src/main/resources" that is zipped into the JAR file when
	 * it is built.
	 *
	 * @param resourceName The name of the resource where the source code is stored.
	 * @return The shader for call chaining.
	 */
	public Shader withResource(String resourceName) {
		try {
			var resource = this.getClass().getClassLoader().getResource(resourceName);
			if (resource == null) {
				throw new IllegalArgumentException("No classpath resource matches " + resourceName);
			}
			return withSource(Paths.get(resource.toURI()));
		} catch (URISyntaxException e) {
			log.log(Level.SEVERE, "Classloader gave a malformed URI for " + type + " shader at " + resourceName + ".", e);
		}
		return this;
	}

	/**
	 * Compile this shader. Any messages are currently logged but in the future
	 * this should stop execution.
	 */
	protected void compile() {
		var gl = GL4();
		var numLines = sourceLines.size();
		if (numLines == 0) {
			throw new IllegalStateException(type + " Shader is declared but missing any source code.");
		}
		var sourceArray = sourceLines.toArray(new String[numLines]);
		gl.glShaderSource(shaderName, sourceLines.size(), sourceArray, null, 0);
		gl.glCompileShader(shaderName);

		String infoLog = ShaderUtil.getShaderInfoLog(gl, shaderName);
		log.info(infoLog);
	}

	public Type getShaderType() {
		return type;
	}

	protected int getName() {
		return shaderName;
	}

	public void close() throws Exception {
		var gl = GL4();
		gl.glDeleteShader(shaderName);
		// TODO Handle any errors that arise from this
	}

	/**
	 * The type of this shader.
	 */
	public enum Type {
		Compute(GL_COMPUTE_SHADER),
		Fragment(GL_FRAGMENT_SHADER),
		Geometry(GL_GEOMETRY_SHADER),
		TesselationControl(GL_TESS_CONTROL_SHADER),
		TesselationEvaluation(GL_TESS_EVALUATION_SHADER),
		Vertex(GL_VERTEX_SHADER),
		;

		Type(int glConst) {
			this.glConst = glConst;
		}
		public final int glConst;
	}

	public enum Status {
		Unspecified,
	}
}
