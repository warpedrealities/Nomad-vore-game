package solarview.spaceEncounter;

import solarview.spaceEncounter.EncounterEntities.EncounterShip;
import solarview.spaceEncounter.effectHandling.EffectHandler;
import solarview.spaceEncounter.rendering.TrailControl;

public class EncounterLogic {

	private EncounterShip[] shipList;
	private float turn;
	private TrailControl trailControl;
	private EffectHandler effectHandler;
	
	public EncounterLogic(EncounterShip[] ships) {
		shipList = ships;
		effectHandler=new EffectHandler();
	}

	public EncounterShip[] getShipList() {
		return shipList;
	}

	public void startTurn() {
		turn = 1;
	}

	public void update(float dt) {
		for (int i = 0; i < shipList.length; i++) {
			shipList[i].update(dt,effectHandler);
		}
		
		effectHandler.update(dt);
		trailControl.update(dt);
		
		turn -= dt;
		if (turn <= 0) {
			turnEnd();
		}

	}

	private void turnEnd() {
		for (int i = 0; i < shipList.length; i++) {
			shipList[i].updateResources(effectHandler);
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

	public EffectHandler getEffectHandler() {
		return effectHandler;
	}

}
