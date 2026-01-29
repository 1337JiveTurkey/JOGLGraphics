package ninja.cardcarrying.opengl;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.util.glsl.ShaderUtil;

import java.util.EnumMap;
import java.util.logging.Logger;

/**
 * Object representing all of the shaders and
 */
public class Program implements AutoCloseable {
	private static final Logger log = Logger.getLogger(Program.class.getName());

	private final int programName;
	private final EnumMap<Shader.Type, Shader> shaders = new EnumMap<>(Shader.Type.class);

	public Program() {
		var gl = GL4();
		programName = gl.glCreateProgram();
	}

	private GL4 GL4() {
		return GLContext.getCurrentGL().getGL4();
	}

	public void compile() {
		for(Shader shader : shaders.values()) {
			shader.compile();
		}
		var gl = GL4();
		for(Shader shader : shaders.values()) {
			int shaderName = shader.getName();
			gl.glAttachShader(programName, shaderName);
		}
		gl.glLinkProgram(programName);
		String infoLog = ShaderUtil.getProgramInfoLog(gl, programName);
		log.info(infoLog);
	}

	public void use() {
		var gl = GL4();
		gl.glUseProgram(programName);
	}

	public void close() throws Exception {
		var gl = GL4();
		gl.glDeleteProgram(programName);
		for(Shader shader : shaders.values()) {
			shader.close();
		}
	}

	public Shader getShader(Shader.Type type) {
		Shader shader = shaders.get(type);
		if (shader == null) {
			shader = new Shader(type);
			shaders.put(type, shader);
		}
		return shader;
	}

	public int getUniformLocation(String uniformName) {
		var gl = GL4();
		return gl.glGetUniformLocation(programName, uniformName);
	}
}
