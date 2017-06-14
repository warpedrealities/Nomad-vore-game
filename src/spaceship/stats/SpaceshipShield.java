package spaceship.stats;

import java.util.List;

import shipsystem.ShipShield;

public class SpaceshipShield {

	private int hitpoints,regeneration,absorption,restartTime;
	private float energyCost;
	
	public SpaceshipShield(List<ShipShield> generators)
	{
		for (int i=0;i<generators.size();i++)
		{
			energyCost=generators.get(i).getEnergyCost();
			hitpoints+=generators.get(i).getHitpoints();
			regeneration+=generators.get(i).getRegeneration();
			absorption+=generators.get(i).getAbsorption();
			if (restartTime<generators.get(i).getRestartTime())
			{
				restartTime=generators.get(i).getRestartTime();
			}
		}
		absorption=absorption/generators.size();
	}
	
	public int getHitpoints() {
		return hitpoints;
	}
	public int getRegeneration() {
		return regeneration;
	}
	public int getAbsorption() {
		return absorption;
	}
	public int getRestartTime() {
		return restartTime;
	}
	public float getEnergyCost() {
		return energyCost;
	}

}
