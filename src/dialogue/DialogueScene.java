package dialogue;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import gui.TextView;
import input.MouseHook;
import nomad.Universe;
import shared.Callback;
import shared.SceneBase;
import shared.Scene_Int;
import shared.Screen;
import shared.Tools;
import shared.Vec2f;
import solarview.SolarScene;
import spaceship.Spaceship;
import view.ViewScene;
import vmo.Game;

public class DialogueScene extends SceneBase implements Callback,Scene_Int {

	public enum dialogueOrigin{View,Space};
	String filename;
	Screen screen;
	dialogueOrigin origin;
	MouseHook m_hook;
	TextView m_text;
	
	Spaceship ship;
	
	public DialogueScene(String filename,dialogueOrigin origin)
	{
		this.origin=origin;
		this.filename=filename;
		SetupTextures();
		m_text = new TextView(m_textureIds[0], new Vec2f(-20, -16.0F), new Vec2f(40, 15), SceneBase.getVariables()[0]);
		
	}
	void SetupTextures() {
		m_textureIds = new int[9];
		// first is square
		// 2nd is font
		m_textureIds[0] = Tools.loadPNGTexture("assets/art/ninepatchblack.png", GL13.GL_TEXTURE0);
		m_textureIds[1] = Tools.loadPNGTexture("assets/art/font2.png", GL13.GL_TEXTURE0);
		m_textureIds[2] = Tools.loadPNGTexture("assets/art/spritesheet.png", GL13.GL_TEXTURE0);
		m_textureIds[4] = Tools.loadPNGTexture("assets/art/window.png", GL13.GL_TEXTURE0);
		m_textureIds[5] = Tools.loadPNGTexture("assets/art/widgets.png", GL13.GL_TEXTURE0);
		m_textureIds[6] = Tools.loadPNGTexture("assets/art/bars.png", GL13.GL_TEXTURE0);
		m_textureIds[7] = Tools.loadPNGTexture("assets/art/button0.png", GL13.GL_TEXTURE0);
		m_textureIds[8] = Tools.loadPNGTexture("assets/art/button1.png", GL13.GL_TEXTURE0);
	}
	@Override
	public void Update(float dt) {
		if (screen!=null)
		{
			screen.update(dt);	
		}
		else
		{
			switch (origin)
			{
			case Space:
				Game.sceneManager.SwapScene(new SolarScene(0,(Spaceship)Universe.getInstance().getCurrentEntity()));
				break;
			case View:
				Game.sceneManager.SwapScene(new ViewScene());
				break;
			}			
		}
	}

	
	@Override
	public void Draw() {
		GL20.glUseProgram(Game.m_pshader);
		DrawSetup();
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
			screen.draw(matrix44Buffer, SceneBase.getVariables()[2]);
	}

	@Override
	public void start(MouseHook mouse) {
		m_hook=mouse;
		DialogueScreen scr = new DialogueScreen(m_textureIds[0], m_textureIds[7], m_textureIds[8],
				SceneBase.getVariables()[0], Universe.getInstance().getPlayer(), m_text,this);
		scr.Load(filename, null,this);
		screen=scr;
		scr.setSpaceship(ship);
		scr.start(mouse);
	}

	@Override
	public void end() {
		m_text.discard();
		CleanTextures();
		screen.discard(m_hook);
		m_hook.Clean();
	}
	@Override
	public void Callback() {
		// TODO Auto-generated method stub
		switch (origin)
		{
		case Space:
			Game.sceneManager.SwapScene(new SolarScene(0,(Spaceship)Universe.getInstance().getCurrentEntity()));
			break;
		case View:
			Game.sceneManager.SwapScene(new ViewScene());
			break;
		}
	}
	public void setShip(Spaceship ship) {
		this.ship=ship;
	}
	@Override
	public void replaceScreen(Screen screen) {
		if (this.screen != null) {
			this.screen.discard(m_hook);
			this.screen = null;
		}
		int values[] = new int[5];
		values[0] = m_textureIds[6];
		values[1] = m_textureIds[0];
		values[2] = m_textureIds[7];
		values[3] = m_textureIds[8];
		values[4] = m_variables[0];
		screen.initialize(values, this);
		this.screen = screen;

		this.screen.start(m_hook);	
	}

}
