package solarview.spaceEncounter.rendering;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import rendering.CircleOverlay;
import shared.Tools;
import shared.Vec2f;

public class CircleHandler {

	private int texture;
	private CircleOverlay circle;
	private boolean visible;

	public CircleHandler()
	{
		texture = Tools.loadPNGTexture("assets/art/encounter/green.png", GL13.GL_TEXTURE0);
		circle=new CircleOverlay();
	}

	public void draw(int objmatrix, int tintvar, FloatBuffer matrix44fbuffer) {
		if (visible)
		{
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
			circle.draw(objmatrix, tintvar, matrix44fbuffer);
		}
	}

	public void setWidth(float w)
	{
		circle.regen(w);
	}
	public void discard()
	{
		circle.discard();
		GL11.glDeleteTextures(texture);
	}

	public void setPosition(Vec2f position) {
		circle.reposition(position);
	}

	public void setRotation(float angle)
	{
		circle.rotate(angle);
	}

	public void setVisible(boolean v)
	{
		visible=v;
	}
}
