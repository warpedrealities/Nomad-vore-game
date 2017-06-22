package graphics;

import rendering.SquareRenderer;
import shared.Vec2f;

public class FX_projectile extends FX {

	private Vec2f velocity;

	public FX_projectile(int index, Vec2f position, Vec2f velocity, float r, float g, float b) {
		super(index, position, r, g, b);
		this.velocity = new Vec2f(velocity.x / 20, velocity.y / 20);

	}

	@Override
	public void update(SquareRenderer square) {
		position.x += velocity.x;
		position.y += velocity.y;
		square.reposition(position);
		this.lifeSpan--;
	}
}
