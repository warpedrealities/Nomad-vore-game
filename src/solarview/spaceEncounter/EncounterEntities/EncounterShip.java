package solarview.spaceEncounter.EncounterEntities;

import rendering.Square_Rotatable_Int;
import shared.Vec2f;
import spaceship.Spaceship;

public class EncounterShip {

	private Spaceship ship;
	private Vec2f position;
	private float heading;
	private Square_Rotatable_Int sprite;
	
	public EncounterShip(Spaceship ship)
	{
		this.ship=ship;
	}
	
	
	
}
