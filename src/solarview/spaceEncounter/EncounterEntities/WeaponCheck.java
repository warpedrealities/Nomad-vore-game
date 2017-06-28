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
		
		p.rotate((facing+heading)* -0.785398F);

		return p;
	}
	
	static double angle(float x, float y)
	{
		double d=Math.atan2(x,y);
		
		return d;
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
		
		double width=0.785398F*weapon.getWeapon().getWeapon().getFiringArc()/2;
		
		double r=angle(v.x-w.x,v.y-w.y);
		
		if (r<=width)
		{
			return true;
		}
		return false;
	}
	
	
}
