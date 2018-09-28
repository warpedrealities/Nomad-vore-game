package shared;

public class Geometry {

	
	static public double getAngle(float x0, float y0, float x1, float y1)
	{	
		Vec2f p=new Vec2f(y1-y0,x0-x1);
		p.normalize();
		double angle = Math.atan2(p.y,p.x);
		if (angle<0)
		{
			angle=(Math.PI*2)+angle;
		}
		angle=angle/0.785398F;
		
		return angle;
	}
	
	public static Vec2f getPos(int i, Vec2f p) {
		switch (i) {
		case 0:
			return new Vec2f(p.x, p.y + 1);
		case 1:
			return new Vec2f(p.x + 1, p.y + 1);
		case 2:
			return new Vec2f(p.x + 1, p.y);
		case 3:
			return new Vec2f(p.x + 1, p.y - 1);
		case 4:
			return new Vec2f(p.x, p.y - 1);
		case 5:
			return new Vec2f(p.x - 1, p.y - 1);
		case 6:
			return new Vec2f(p.x - 1, p.y);
		case 7:
			return new Vec2f(p.x - 1, p.y + 1);
		}
		return p;
	}

	public static Vec2i getPos(int i, Vec2i p) {
		switch (i) {
		case 0:
			return new Vec2i(p.x, p.y + 1);
		case 1:
			return new Vec2i(p.x + 1, p.y + 1);
		case 2:
			return new Vec2i(p.x + 1, p.y);
		case 3:
			return new Vec2i(p.x + 1, p.y - 1);
		case 4:
			return new Vec2i(p.x, p.y - 1);
		case 5:
			return new Vec2i(p.x - 1, p.y - 1);
		case 6:
			return new Vec2i(p.x - 1, p.y);
		case 7:
			return new Vec2i(p.x - 1, p.y + 1);
		}
		return p;
	}
	
	public static Vec2i getPos(int i, int x, int y) {
		switch (i) {
		case 0:
			return new Vec2i(x, y + 1);
		case 1:
			return new Vec2i(x + 1, y + 1);
		case 2:
			return new Vec2i(x + 1, y);
		case 3:
			return new Vec2i(x + 1, y - 1);
		case 4:
			return new Vec2i(x, y - 1);
		case 5:
			return new Vec2i(x - 1, y - 1);
		case 6:
			return new Vec2i(x - 1, y);
		case 7:
			return new Vec2i(x - 1, y + 1);
		}
		return null;
	}
	
	public static int getDirection(Vec2f o, Vec2f p) {
		int d = 0;
		if (o.x > p.x) {
			d = d + 1; // east
		}
		if (o.x < p.x) {
			d = d + 2;// west
		}
		if (o.y > p.y) {
			d = d + 4; // south
		}
		if (o.y < p.y) {
			d = d + 8; // north
		}
		switch (d) {

		case 2:
			return 2;
		case 1:
			return 6;
		case 4:
			return 4;
		case 5:
			return 5;
		case 6:
			return 3;
		case 8:
			return 0;
		case 9:
			return 7;
		case 10:
			return 1;
		}

		return -1;
	}
}
