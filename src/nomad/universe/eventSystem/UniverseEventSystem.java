package nomad.universe.eventSystem;

import nomad.universe.Universe;
import spaceship.Spaceship;

public class UniverseEventSystem {
	private SosEvents fuelEvents;
	private int timer;
	public UniverseEventSystem() {
		timer = 0;
		fuelEvents = new SosEvents();
	}

	public void update(Universe universe, long clock) {
		timer += clock;
		if (timer > 100) {
			if (Spaceship.class.isInstance(universe.getCurrentEntity())) {
				fuelEvents.update((Spaceship) universe.getCurrentEntity());
			}
			timer = 0;
		}

	}

	public void reset() {
		fuelEvents.reset();
		timer = 0;
	}
}
