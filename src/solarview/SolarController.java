package solarview;

import java.util.ArrayDeque;
import java.util.Queue;

import nomad.StarSystem;
import nomad.universe.Universe;
import solarview.hazardSystem.SolarHazardSystem;
import spaceship.Spaceship;

public class SolarController {

	StarSystem currentSystem;
	private Queue<String> messageQueue;
	private Spaceship ship;
	private SolarHazardSystem hazardSystem;
	
	public SolarController(StarSystem system, Spaceship ship) {
		currentSystem = system;
		messageQueue=new ArrayDeque<String>();
		this.ship=ship;
		hazardSystem=new SolarHazardSystem(ship,messageQueue);
	}

	public void update() {
		Universe.AddClock(1);
		for (int i = 0; i < currentSystem.getEntities().size(); i++) {
			currentSystem.getEntities().get(i).update();
		}
		hazardSystem.update();
	}

	public Queue<String> getMessageQueue() {
		return messageQueue;
	}

}
