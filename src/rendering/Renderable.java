package rendering;

import java.nio.FloatBuffer;

public interface Renderable {

	boolean getVisible();

	void draw(int objmatrix, int tintvar, FloatBuffer matrix44fbuffer);

	void discard();
	
	void setSpriteBatch(SpriteBatch batch);

	SpriteBatch getBatch();
}
