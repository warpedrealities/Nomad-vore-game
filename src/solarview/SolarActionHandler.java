package solarview;

import nomad.Entity;
import nomad.Station;
import nomad.World;
import spaceship.Spaceship;
import spaceship.SpaceshipActionHandler;

public class SolarActionHandler {

	Spaceship ship;
	Entity target;
	
	public SolarActionHandler(Spaceship ship, Entity target)
	{
		this.ship=ship;
		this.target=target;
	}
	
	public void doAction()
	{
		if (target.getClass().getName().contains("World"))
		{
			new SpaceshipActionHandler().land(ship, (World)target);
		}
		if (target.getClass().getName().contains("Station"))
		{
			new SpaceshipActionHandler().dockStation(ship, (Station)target);
		}
		
	}
	
}
