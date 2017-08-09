package rendering;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL20;

import font.NuFont;
import shared.Vec2f;

public class SpriteFont extends NuFont implements Renderable {

	private float r,g,b;
	
	public SpriteFont(Vec2f pos, int maxlength, float s) {
		super(pos, maxlength, s);
	}

	public void setRGB(float r, float g, float b)
	{
		this.r=r;
		this.g=g;
		this.b=b;
	}
	
	@Override
	public boolean getVisible() {
		return true;
	}

	@Override
	public void draw(int objmatrix, int tintvar, FloatBuffer matrix44fbuffer) {
		// TODO Auto-generated method stub
		//use tintvar to select colour
		GL20.glUniform4f(tintvar, r, g, b, 1);
		super.Draw(matrix44fbuffer, objmatrix);
		GL20.glUniform4f(tintvar, 1, 1, 1, 1);
	}

	@Override
	public void discard() {
		// TODO Auto-generated method stub
		super.Discard();
	}

	@Override
	public void setSpriteBatch(SpriteBatch batch) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SpriteBatch getBatch() {
		// TODO Auto-generated method stub
		return null;
	}

}
