package graphics;

import rendering.SpriteBeam;
import rendering.SquareRenderer;
import rendering.Square_Int;
import shared.Vec2f;

public class FX {

	protected Vec2f position;
	protected int index;

	protected float[] colour;

	public Vec2f getPosition() {
		return position;
	}

	public int getIndex() {
		return index;
	}

	public int getLifeSpan() {
		return lifeSpan;
	}

	protected int lifeSpan;

	public float getRed() {
		return colour[0];
	}

	public float getGreen() {
		return colour[1];
	}

	public float getBlue() {
		return colour[2];
	}

	public FX(int index, Vec2f position, float r, float g, float b) {
		this.colour = new float[3];
		this.colour[0] = r;
		this.colour[1] = g;
		this.colour[2] = b;
		this.position = position;
		this.index = index;
		lifeSpan = 10;
	}

	public FX(int index, Vec2f position, float r, float g, float b, int lifeSpan) {
		this.colour = new float[3];
		this.colour[0] = r;
		this.colour[1] = g;
		this.colour[2] = b;
		this.position = position;
		this.index = index;
		this.lifeSpan = lifeSpan;
	}

	public FX(int index, Vec2f position, float[] colour) {
		this.colour = colour;
		this.position = position;
		this.index = index;
		lifeSpan = 10;
	}

	public void update(Square_Int[] squares) {
		squares[index].setVisible(true);
		this.lifeSpan--;
	}

}
