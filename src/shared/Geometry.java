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
}
