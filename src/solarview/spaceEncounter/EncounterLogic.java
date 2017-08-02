package solarview.spaceEncounter;

import solarview.spaceEncounter.EncounterEntities.EncounterShip;
import solarview.spaceEncounter.effectHandling.EffectHandler;
import solarview.spaceEncounter.rendering.TrailControl;
import spaceship.ShipController.scriptEvents;
import spaceship.stats.SpaceshipAnalyzer;

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
		testVictory();
	}
	
	private void testVictory()
	{
		if (shipList[0].getShip().getShipStats().getResource("HULL").getResourceAmount()<=0)
		{
			resolveDefeat();
		}
		if (shipList[1].getShip().getShipStats().getResource("HULL").getResourceAmount()<=0)
		{
			resolveVictory();
		}
	}
	
	private void resolveDefeat()
	{
		decomposeStats();
		shipList[1].getShip().getShipController().event(scriptEvents.victory);
	}
	
	private void resolveVictory()
	{
		decomposeStats();
		shipList[1].getShip().getShipController().event(scriptEvents.loss);
	}

	private void decomposeStats()
	{
		for (int i=1;i<shipList.length;i++)
		{
			shipList[i].getShip().getShipStats().runDecompose();
			new SpaceshipAnalyzer().decomposeResources(shipList[i].getShip().getShipStats(), 
					shipList[i].getShip());
			shipList[i].getShip().setShipStats(null);
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
