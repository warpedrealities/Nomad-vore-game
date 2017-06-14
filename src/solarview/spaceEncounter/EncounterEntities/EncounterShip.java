package solarview.spaceEncounter.EncounterEntities;

import rendering.Square_Rotatable_Int;
import shared.Vec2f;
import solarview.spaceEncounter.CombatController;
import spaceship.Spaceship;

public class EncounterShip {

	private Spaceship ship;
	private CombatController controller;
	private Vec2f position;
	private float heading;
	private Square_Rotatable_Int sprite;
	
	public EncounterShip(Spaceship ship, Vec2f position)
	{
		this.ship=ship;
		if (this.ship.getShipController()!=null)
		{
			this.controller=this.ship.getShipController().getCombat();			
		}

		this.position=new Vec2f(position.x,position.y);
	}

	public Spaceship getShip() {
		return ship;
	}

	public Square_Rotatable_Int getSprite() {
		return sprite;
	}

	public void setSprite(Square_Rotatable_Int sprite) {
		this.sprite = sprite;
	}

	public Vec2f getPosition() {
		return position;
	}

	public float getHeading() {
		return heading;
	}
	
	
	
}
