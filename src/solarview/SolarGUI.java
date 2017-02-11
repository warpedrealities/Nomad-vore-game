package solarview;

import input.MouseHook;

import java.nio.FloatBuffer;

import gui.Button;
import gui.Text;
import gui.Window;

import nomad.Universe;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import actorRPG.Actor_RPG;

import shared.MyListener;
import shared.Screen;
import shared.Tools;
import shared.Vec2f;
import spaceship.Spaceship;

public class SolarGUI {

	Spaceship playerShip;
	int []textureIds;
	Matrix4f m_GUImatrix;
	Window window;
	Text [] resourceTexts;
	String [] resourceStrings;
	
	Text globalPosition;
	

	
	public SolarGUI()
	{
		m_GUImatrix=new Matrix4f();
		m_GUImatrix.m00=0.05F;
		m_GUImatrix.m11=0.0625F;
		m_GUImatrix.m22=1.0F;
		m_GUImatrix.m33=1.0F;		
		m_GUImatrix.m31=0; m_GUImatrix.m32=0;	
		
		setupTextures();	

	}

	public int [] getTextures()
	{
		return textureIds;
	}
	
	private void setupTextures()
	{
		textureIds=new int[2];
		textureIds[0]=Tools.loadPNGTexture("assets/art/ninepatchblack.png", GL13.GL_TEXTURE0);	
		textureIds[1]=Tools.loadPNGTexture("assets/art/button0.png", GL13.GL_TEXTURE0);
		//textureIds[1]=Tools.loadPNGTexture("assets/art/font2.png", GL13.GL_TEXTURE0);
	}
	
	public void generate(Spaceship ship, MyListener listener)
	{
		window=new Window(new Vec2f (11,-10), new Vec2f(9,26), textureIds[0],true);
		playerShip=ship;

		resourceStrings=playerShip.getShipStats().getResourceKeys();
		resourceTexts=new Text[resourceStrings.length];
		for (int i=0;i<resourceStrings.length;i++)
		{
			if (resourceStrings[i].equals("FOOD"))
			{
				resourceTexts[i]=new Text(
						new Vec2f(0.2F,12.5F-i),"FOOD:"+Universe.getInstance().getPlayer().getRPG().getStat(Actor_RPG.SATIATION)+"/"+
								+Universe.getInstance().getPlayer().getRPG().getStatMax(Actor_RPG.SATIATION), 0.8F, 0);	
			}
			else
			{
				resourceTexts[i]=new Text(
						new Vec2f(0.2F,12.5F-i),resourceStrings[i]+":"+playerShip.getShipStats().getResource(resourceStrings[i]).getResourceAmount()+
						"/"+playerShip.getShipStats().getResource(resourceStrings[i]).getResourceCap(), 0.8F, 0);			
			}

			window.add(resourceTexts[i]);
		}

		globalPosition=new Text(new Vec2f(0.2F,2.5F),"X:"+playerShip.getPosition().x+" Y:"+playerShip.getPosition().y,0.8F,0);
		window.add(globalPosition);
		
		//add buttons
		Button [] buttons=new Button[3];
		
		buttons[0]=new Button(new Vec2f(0.1F,0.1F), new Vec2f(8.8F,2), textureIds[1], listener, "exit", 0);
		buttons[1]=new Button(new Vec2f(4.6F,2.1F), new Vec2f(4.4F,2), textureIds[1], listener, "+", 1);
		buttons[2]=new Button(new Vec2f(0.1F,2.1F), new Vec2f(4.4F,2), textureIds[1], listener, "-", 2);
		
		for (int i=0;i<3;i++)
		{
			window.add(buttons[i]);
		}
	}
	
	public void draw(int viewMatrix,int objmatrix, int tintvar, FloatBuffer matrix44Buffer)
	{
		m_GUImatrix.store(matrix44Buffer); matrix44Buffer.flip();
		GL20.glUniformMatrix4fv(viewMatrix, false, matrix44Buffer);
		
		window.Draw(matrix44Buffer, objmatrix);
		
		
	}
	
	public void update()
	{
		for (int i=0;i<resourceStrings.length;i++)
		{
			if (resourceStrings[i].equals("FOOD"))
			{
				resourceTexts[i].setString("FOOD:"+Universe.getInstance().getPlayer().getRPG().getStat(Actor_RPG.SATIATION)+"/"+
								+Universe.getInstance().getPlayer().getRPG().getStatMax(Actor_RPG.SATIATION));
			}
			else
			{
				resourceTexts[i].setString(resourceStrings[i]+":"+playerShip.getShipStats().getResource(resourceStrings[i]).getResourceAmount()+
						"/"+playerShip.getShipStats().getResource(resourceStrings[i]).getResourceCap());			
			}
		
		}
	}
	
	public void start(MouseHook mouse)
	{
		mouse.Register(window);
	}
	
	public void discard(MouseHook mouse)
	{
		mouse.Remove(window);
		window.discard();
		for (int i=0;i<textureIds.length;i++)
		{
			GL11.glDeleteTextures(textureIds[i]);		
		}
	}
}
