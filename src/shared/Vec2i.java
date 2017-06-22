package shared;

public class Vec2i {

	public int x;
	public int y;

	public Vec2i(int xin, int yin) {
		x = xin;
		y = yin;
	}

	public float getDistance(Vec2i p) {
		float xe = Math.abs(x - p.x);
		float ye = Math.abs(y - p.y);
		xe = xe * xe;
		ye = ye * ye;

		return (float) Math.sqrt(xe + ye);
	}

	public float getDistance(Vec2f p) {
		float xe = Math.abs(x - p.x);
		float ye = Math.abs(y - p.y);
		xe = xe * xe;
		ye = ye * ye;
		float sqrt = (float) Math.sqrt(xe + ye);
		return sqrt;
	}
}
