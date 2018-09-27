package solarview.spaceEncounter.rendering;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import rendering.SpriteRotatable;
import shared.Tools;
import shared.Vec2f;

public class Targeting {

	private int texture;
	private SpriteRotatable sprite;
	private boolean visible;
	private float rotation;
	
	public Targeting()
	{
		texture = Tools.loadPNGTexture("assets/art/encounter/target.png", GL13.GL_TEXTURE0);
		sprite=new SpriteRotatable(new Vec2f(0,0),1);
		sprite.setSpriteSize(4);
		sprite.setCentered(true);
	}
	
	public void draw(int viewMatrix, int objmatrix, int tintvar, FloatBuffer matrix44Buffer) 
	{
		if (visible)
		{
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);	
			sprite.draw(objmatrix, tintvar, matrix44Buffer);		
		}
	}
	
	public void update(float dt)
	{
		rotation+=dt;
		if (rotation>16)
		{
			rotation-=16;
		}
		sprite.setFacing(rotation);
		
	}
	
	public void setPosition(Vec2f p)
	{
		sprite.repositionF(p);
	}
	
	public void discard()
	{
		sprite.discard();
		GL11.glDeleteTextures(texture);
	}
	
	public void setVisible(boolean v)
	{
		visible=v;
	}
}
