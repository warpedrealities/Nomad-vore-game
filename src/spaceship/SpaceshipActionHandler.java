package spaceship;

import shared.Vec2f;
import solarview.SolarScene;
import spaceship.Spaceship.ShipState;
import view.ViewScene;
import view.ZoneInteractionHandler;
import vmo.Game;
import widgets.WidgetPortal;
import worldgentools.ZoneBuildTools;
import zone.Landing;
import zone.Zone;
import zone.Zone.zoneType;
import landingScreen.LandingScreen;
import nomad.Station;
import nomad.Universe;
import nomad.World;

public class SpaceshipActionHandler {

	public int Launch(Spaceship ship, World world)
	{
		ship.getShipStats().runConversions();
		Landing landing=null;
		//find landing spot
		for (int i=0;i<world.getLandings().size();i++)
		{
			if (world.getLandings().get(i).getShip()==ship)
			{
				landing=world.getLandings().get(i);
				break;
			}
		}
		
		Zone zone=null;
		for (int i=0;i<world.getNumZones();i++)
		{
			if (world.getZone(i).getType()!=zoneType.CLOSED && world.getZone(i).getPosition().x==landing.getX() && world.getZone(i).getPosition().y==landing.getY())
			{
				zone=world.getZone(i);
				break;
			}
		}
		
		//disconnect ship
		for (int i=0;i<ship.getZone(0).getWidth();i++)
		{
			for (int j=0;j<ship.getZone(0).getHeight();j++)
			{
				if (ship.getZone(0).getTile(i, j)!=null && ship.getZone(0).getTile(i, j).getWidgetObject()!=null)
				{
					if (ship.getZone(0).getTile(i, j).getWidgetObject().getClass().getName().contains("WidgetPortal"))
					{
						WidgetPortal portal=(WidgetPortal)ship.getZone(0).getTile(i, j).getWidgetObject();
						portal.setDestination(null,null);
					}
				}
			}
		}
		//write ship out of zone
		if (zone.getTiles()!=null)
		{
			ZoneBuildTools ZBT=new ZoneBuildTools(zone.getName(),zone);
			ZBT.RemoveShip(landing);		
		}
		world.getLandings().remove(landing);
		//move ship one tile
		Universe.getInstance().getcurrentSystem().getEntities().add(ship);
		int r=Universe.m_random.nextInt(8);
		ship.setPosition(ZoneInteractionHandler.getPos(r, ship.getPosition()));
		
		//switch player current entity to the ship not the world
		Universe.getInstance().setCurrentWorld(ship);
		
	
		
		return r;
	}
	

	public int undock(Spaceship ship, Station station) {
		// TODO Auto-generated method stub
		
		ship.getShipStats().runConversions();
		station.unDock(ship);
		
		//disconnect ship
		for (int i=0;i<ship.getZone(0).getWidth();i++)
		{
			for (int j=0;j<ship.getZone(0).getHeight();j++)
			{
				if (ship.getZone(0).getTile(i, j)!=null && ship.getZone(0).getTile(i, j).getWidgetObject()!=null)
				{
					if (ship.getZone(0).getTile(i, j).getWidgetObject().getClass().getName().contains("WidgetPortal"))
					{
						WidgetPortal portal=(WidgetPortal)ship.getZone(0).getTile(i, j).getWidgetObject();
						portal.setDestination(null,null);
					}
				}
			}
		}
		
		
		//move ship one tile
		Universe.getInstance().getcurrentSystem().getEntities().add(ship);
		int r=Universe.m_random.nextInt(8);
		ship.setPosition(ZoneInteractionHandler.getPos(r, ship.getPosition()));
		
		//switch player current entity to the ship not the world
		Universe.getInstance().setCurrentWorld(ship);	
		return 0;
	}
	
	
	private boolean needLandingScreen(Spaceship ship, World world)
	{
		int visitedCount=0;
		if (world.getNumZones()>0)
		{
			for (int i=0;i<world.getNumZones();i++)
			{
				if (world.getZone(i)!=null)
				{
					Zone zone=world.getZone(i);
					if (zone.getType()==zoneType.SURFACE && zone.getTiles()!=null)
					{
						visitedCount++;
					}
				}		
			}

		}
		if (visitedCount>=2)
		{
			return true;
		}
		return false;
	}
	
	public void land(Spaceship ship,World world)
	{
		world.Generate();
		if (needLandingScreen(ship,world)==false)
		{

			//add ship to landing for world
			if (world.Land(ship))
			{
				//remove ship from starsystem
				Universe.getInstance().getcurrentSystem().getEntities().remove(ship);
				//move ship to colocate with world
				ship.setPosition(new Vec2f(world.getPosition().x,world.getPosition().y));
				//write ship into zone
				ship.setShipState(ShipState.LAND);
				//connect ship
				
				//switch current entity
					
				Universe.getInstance().setCurrentEntity(world);			

				//switch view to viewscene
				Game.sceneManager.SwapScene(new ViewScene());
				
				//dont need to decompose, happens on scene switch			
			}					
		}
		else
		{
			SolarScene.getInterface().setScreen(new LandingScreen(ship,world));
		}
	}

	public void dockStation(Spaceship ship, Station station) {
		// TODO Auto-generated method stub
		station.Generate();
		if (station.dock(ship))
		{
			//remove ship from starsystem
			Universe.getInstance().getcurrentSystem().getEntities().remove(ship);
			//move ship to colocate with world
			ship.setPosition(new Vec2f(station.getPosition().x,station.getPosition().y));
			//write ship into zone
			ship.setShipState(ShipState.DOCK);
			//connect ship
			
			//switch current entity
				
			Universe.getInstance().setCurrentEntity(station);			

			//switch view to viewscene
			Game.sceneManager.SwapScene(new ViewScene());
			
			//dont need to decompose, happens on scene switch			
		}	
	}

}
