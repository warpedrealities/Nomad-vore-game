package solarview.spaceEncounter;

import java.util.List;

import shipsystem.conversionSystem.ShipConverter;
import spaceship.Spaceship;
import spaceship.stats.SpaceshipAnalyzer;
import vmo.Game;
public class SpaceCombatInitializer {

	private Spaceship player;
	private Spaceship enemy;
	
	public SpaceCombatInitializer(Spaceship player, Spaceship enemy)
	{
		this.player=player;
		this.enemy=enemy;
	}
	
	public void run()
	{
		if (player.getShipStats()==null)
		{
			player.setShipStats(new SpaceshipAnalyzer().generateStats(player));
		}
		List <ShipConverter> list=player.getShipStats().getConverters();
		for (int i=0;i<list.size();i++)
		{
			if (list.get(i).getProduct().equals("ENERGY"))
			{
				list.get(i).setActive(true);			
			}
		}
		player.setWarpHandler(null);
		enemy.Generate();
		enemy.generateStats();
		enemy.setShipStats(new
				 SpaceshipAnalyzer().generateStats(enemy));
		list=enemy.getShipStats().getConverters();
		for (int i=0;i<list.size();i++)
		{
			list.get(i).setActive(true);
		}
		Spaceship []enemies=new Spaceship[1];
		enemies[0]=enemy;
		Game.sceneManager.SwapScene(new SpaceEncounter(player,enemies));
	}
	
}
