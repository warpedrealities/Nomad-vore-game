package shipsystem.droneSpawning;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import actor.Actor;
import actor.npc.NPC;
import nomad.universe.Universe;
import shared.ParserHelper;
import shared.Vec2f;
import shared.Vec2i;
import shipsystem.ShipResource;
import shipsystem.WidgetSystem;
import shipsystem.droneSpawning.ShipDroneSystem.DroneTrigger;
import spaceship.Spaceship;
import spaceship.SpaceshipResource;
import widgets.WidgetSlot;
import zone.Landing;
import zone.TileDef.TileMovement;
import zone.Zone;

public class DroneHandling {

	private List <ShipDroneSystem> droneSystems;
	private List <ShipResource> resourceSystems;
	
	public DroneHandling()
	{
		droneSystems=new ArrayList<ShipDroneSystem>();
		resourceSystems=new ArrayList<ShipResource>();
	}
	
	public void droneDeployment(Landing landing, Zone m_zone) {
		populateDroneSystemList(landing.getShip());
		if (droneSystems.size()==0)
		{
			return;
		}
		for (int i=0;i<droneSystems.size();i++)
		{
			deployDroneSystem(landing, m_zone, droneSystems.get(i));
		}
	}

	private void deployDroneSystem(Landing landing, Zone m_zone, ShipDroneSystem shipDroneSystem) {
		List<ShipResource> deploymentResources=listResource(shipDroneSystem);
		int count=countResources(deploymentResources);
		for (int i=0;i<shipDroneSystem.getNumDeployed();i++)
		{
			if (count>=shipDroneSystem.getDeployCost())
			{
				deployDrone(shipDroneSystem,landing,m_zone);
				count-=shipDroneSystem.getDeployCost();
				reduceResources(shipDroneSystem, deploymentResources);
			}
		}	
	}
	
	private void reduceResources(ShipDroneSystem shipDroneSystem, List<ShipResource> deploymentResources) {
		for (int i=0;i<deploymentResources.size();i++)
		{
			if (deploymentResources.get(i).getAmountContained()>shipDroneSystem.getDeployCost())
			{
				deploymentResources.get(i).setAmountContained(deploymentResources.get(i).getAmountContained()-shipDroneSystem.getDeployCost());
				break;
			}
		}		
	}

	private void deployDrone(ShipDroneSystem shipDroneSystem,Landing landing, Zone m_zone) {
		
		Vec2i p=getPosition(landing,m_zone);
		Document doc = ParserHelper.LoadXML("assets/data/npcs/" + shipDroneSystem.getFileName() + ".xml");
		Element n = (Element) doc.getFirstChild();
		NPC npc = new NPC(n, new Vec2f(p.x, p.y), shipDroneSystem.getFileName());

		npc.setCollisioninterface(m_zone);
		m_zone.getTile(p.x, p.y).setActorInTile(npc);
		m_zone.getActors().add(npc);
	}

	private Vec2i getPosition(Landing landing, Zone m_zone) {
		Vec2i p=null;
		while (p==null)
		{
			int x=(int)landing.getPosition().x+(Universe.getInstance().m_random.nextInt((int)landing.getShip().getSize().x));
			int y=(int)landing.getPosition().y+(Universe.getInstance().m_random.nextInt((int)landing.getShip().getSize().y));
			if (m_zone.getTile(x, y)!=null && 
				m_zone.getTile(x, y).getDefinition().getMovement()==TileMovement.WALK &&
				m_zone.getTile(x, y).getActorInTile()==null)
			{
				p=new Vec2i(x,y);
			}
		}
		return p;
	}

	private int countResources(List<ShipResource> deploymentResources) {
		int count=0;
		for (int i=0;i<deploymentResources.size();i++)
		{
			count+=(int)deploymentResources.get(i).getAmountContained();
		}
		return count;
	}

	private List<ShipResource> listResource(ShipDroneSystem shipDroneSystem) {
		List<ShipResource> list=new ArrayList<ShipResource>();
		for (int i=0;i<resourceSystems.size();i++)
		{
			if (resourceSystems.get(i).getContainsWhat().equals(shipDroneSystem.getResourceName()))
			{
				list.add(resourceSystems.get(i));
			}
		}
		return list;
	}

	private void retrieveDroneSystem(Landing landing, Zone m_zone, ShipDroneSystem shipDroneSystem) {
		List<ShipResource> deploymentResources=listResource(shipDroneSystem);
		for (int i=0;i<shipDroneSystem.getNumDeployed();i++)
		{
			for (int j=0;j<m_zone.getActors().size();j++)
			{
				if (m_zone.getActors().get(j).getName().equals(shipDroneSystem.getNpcName()) &&
						m_zone.getActors().get(j).getRPGHandler().getActive())
				{
					Actor actor=m_zone.getActors().get(j);
					m_zone.getTile((int)actor.getPosition().x,(int)actor.getPosition().y).setActorInTile(null);
					m_zone.getActors().remove(actor);
					recoverResources(landing.getShip(),shipDroneSystem);
					break;
				}
			}
		}
	}

	private void recoverResources(Spaceship ship, ShipDroneSystem shipDroneSystem) {
		SpaceshipResource r=ship.getShipStats().getResource(shipDroneSystem.getResourceName());
		r.setResourceAmount(r.getResourceAmount()+shipDroneSystem.getDeployCost());
	}

	public void droneRemoval(Landing landing, Zone m_zone) {
		populateDroneSystemList(landing.getShip());
		if (droneSystems.size()==0)
		{
			return;
		}
		for (int i=0;i<droneSystems.size();i++)
		{
			retrieveDroneSystem(landing, m_zone, droneSystems.get(i));
		}
	}


	private void populateDroneSystemList(Spaceship ship)
	{
		if (ship.getZone(0)!=null && ship.getZone(0).getTiles()!=null)
		{
			Zone z=ship.getZone(0);
			for (int i=0;i<z.getWidth();i++)
			{
				for (int j=0;j<z.getHeight();j++)
				{
					if (z.getTile(i, j)!=null &&
							WidgetSlot.class.isInstance(z.getTile(i, j).getWidgetObject()))
					{
						WidgetSlot slot=(WidgetSlot)z.getTile(i, j).getWidgetObject();
						if (WidgetSystem.class.isInstance(slot.getWidget()))
						{
							WidgetSystem ws=(WidgetSystem)slot.getWidget();
							for (int k=0;k<ws.getShipAbilities().size();k++)
							{
								switch (ws.getShipAbilities().get(k).getAbilityType())
								{
								case SA_RESOURCE:
									resourceSystems.add((ShipResource)ws.getShipAbilities().get(k));
									break;
									
								case SA_SPAWNER:
									ShipDroneSystem d=(ShipDroneSystem)ws.getShipAbilities().get(k);
									if (d.getTrigger()==DroneTrigger.DT_LANDING)
									{
										droneSystems.add((ShipDroneSystem)ws.getShipAbilities().get(k));								
									}
									break;
								}	
							}	
						}			
					}
				}
			}
			
		}
	}
}
