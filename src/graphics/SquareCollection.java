package graphics;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector4f;

import rendering.SquareRenderer;
import rendering.Square_Int;
import shared.Vec2f;

public class SquareCollection implements Square_Int {

	SquareRenderer square;
	private List<Vec2f> positions;
	
	public SquareCollection()
	{
		square=new SquareRenderer(255, new Vec2f(0, 0), new Vector4f(1, 0, 0, 1));
	}
	
	@Override
	public void reposition(Vec2f p) {
		// TODO Auto-generated method stub

	}

	@Override
	public void repositionF(Vec2f p) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setVisible(boolean visible) {
		square.setVisible(visible);
	}

	@Override
	public void setFlashing(int bool) {
		square.setFlashing(bool);
	}

	@Override
	public boolean getVisible() {
		return square.getVisible();
	}

	@Override
	public void setImage(int value) {
		square.setImage(value);
	}

	@Override
	public void setColour(float r, float g, float b) {
		square.setColour(r, g, b);
	}

	@Override
	public void draw(int objmatrix, int tintvar, FloatBuffer matrix44fbuffer) {
		for (int i=0;i<positions.size();i++)
		{
			square.reposition(positions.get(i));
			square.draw(objmatrix, tintvar, matrix44fbuffer);
		}
	}

	@Override
	public void discard() {
		square.discard();
	}

	public void setPositions(List<Vec2f> positions) {
		this.positions=positions;
	}

}
