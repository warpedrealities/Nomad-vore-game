package gui;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import shared.Tools;

public class SharedGUIResources {

	private static SharedGUIResources instance;
	private int[] textures;
	public static SharedGUIResources getInstance()
	{
		if (instance==null)
		{
			instance=new SharedGUIResources();
		}
		return instance;
	}
	
	public void initialize()
	{
		textures=new int[1];
		textures[0]=Tools.loadPNGTexture("assets/art/slider.png", GL13.GL_TEXTURE0);	
		
	}
	
	public int getTexture(int id)
	{
		return textures[id];
	}
	
	public void discard()
	{
		for (int i=0;i<textures.length;i++)
		{
			GL11.glDeleteTextures(textures[i]);		
		}
	}
	

}
