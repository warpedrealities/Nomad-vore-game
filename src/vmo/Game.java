package vmo;

import static org.lwjgl.glfw.GLFW.GLFW_ALPHA_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_BLUE_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_DEPTH_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_GREEN_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_COMPAT_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RED_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_SAMPLES;
import static org.lwjgl.glfw.GLFW.GLFW_STENCIL_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCharCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import font.FontSupport;
import gui.SharedGUIResources;
import input.Keyboard;
import input.MouseHook;
import menu.Menu;
import nomad.universe.Universe;
import shared.Scene;
import shared.SceneBase;
import shared.SceneManager;

public class Game implements SceneManager {

	private GLFWErrorCallback errorCallback;
	private GLFWKeyCallback keyCallback;
	private long openGLWindow;
	public static SceneManager sceneManager;
	double m_lastframe;
	int m_vshader, m_fshader, m_vshadowshader, m_fshadowshader, m_tintvar0, m_viewvar0, m_objvar0;
	int m_viewvar1, m_objvar1;
	static public int m_pshader, m_pshadowshader;
	Scene m_currentscene;
	GameManager m_gman;

	Config graphicsConfiguration;
	MouseHook mouseInput;

	// public static float m_scale;

	@Override
	public Config getConfig() {
		return graphicsConfiguration;
	}

