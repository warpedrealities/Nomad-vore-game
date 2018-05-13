package graphics;

import java.util.ArrayList;
import java.util.List;

import rendering.Square_Int;
import shared.Vec2f;

public class CompiledFX extends FX {

	private List<Vec2f> positions;
	
	
	public CompiledFX(int index, Vec2f position, float r, float g, float b) {
		super(index, null, r, g, b);
		index=3;
		lifeSpan=5;
		positions=new ArrayList<Vec2f>();
	}

	public CompiledFX() {
		positions=new ArrayList<Vec2f>();
		colour=new float[3];
		index=3;
		lifeSpan=5;
	}

	public void setRGB(float r, float g, float b) {
		colour[0]=r;
		colour[1]=g;
		colour[2]=b;
	}

	public void addPosition(Vec2f position) {
		positions.add(position);
	}
	
	public void initializeEffect(Square_Int square) {
		if (SquareCollection.class.isInstance(square))
		{
			SquareCollection sq=(SquareCollection)square;
			sq.setPositions(positions);
		}
	}
}
