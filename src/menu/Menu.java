package menu;

import gui.Button;
import gui.Button2;
import gui.Text;
import gui.Window;
import input.MouseHook;
import nomad.Universe;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import chargen.PlayerStartScene;

import particlesystem.ParticleEmitter;
import preloader.PreloadReport;
import shared.Callback;
import shared.MyListener;
import shared.SceneBase;
import shared.Screen;
import shared.Sprite;
import shared.Tools;
import shared.Vec2f;
import view.ViewScene;
import vmo.Config;
import vmo.Game;
import vmo.GameManager;

public class Menu extends SceneBase implements MyListener, Callback {

	Matrix4f m_GUImatrix;
	Sprite m_sprite;
	Window m_window;
	ParticleEmitter m_emitter;
	float m_stagger;
	Screen m_screen;
	MouseHook m_hook;
	Text version;
	PreloadReport preload;

	public Menu(int[] variables) {
		super(variables);
		if (Universe.getInstance() == null) {
			preload = new PreloadReport();
			preload.run();
		}

		SetupTextures();
		m_GUImatrix = new Matrix4f();
		m_GUImatrix.m00 = 0.05F;
		m_GUImatrix.m11 = 0.0625F;
		m_GUImatrix.m22 = 1.0F;
		m_GUImatrix.m33 = 1.0F;
		m_GUImatrix.m31 = 0;
		m_GUImatrix.m32 = 0;
		m_stagger = 0.1F;
		// build logo
		m_sprite = new Sprite("logo", new Vec2f(-5, 0), 10);
		// build window
		m_window = new Window(new Vec2f(-6, -12), new Vec2f(12, 11), m_textureIds[0], true);
		// build buttons
		for (int i = 0; i < 5; i++) {
			String str = null;
			switch (i) {
			case 0:
				str = "Continue";
				break;
			case 1:
				str = "New game";
				break;
			case 2:
				str = "Save";
				break;
			case 3:
				str = "Load";
				break;
			case 4:
				str = "options";
			}
			Button button = new Button(new Vec2f(0.5F, 0.5F + 8 - (2 * i)), new Vec2f(11, 2), m_textureIds[2], this,
					str, i);

			m_window.add(button);
		}
		m_emitter = new ParticleEmitter(128, "particle", new Vec2f(0, 4), 10, 0.5F);
		version = new Text(new Vec2f(14, -14), "version:" + Config.VERSION, SceneBase.getVariables()[0]);
	}

	void SetupTextures() {
		m_textureIds = new int[3];
		// first is square
		// 2nd is font
		m_textureIds[0] = Tools.loadPNGTexture("assets/art/ninepatchblack.png", GL13.GL_TEXTURE0);
		m_textureIds[1] = Tools.loadPNGTexture("assets/art/font2.png", GL13.GL_TEXTURE0);
		m_textureIds[2] = Tools.loadPNGTexture("assets/art/button.png", GL13.GL_TEXTURE0);

	}

	@Override
	public void Update(float dt) {
		// TODO Auto-generated method stub
		m_emitter.Update(dt);
		if (m_stagger > 0) {

			m_stagger -= dt;

			if (m_stagger <= 0) {
				int r = GameManager.m_random.nextInt(16) - 10;
				if (r > 0) {
					m_emitter.SpawnParticles(r);
				}
				m_stagger = 0.1F;
			}
		}
		if (m_screen != null) {
			m_screen.update(dt);
		}
	}

	@Override
	public void Draw() {

		DrawSetup();
		GL20.glUseProgram(Game.m_pshader);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		m_GUImatrix.store(matrix44Buffer);
		matrix44Buffer.flip();
		GL20.glUniformMatrix4fv(m_variables[1], false, matrix44Buffer);

		m_emitter.draw(matrix44Buffer, SceneBase.getVariables()[2], m_variables[0]);
		GL20.glUniform4f(m_variables[0], 1.0F, 1.0F, 1.0F, 1);
		m_sprite.Draw(SceneBase.getVariables()[2], 0, matrix44Buffer);
		m_window.Draw(matrix44Buffer, SceneBase.getVariables()[2]);
		if (m_screen != null) {
			m_screen.draw(matrix44Buffer, SceneBase.getVariables()[2]);
		}
		version.Draw(matrix44Buffer, SceneBase.getVariables()[2]);
	}

	@Override
	public void start(MouseHook mouse) {
		mouse.Register(m_window);
		m_hook = mouse;
	}

	@Override
	public void end() {

		CleanTextures();
		m_window.discard();
		m_sprite.Discard();
		m_emitter.Discard();
		if (m_screen != null) {
			m_screen.discard(m_hook);
		}
		version.discard();
	}

	void Save() {
		if (Universe.getInstance().getPlaying() == true) {
			m_screen = new SaveLoad(m_textureIds[1], m_textureIds[0], m_textureIds[2], m_variables[0], true, this);
			m_screen.start(m_hook);
		}
	}

	void Load() {
		m_screen = new SaveLoad(m_textureIds[1], m_textureIds[0], m_textureIds[2], m_variables[0], false, this);
		m_screen.start(m_hook);

	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {

		if (m_screen == null && (preload == null || preload.isComplete())) {
			switch (ID) {
			case 0:
				// continue
				if (Universe.getInstance().getPlaying() == true) {
					Game.sceneManager.SwapScene(new ViewScene(SceneBase.getVariables(), Universe.getInstance()));
				}
				break;
			case 1:
				// new game
				Universe.getInstance().Newgame();
				Game.sceneManager.SwapScene(new PlayerStartScene(Universe.getInstance().getPlayer()));
				// Game.sceneManager.SwapScene(new
				// ViewScene(SceneBase.getVariables(),Universe.getInstance()));
				break;
			case 2:
				// save
				Save();
				break;
			case 3:
				// load
				Load();
				break;

			case 4:
				// options
				options();
				break;
			}
		}

	}

	public void options() {
		m_screen = new OptionsScreen(m_textureIds[1], m_textureIds[0], m_textureIds[2], m_variables[0], true, this);
		m_screen.start(m_hook);
	}

	@Override
	public void Callback() {
		// TODO Auto-generated method stub
		if (m_screen != null) {
			m_screen.discard(m_hook);
			m_screen = null;
		}
	}

}
