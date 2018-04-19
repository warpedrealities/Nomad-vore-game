package nomad;

import java.util.ArrayList;

import gui.Button;
import gui.MultiLineText;
import gui.TextView;
import gui.Window;
import input.MouseHook;
import menu.Menu;
import nomad.universe.Universe;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import actor.Actor;
import actor.player.reformation.ReformationTool;
import shared.MyListener;
import shared.SceneBase;
import shared.Tools;
import shared.Vec2f;
import view.ViewScene;
import vmo.Game;

public class GameOver extends SceneBase implements MyListener {

	private Actor victor;
	TextView m_text;
	Window m_window;
	Matrix4f m_GUImatrix;
	MouseHook m_hook;
	ReformationTool tool;
	public GameOver(int[] variables, String text,Actor victor,boolean suppressReform) {
		super(variables);
		tool=new ReformationTool(Universe.getInstance().getCurrentEntity(),Universe.getInstance().getPlayer().getReformHandler());
		SetupTextures();
		this.victor=victor;
		if (victor!=null)
		{
			victor.getRPG().Heal(1);
		}
		m_window = new Window(new Vec2f(-20, -16), new Vec2f(40, 30), m_textureIds[0], true);

		m_text = new TextView(m_textureIds[3], new Vec2f(-20, -14.0F), new Vec2f(40, 15), SceneBase.getVariables()[0]);
		if (text != null) {
			m_text.AddText(text);
			m_text.BuildStrings();
		}
		m_window.add(m_text);
		m_GUImatrix = new Matrix4f();
		m_GUImatrix.m00 = 0.05F;
		m_GUImatrix.m11 = 0.0625F;
		m_GUImatrix.m22 = 1.0F;
		m_GUImatrix.m33 = 1.0F;
		m_GUImatrix.m31 = 0;
		m_GUImatrix.m32 = 0;
		Universe.universeInstance.GameOver();

		Button [] buttons = new Button[2];
		buttons[0]=new Button(new Vec2f(0.5F, 0.0F), new Vec2f(11, 2), m_textureIds[2], this, "file", 0);
		m_window.add(buttons[0]);
		if (!suppressReform && tool.getCanReform())
		{
			buttons[1]=new Button(new Vec2f(11.5F, 0.0F), new Vec2f(11, 2), m_textureIds[2], this, "reform", 0);
			m_window.add(buttons[1]);			
		}
	}

	public GameOver(int[] variables, String text, ArrayList<String> strings,Actor victor,boolean suppressReform) {
		super(variables);
		this.victor=victor;
		SetupTextures();
		tool=new ReformationTool(Universe.getInstance().getCurrentEntity(),Universe.getInstance().getPlayer().getReformHandler());
		m_window = new Window(new Vec2f(-20, -16), new Vec2f(40, 30), m_textureIds[0], true);

		m_text = new TextView(m_textureIds[3], new Vec2f(-20, -14.0F), new Vec2f(40, 15), SceneBase.getVariables()[0],
				strings);
		m_text.setExpanded(true);
		if (text != null) {
			m_text.AddText(text);
			m_text.BuildStrings();
		}
		m_window.add(m_text);
		m_GUImatrix = new Matrix4f();
		m_GUImatrix.m00 = 0.05F;
		m_GUImatrix.m11 = 0.0625F;
		m_GUImatrix.m22 = 1.0F;
		m_GUImatrix.m33 = 1.0F;
		m_GUImatrix.m31 = 0;
		m_GUImatrix.m32 = 0;
		Universe.universeInstance.GameOver();

		Button [] buttons = new Button[2];
		buttons[0]=new Button(new Vec2f(0.5F, 0.0F), new Vec2f(11, 2), m_textureIds[2], this, "file", 0);
		m_window.add(buttons[0]);
		if (!suppressReform && tool.getCanReform())
		{
			buttons[1]=new Button(new Vec2f(11.5F, 0.0F), new Vec2f(11, 2), m_textureIds[2], this, "reform", 1);
			m_window.add(buttons[1]);			
		}
	}

	public GameOver(String gameOver) {
		this(null,gameOver,null,false);
	}

	void SetupTextures() {
		m_textureIds = new int[4];
		// first is square
		// 2nd is font
		m_textureIds[0] = Tools.loadPNGTexture("assets/art/ninepatchblack.png", GL13.GL_TEXTURE0);
		m_textureIds[1] = Tools.loadPNGTexture("assets/art/font2.png", GL13.GL_TEXTURE0);
		m_textureIds[2] = Tools.loadPNGTexture("assets/art/button.png", GL13.GL_TEXTURE0);
		m_textureIds[3] = Tools.loadPNGTexture("assets/art/textWindow.png", GL13.GL_TEXTURE0);	
	}

	@Override
	public void Update(float dt) {
		m_window.update(dt);
		m_text.update(dt);
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
		GL20.glUniform4f(m_variables[0], 1.0F, 1.0F, 1.0F, 1);

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		m_window.Draw(matrix44Buffer, SceneBase.getVariables()[2]);
	}

	@Override
	public void start(MouseHook mouse) {
		mouse.Register(m_window);
		m_hook = mouse;
	}

	@Override
	public void end() {
		m_window.discard();
		m_hook.Remove(m_window);
	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		switch (ID) {
		case 0:
			Game.sceneManager.SwapScene(new Menu(SceneBase.getVariables()));
			break;
		case 1:
			tool.reform();
			m_text.AddText("You awaken naked in your reformation tube");
			if (victor!=null)
			{
				victor.getRPG().Heal(1);
			}
			Game.sceneManager.SwapScene(new ViewScene(m_text.getDisplayStrings()));
			break;
		}
	}

}
