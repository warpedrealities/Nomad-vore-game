package rendering;

import java.nio.FloatBuffer;

import shared.Vec2f;

public interface Square_Int {
	public void reposition(Vec2f p);

	public void repositionF(Vec2f p);

	public void setVisible(boolean visible);

	public void setFlashing(int bool);

	public void setImage(int value);
	
	public void setColour(float r, float g, float b);
	
	public void draw(int objmatrix, int tintvar, FloatBuffer matrix44fbuffer);
	
	public void discard();
}
