package solarview.hazardSystem;

import java.util.Queue;

import spaceship.Spaceship;

public abstract class Hazard_Base {

	public abstract void runHazard(Spaceship ship, Queue<String> queue);

}
