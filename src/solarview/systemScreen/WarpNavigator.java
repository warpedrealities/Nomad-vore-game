package solarview.systemScreen;

import nomad.StarSystem;
import nomad.universe.Universe;
import shared.Geometry;
import shared.Vec2f;
import shared.Vec2i;
import spaceship.Spaceship;
public class WarpNavigator {

	public WarpNavigator()
	{
		
	}
	
	public double getAngle(float x0, float y0, float x1, float y1)
	{	
		Vec2f p=new Vec2f(y0-y1,x0-x1);
		p.normalize();
		double angle = Math.atan2(p.y,p.x);
		if (angle<0)
		{
			angle=(Math.PI*2)+angle;
		}
		angle=angle/0.785398F;
		
		return angle;
	}

	public int calcFacing(Vec2f position) {
		float angle=(float)getAngle(position.x,position.y,0,1);
		int dir=Math.round(angle);
		if (dir>7)
		{
			dir=dir-8;
		}
		return dir;
	}	
	public Vec2i calculateDestination(int dir)
	{
		//calc angle

		Vec2i coordinates=Universe.getInstance().getSystem().getPosition();
		Vec2i p=Geometry.getPos(dir,new Vec2i(0,0));
		p.y=p.y*-1;
		p.add(coordinates);
		StarSystem system=Universe.getInstance().getSystem(p.x, p.y);
		if (system!=null)
		{
			return system.getPosition();
		}
		return null;	
	}
	
	public float calculateStress(Vec2f position)
	{
		float distance=position.getDistance(new Vec2f(0,0));
		float d=40-distance; if (d<3) {d=3;}
		double s=Math.sqrt((double)d);
		if (s<1)
		{
			s=1;
		}
		s=s/((Spaceship)Universe.getInstance().getCurrentEntity()).getShipStats().getFTL();
		return (float) s;
	}

}
