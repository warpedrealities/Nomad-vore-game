package solarview;

import nomad.StarSystem;
import nomad.Universe;

public class SolarController {

	StarSystem currentSystem;

	public SolarController(StarSystem system) {
		currentSystem = system;
	}

	public void update() {
		Universe.AddClock(1);
		for (int i = 0; i < currentSystem.getEntities().size(); i++) {
			currentSystem.getEntities().get(i).update();
		}

	}

}
