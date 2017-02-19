package nomad;

import java.util.ArrayList;

import gui.Button;
import gui.MultiLineText;
import gui.TextView;
import gui.Window;
import input.MouseHook;
import menu.Menu;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import shared.MyListener;
import shared.SceneBase;
import shared.Tools;
import shared.Vec2f;
import vmo.Game;

public class GameOver extends SceneBase implements MyListener{

	TextView m_text;
	Window m_window;
	Matrix4f m_GUImatrix;
	MouseHook m_hook;
	
	public GameOver(int[] variables,String text) {
		super(variables);
	
		SetupTextures();
		
		m_window=new Window(new Vec2f (-20,-16), new Vec2f(40,30), m_textureIds[0],true);

		
		m_text=new TextView(m_textureIds[0],new Vec2f(-20,-14.0F), new Vec2f(40,15), SceneBase.getVariables()[0]);
		if (text!=null)
		{
			m_text.AddText(text);
			m_text.BuildStrings();
		}
		m_window.add(m_text);
		m_GUImatrix=new Matrix4f();
		m_GUImatrix.m00=0.05F;
		m_GUImatrix.m11=0.0625F;
		m_GUImatrix.m22=1.0F;
		m_GUImatrix.m33=1.0F;		
		m_GUImatrix.m31=0; m_GUImatrix.m32=0;	
		Universe.universeInstance.GameOver();
		
		Button button=new Button(new Vec2f(0.5F,0.0F), new Vec2f(11,2), m_textureIds[2],this,"file", 0);
		m_window.add(button);
	}
	public GameOver(int[] variables,String text,ArrayList<String> strings) {
		super(variables);
		
		SetupTextures();
		
		m_window=new Window(new Vec2f (-20,-16), new Vec2f(40,30), m_textureIds[0],true);

		
		m_text=new TextView(m_textureIds[0],new Vec2f(-20,-14.0F), new Vec2f(40,15), SceneBase.getVariables()[0],strings);
		m_text.setExpanded(true);
		if (text!=null)
		{
			m_text.AddText(text);
			m_text.BuildStrings();
		}
		m_window.add(m_text);
		m_GUImatrix=new Matrix4f();
		m_GUImatrix.m00=0.05F;
		m_GUImatrix.m11=0.0625F;
		m_GUImatrix.m22=1.0F;
		m_GUImatrix.m33=1.0F;		
		m_GUImatrix.m31=0; m_GUImatrix.m32=0;	
		Universe.universeInstance.GameOver();
		
		Button button=new Button(new Vec2f(0.5F,0.0F), new Vec2f(11,2), m_textureIds[2],this,"file", 0);
		m_window.add(button);
	}

	
	void SetupTextures()
	{
		m_textureIds=new int[3];
		//first is square
		//2nd is font
		m_textureIds[0]=Tools.loadPNGTexture("assets/art/ninepatchblack.png", GL13.GL_TEXTURE0);	
		m_textureIds[1]=Tools.loadPNGTexture("assets/art/font2.png", GL13.GL_TEXTURE0);
		m_textureIds[2]=Tools.loadPNGTexture("assets/art/button.png", GL13.GL_TEXTURE0);
	}
	
	@Override
	public void Update(float dt) {
		// TODO Auto-generated method stub
		m_text.update(dt);
	}

	@Override
	public void Draw() {
		// TODO Auto-generated method stub
		DrawSetup();
		GL20.glUseProgram(Game.m_pshader);
		GL11.glDisable(GL11.GL_DEPTH_TEST); 
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		m_GUImatrix.store(matrix44Buffer); matrix44Buffer.flip();
		GL20.glUniformMatrix4fv(m_variables[1], false, matrix44Buffer);
		GL20.glUniform4f(m_variables[0],1.0F,1.0F,1.0F, 1);

		GL11.glEnable(GL11.GL_DEPTH_TEST); 
		m_window.Draw(matrix44Buffer, SceneBase.getVariables()[2]);
	}

	@Override
	public void start(MouseHook mouse) {
		// TODO Auto-generated method stub
		mouse.Register(m_window);
		m_hook=mouse;
	}

	@Override
	public void end() {
		// TODO Auto-generated method stub
		m_window.discard();
		m_hook.Remove(m_window);
	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		// TODO Auto-generated method stub
		switch (ID)
		{
			case 0:
			Game.sceneManager.SwapScene(new Menu(SceneBase.getVariables()));
			break;
		
		
		}
	}

	
	
}
