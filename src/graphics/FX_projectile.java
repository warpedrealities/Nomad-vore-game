package graphics;

import rendering.Square_Int;
import shared.Vec2f;

public class FX_projectile extends FX {

	private Vec2f velocity;
	private Vec2f target;
	private boolean hit;
	public FX_projectile(int index, Vec2f position, Vec2f velocity, float r, float g, float b, boolean hit) {
		super(index, position, r, g, b,10);
		this.velocity = velocity;
		this.hit=hit;
		target=new Vec2f(velocity.x+position.x,velocity.y+position.y);
	}

	@Override
	public void update(Square_Int[] squares) {
		if (hit)
		{
			squares[0].setVisible(true);
			squares[0].reposition(target);		
		}
		squares[2].setVisible(true);
		squares[2].reposition(position);
		squares[2].repositionF(velocity);	
		this.lifeSpan--;
		if (this.lifeSpan==0)
		{
			squares[2].repositionF(null);
		}
	}
}
