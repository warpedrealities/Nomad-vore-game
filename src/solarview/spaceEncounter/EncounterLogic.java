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
	public enum GameState{playing,victory,defeat,retreat};
	private GameState gameState;
	private EncounterWarpHandler warpHandler;
	
	public EncounterLogic(EncounterShip[] ships) {
		shipList = ships;
		effectHandler=new EffectHandler();
		gameState=GameState.playing;
		warpHandler=new EncounterWarpHandler(shipList[0]);
	}

	public EncounterShip[] getShipList() {
		return shipList;
	}

	public void startTurn() {
		if (warpHandler.getWarpLevel()<warpHandler.MAXIMUMCHARGE)
		{
			for (int i=0;i<shipList.length;i++)
			{
				shipList[i].runAi(shipList,effectHandler);
			}			
		}
		turn = 2;
	}

	public void update(float dt) {
		for (int i = 0; i < shipList.length; i++) {
			shipList[i].update(dt/2,effectHandler);
		}

		effectHandler.update(dt/2);
		trailControl.update(dt/2);
		
		turn -= dt;
		if (turn <= 0) {
			turnEnd();
		}

	}

	private void turnEnd() {
		warpHandler.update();
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
		
		if (warpHandler.getWarpLevel()>warpHandler.MAXIMUMCHARGE && warpHandler.isJumping())
		{
			resolveRetreat();
		}
	}
	
	private void resolveRetreat() {
		gameState=GameState.retreat;
	}

	private void resolveDefeat()
	{
		//decomposeStats();
		//shipList[1].getShip().getShipController().event(scriptEvents.victory);
		gameState=GameState.defeat;
	}
	
	private void resolveVictory()
	{
		//decomposeStats();
		//shipList[1].getShip().getShipController().event(scriptEvents.loss);
		gameState=GameState.victory;
	}

	public void end()
	{
		decomposeStats();
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

	public GameState getGameState() {
		return gameState;
	}

	public EncounterWarpHandler getWarpHandler() {
		return warpHandler;
	}

	public void start() {
		for (int i=0;i<shipList.length;i++)
		{
			if (shipList[i].getShip().getShipStats()==null)
			{
				shipList[i].getShip().setShipStats(new SpaceshipAnalyzer().generateStats(shipList[i].getShip()));
			}
		}
	}

}