	public Game() {

		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));

		sceneManager = this;
		graphicsConfiguration = new Config();
		//
		//		  try { System.setErr(new PrintStream(new
		//		  FileOutputStream(System.getProperty("user.dir")+"/error.log"))); }
		//		  catch (FileNotFoundException ex) { ex.printStackTrace(); }
		//

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}
		// Configure our window
		glfwDefaultWindowHints(); // optional, the current window hints are
		// already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden
		// after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will be
		// resizable
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
		glfwWindowHint(GLFW_RED_BITS, 8);
		glfwWindowHint(GLFW_GREEN_BITS, 8);
		glfwWindowHint(GLFW_BLUE_BITS, 8);
		glfwWindowHint(GLFW_ALPHA_BITS, 8);
		glfwWindowHint(GLFW_DEPTH_BITS, 0);
		glfwWindowHint(GLFW_STENCIL_BITS, 8);

		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_COMPAT_PROFILE);
		glfwWindowHint(GLFW_SAMPLES, 8);

		int WIDTH = (int) (1280 * graphicsConfiguration.getScale());
		int HEIGHT = (int) (1024 * graphicsConfiguration.getScale());

		// Create the window
		openGLWindow = glfwCreateWindow(WIDTH, HEIGHT, "Nomad", NULL, NULL);
		if (openGLWindow == NULL) {
			System.err.println("Failed to create the GLFW window");
			System.err.println("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));
			System.err.println("OpenGL shader version: " + GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION));
			System.exit(-1);
		}
		// Get the resolution of the primary monitor
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		// Center our window
		glfwSetWindowPos(openGLWindow, (vidmode.width() - WIDTH) / 2, (vidmode.height() - HEIGHT) / 2);

		// Make the OpenGL context current
		glfwMakeContextCurrent(openGLWindow);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(openGLWindow);

		Keyboard.setWindow(openGLWindow);
		GL.createCapabilities();

		FontSupport.getInstance();

		LoadShaders();

		m_gman = new Universe();
		Universe universe = (Universe) m_gman;

		int[] var = new int[5];
		var[0] = m_tintvar0;
		var[1] = m_viewvar0;
		var[2] = m_objvar0;
		var[3] = m_viewvar1;
		var[4] = m_objvar1;

		SceneBase.setVariables(var);
		m_currentscene = new Menu(var);

		//	universe.Newgame();
		//		universe.Newgame();
		//		Spaceship ships[]=new Spaceship[1];
		//		ships[0]=new Spaceship("sloop",2,2,ShipState.SPACE);
		//
		//		Document doc=ParserHelper.LoadXML("assets/data/shipControllers/pirateSloop.xml");
		//		//read through the top level nodes
		//		Element root=doc.getDocumentElement();
		//
		//		ships[0].setShipController(new NpcShipController(root));
		//		m_currentscene = new SpaceEncounter((Spaceship)universe.getCurrentEntity(), ships);


		GL11.glDepthFunc(GL_LEQUAL);
		mouseInput = new MouseHook(openGLWindow);
		GLFW.glfwSetScrollCallback(openGLWindow, mouseInput.getScroll());
		glfwSetCursorPosCallback(openGLWindow, mouseInput);
		GL11.glDepthFunc(GL_LEQUAL);
		m_currentscene.start(mouseInput);

		// load font
		m_lastframe = GLFW.glfwGetTime();
		glViewport(0, 0, WIDTH, HEIGHT);

		glEnable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		glfwSetCharCallback(openGLWindow, input.Keyboard.getInstance());

		SharedGUIResources.getInstance().initialize();
	}

	private int loadShader(String filename, int type) {
		StringBuilder shaderSource = new StringBuilder();
		int shaderID = 0;

		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = reader.readLine()) != null) {
				shaderSource.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Could not read file.");
			e.printStackTrace();
			System.exit(-1);
		}

		shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);

		if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.err.println("Could not compile shader.");
			System.err.println("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));
			System.err.println("OpenGL shader version: " + GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION));
			System.exit(-1);
		}

		return shaderID;
	}

	public void LoadShaders() {
		m_vshader = this.loadShader("assets/shaders/shaderv.txt", GL20.GL_VERTEX_SHADER);
		m_fshader = this.loadShader("assets/shaders/shaderf.txt", GL20.GL_FRAGMENT_SHADER);
		m_pshader = GL20.glCreateProgram();
		System.err.println("system debug primary shader program id is " + m_pshader);
		GL20.glAttachShader(m_pshader, m_vshader);
		GL20.glAttachShader(m_pshader, m_fshader);

		GL20.glBindAttribLocation(m_pshader, 0, "in_Position");
		GL20.glBindAttribLocation(m_pshader, 1, "in_TextureCoord");
		GL20.glLinkProgram(m_pshader);
		GL20.glValidateProgram(m_pshader);

		m_tintvar0 = GL20.glGetUniformLocation(m_pshader, "in_color");
		m_viewvar0 = GL20.glGetUniformLocation(m_pshader, "projMatrix");
		m_objvar0 = GL20.glGetUniformLocation(m_pshader, "modelMatrix");

		m_vshadowshader = this.loadShader("assets/shaders/shadowshaderv.txt", GL20.GL_VERTEX_SHADER);
		m_fshadowshader = this.loadShader("assets/shaders/shadowshaderf.txt", GL20.GL_FRAGMENT_SHADER);
		m_pshadowshader = GL20.glCreateProgram();
		System.err.println("system debug shadow shader program id is " + m_pshadowshader);
		GL20.glAttachShader(m_pshadowshader, m_vshadowshader);
		GL20.glAttachShader(m_pshadowshader, m_fshadowshader);

		GL20.glBindAttribLocation(m_pshadowshader, 0, "in_Position");
		GL20.glBindAttribLocation(m_pshadowshader, 1, "in_TextureCoord");
		GL20.glBindAttribLocation(m_pshadowshader, 2, "in_Light");
		GL20.glLinkProgram(m_pshadowshader);
		GL20.glValidateProgram(m_pshadowshader);
		m_viewvar1 = GL20.glGetUniformLocation(m_pshadowshader, "projMatrix");
		m_objvar1 = GL20.glGetUniformLocation(m_pshadowshader, "modelMatrix");

	}

	public void End() {

		Callbacks.glfwFreeCallbacks(openGLWindow);
		// delete shader
		GL20.glUseProgram(0);
		GL20.glDetachShader(m_pshader, m_vshader);
		GL20.glDetachShader(m_pshader, m_fshader);
		GL20.glDeleteShader(m_vshader);
		GL20.glDeleteShader(m_fshader);
		GL20.glDeleteProgram(m_pshader);

		GL20.glUseProgram(0);
		GL20.glDetachShader(m_pshadowshader, m_vshadowshader);
		GL20.glDetachShader(m_pshadowshader, m_fshadowshader);
		GL20.glDeleteShader(m_vshadowshader);
		GL20.glDeleteShader(m_fshadowshader);
		GL20.glDeleteProgram(m_pshadowshader);
		// clean up
		// m_mouse.Discard();
		m_currentscene.end();
		glfwDestroyWindow(openGLWindow);

		glfwTerminate();
		errorCallback.close();
		FontSupport.discard();
		SharedGUIResources.getInstance().discard();
	}

	public void Run() {

		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		// Set the clear color
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		while (!glfwWindowShouldClose(openGLWindow)) {

			double t = GLFW.glfwGetTime();
			double dt = t - m_lastframe;
			if (dt > 0.016F) {
				glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the
				// framebuffer
				Update(dt);
				m_lastframe = t;
				glfwSwapBuffers(openGLWindow); // swap the color buffers
			} else {
				Thread.yield();
			}

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}

	}

	public void Update(double dt) {
		// update
		m_currentscene.Update((float) dt);
		glfwPollEvents();
		mouseInput.update();
		// render
		Draw();
	}

	void Draw() {

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL20.glUseProgram(m_pshader);

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_BLEND);

		m_currentscene.Draw();

		GL20.glUseProgram(0);

	}

	@Override
	public void SwapScene(Scene scene) {
		// TODO Auto-generated method stub
		m_currentscene.end();
		m_currentscene = scene;
		mouseInput.Clean();
		m_currentscene.start(mouseInput);
	}

}
