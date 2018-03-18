package solarview.spawningSystem;

import java.util.ArrayList;
import java.util.List;

import faction.FactionLibrary;
import nomad.Entity;
import nomad.FlagField;
import nomad.universe.Universe;
import shared.Geometry;
import shared.Vec2f;
import spaceship.Spaceship;
import spaceship.npcShips.NpcShipController;
import vmo.GameManager;

public class SpawnScriptTools {

	private List<Entity> systemEntities;
	private List<Entity> entitiesInSystem;
	private Long clock;
	private FlagField globalFlags;
	private FactionLibrary factions;
	
	public SpawnScriptTools(List<Entity> entitiesInSystem, List<Entity> systemEntities) {
		clock=GameManager.worldClock;
		factions=FactionLibrary.getInstance();
		globalFlags=Universe.getInstance().getPlayer().getFlags();
		this.systemEntities=systemEntities;
		this.entitiesInSystem=entitiesInSystem;
	}

	public List<Entity> getSystemEntities() {
		return systemEntities;
	}

	public List<Entity> getEntitiesInSystem() {
		return entitiesInSystem;
	}

	public Long getClock() {
		return clock;
	}

	public FlagField getGlobalFlags() {
		return globalFlags;
	}

	public FactionLibrary getFactions() {
		return factions;
	}
	
	public int countShips(String ship)
	{
		int count=0;
		for (int i=0;i<entitiesInSystem.size();i++)
		{
			if (Spaceship.class.isInstance(entitiesInSystem.get(i)))
			{
				Spaceship s=(Spaceship)entitiesInSystem.get(i);
				if (s.getshipName().equals(ship))
				{
					count++;
				}
			}
		}
		return count;
	}
	public int countFaction(String faction)
	{
		int count=0;
		for (int i=0;i<entitiesInSystem.size();i++)
		{
			if (Spaceship.class.isInstance(entitiesInSystem.get(i)))
			{
				Spaceship s=(Spaceship)entitiesInSystem.get(i);
				if (s.getShipController()!=null && s.getShipController().getFaction().getFilename().equals("faction"))
				{
					count++;
				}
			}
		}
		return count;
	}	
	private Vec2f getPosition()
	{
		int r=0;
		if (systemEntities.size()>1)
		{
			r=Universe.getInstance().m_random.nextInt(systemEntities.size());
		}
		int d=Universe.getInstance().m_random.nextInt(8);
		Vec2f p=Geometry.getPos(d, new Vec2f(0,0));
		p.x*=2;
		p.y*=2;
		p.add(systemEntities.get(r).getPosition());
		return p;
	}
	
	public void spawn(String ship,String controller)
	{
		//find a good spot to spawn it
		Vec2f p=getPosition();
		Spaceship spaceship=new Spaceship(ship,(int)p.x,(int)p.y,Spaceship.ShipState.SPACE);
		spaceship.setShipController(new NpcShipController(controller));
		entitiesInSystem.add(spaceship);
	}
	public void deSpawn(String ship)
	{
		//find a good spot to spawn it
		Vec2f p=getPosition();
		Spaceship spaceship=new Spaceship(ship,(int)p.x,(int)p.y,Spaceship.ShipState.SPACE);

		for (int i=0;i<entitiesInSystem.size();i++)
		{
			if (Spaceship.class.isInstance(entitiesInSystem.get(i)))
			{
				Spaceship s=(Spaceship)entitiesInSystem.get(i);
				if (s.getshipName().equals(ship))
				{
					entitiesInSystem.remove(s);
					break;
				}
			}
		}
	}
	
}
