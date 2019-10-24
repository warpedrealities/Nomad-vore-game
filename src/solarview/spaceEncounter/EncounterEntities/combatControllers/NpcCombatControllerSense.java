package solarview.spaceEncounter.EncounterEntities.combatControllers;

import shared.Vec2f;
import solarview.spaceEncounter.EncounterEntities.EncounterShipImpl;

public class NpcCombatControllerSense {

	private EncounterShipImpl []ships;
	private EncounterShipImpl controlledShip;
	public void setShips(EncounterShipImpl[] ships) {
		this.ships = ships;
	}
	public void setControlledShip(EncounterShipImpl controlledShip) {
		this.controlledShip = controlledShip;
	}
	public EncounterShipImpl[] getShips() {
		return ships;
	}
	public EncounterShipImpl getControlledShip() {
		return controlledShip;
	}
	
	public float getHeading()
	{
		return controlledShip.getHeading();
	}
	
	public double getAngle(float x0, float y0, float x1, float y1)
	{	
		Vec2f p=new Vec2f(y1-y0,x1-x0);
		p.normalize();
		double angle = Math.atan2(p.y,p.x);
		if (angle<0)
		{
			angle=(Math.PI*2)+angle;
		}
		angle=angle/57.2958F;
		return angle;
	}

	public float getDistanceTo(int index)
	{
		return ships[index].getPosition().getDistance(controlledShip.getPosition());
	}
	
	public float getAngleTo(int index)
	{
		double a=getAngle(controlledShip.getPosition().x,controlledShip.getPosition().y,
				ships[index].getPosition().x,ships[index].getPosition().y);
		return (float)a;
	}
	
}
