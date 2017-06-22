package solarview.spaceEncounter;

import solarview.spaceEncounter.EncounterEntities.EncounterShip;
import solarview.spaceEncounter.rendering.TrailControl;

public class EncounterLogic {

	private EncounterShip[] shipList;
	private float turn;
	private TrailControl trailControl;
	
	public EncounterLogic(EncounterShip[] ships) {
		shipList = ships;
	}

	public EncounterShip[] getShipList() {
		return shipList;
	}

	public void startTurn() {
		turn = 1;
	}

	public void update(float dt) {
		for (int i = 0; i < shipList.length; i++) {
			shipList[i].update(dt);
		}
		turn -= dt;
		if (turn <= 0) {
			turnEnd();
		}
		trailControl.update(dt);
	}

	private void turnEnd() {
		for (int i = 0; i < shipList.length; i++) {
			shipList[i].updateResources();
		}
	}

	public boolean isRunning() {
		if (turn <= 0) {
			return false;
		}
		return true;
	}

	public void setTrailControl(TrailControl trailControl) {
		this.trailControl = trailControl;
	}

}
