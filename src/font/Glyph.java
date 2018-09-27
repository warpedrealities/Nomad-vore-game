package font;

import shared.Vec2f;

public class Glyph {
	private Vec2f corner;
	private Vec2f size;
	private Vec2f offset;
	private float advanceX;

	public Glyph(Vec2f corner, Vec2f size, Vec2f offset, float advance) {
		this.corner = corner;
		this.size = size;
		this.offset = offset;
		this.advanceX = advance;
	}

	public Vec2f getCorner() {
		return corner;
	}

	public Vec2f getSize() {
		return size;
	}

	public Vec2f getOffset() {
		return offset;
	}

	public float getAdvanceX() {
		return advanceX;
	}

}
