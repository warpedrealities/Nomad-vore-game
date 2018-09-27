package spaceship.npcShips;

import faction.Faction;
import nomad.StarSystem;
import nomad.universe.Universe;
import shared.Geometry;
import shared.Vec2f;
import solarview.spawningSystem.SpawnScriptTools;
import solarview.spawningSystem.SpawningSystem;
import spaceship.Spaceship;

public class NpcShipControllerSenseDetection {
	private StarSystem system;
	private Faction faction;
	private Spaceship hostile;
	private SpawnScriptTools spawnScriptTools;
	
	public NpcShipControllerSenseDetection(Faction faction,StarSystem system)
	{
		this.faction=faction;
		this.system=system;
	//	this.spawnScriptTools=new SpawnScriptTools(system.getEntities(),SpawningSystem.buildEntities(system.getEntities()));
	}
	
	public Spaceship getPlayer(int x, int y, int range)
	{
		if (Universe.getInstance().getCurrentEntity().getPosition().getDistance(x,y)<range)
		{
			return (Spaceship)Universe.getInstance().getCurrentEntity();
		}
		return null;
	}	
	public Spaceship getHostile(int x, int y, int range)
	{
		if (hostile!=null)
		{
			if (hostile.getPosition().getDistance(x,y)<range)
			{
				return hostile;
			}
			else
			{
				hostile=null;
			}
		}
		for (int i=0;i<system.getEntities().size();i++)
		{
			if (system.getEntities().get(i).getPosition().getDistance(x, y)<range)
			{
				if (Spaceship.class.isInstance(system.getEntities().get(i)))
				{
					Spaceship ship=(Spaceship) system.getEntities().get(i);
					if (ship.getShipController()!=null)
					{
						Faction f=ship.getShipController().getFaction();
						if (f.getRelationship(faction.getFilename())<50)
						{
							return ship;
						}
					}			
				}					
			}
		}			
		return null;
	}
	
	public int getDirection(Vec2f o,Vec2f p)
	{
		return Geometry.getDirection(o, p);
	}
	
	public SpawnScriptTools getSpawnScriptTools()
	{
		if (spawnScriptTools!=null)
		{
			return spawnScriptTools;		
		}
		spawnScriptTools=new SpawnScriptTools(system.getEntities(),SpawningSystem.buildEntities(system.getEntities()));
		return spawnScriptTools;
	}
}
