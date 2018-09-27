package solarview.spaceEncounter.EncounterEntities;

import shared.Vec2f;

public class WeaponCheck {

	
	
	static public boolean checkRange(EncounterShip ship, EncounterShip target, CombatWeapon weapon)
	{
		float range=weapon.getWeapon().getWeapon().getMaxRange();
		
		float distance=ship.getPosition().getDistance(target.getPosition());
		
		if (range>=distance)
		{
			return true;
		}
		return false;
	}
	
	static private Vec2f vFromFacing(float facing, float heading)
	{
		Vec2f p=new Vec2f (0,1);
		float r=(facing+heading)* -0.785398F;
		p.rotate(r);

		return p;
	}
	
	static double angle(float x0, float y0,float x1,float y1)
	{
		double a=Math.atan2(x0,y0);
		double b=Math.atan2(x1, y1);
		
		double angle=a-b;
		if (angle>Math.PI)
		{
			angle=(Math.PI*-2)+angle;
		}
		if (angle<-Math.PI)
		{
			angle=(Math.PI*2)+angle;
		}
		return angle;
	}
	
	static public boolean checkArc(EncounterShip ship, EncounterShip target, CombatWeapon weapon)
	{
		
		//find the angle to the target
		Vec2f v=target.getPosition().replicate().subtract(ship.getPosition());
		//find the angle of the weapon mount
		Vec2f w=vFromFacing(weapon.getWeapon().getFacing(),ship.getHeading());
		//find if the difference in angles is smaller than the arc
		v.normalize();
		w.normalize();
		v.x=v.x*-1;
		
		double width=0.785398F*weapon.getWeapon().getWeapon().getFiringArc()/2;
		
		double r=angle(v.x,v.y,w.x,w.y);

		if (r<=width && r>=width*-1)
		{
			return true;
		}
		return false;
	}
	
	
}
