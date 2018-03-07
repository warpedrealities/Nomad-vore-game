package graphics;

import rendering.Square_Int;
import shared.Vec2f;

public class FX_projectile extends FX {

	private Vec2f velocity;

	public FX_projectile(int index, Vec2f position, Vec2f velocity, float r, float g, float b) {
		super(index, position, r, g, b,5);
		this.velocity = velocity;
		
	}

	@Override
	public void update(Square_Int square) {
		square.reposition(position);
		square.repositionF(velocity);	
		this.lifeSpan--;
		if (this.lifeSpan==0)
		{
			square.repositionF(null);
		}
	}
}
