package spaceship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import actor.Actor;
import actor.NPC;

import nomad.Universe;

import shipsystem.ShipAbility;
import shipsystem.ShipAbility.AbilityType;
import shipsystem.ShipConverter;
import shipsystem.ShipModifier;
import shipsystem.ShipResource;
import shipsystem.WidgetDamage;
import shipsystem.WidgetSystem;
import widgets.WidgetAccomodation;
import widgets.WidgetSlot;
import zone.Tile;
import zone.TileDef.TileMovement;

public class SpaceshipAnalyzer {

	
	public SpaceshipStats generateStats(Spaceship ship)
	{
		
		ArrayList<Integer> uids=new ArrayList<Integer>();
		
		SpaceshipStats stats=new SpaceshipStats();
		if (ship.getBaseStats().getThrustCost()==0)
		{
			ship.generateStats();
		}
		stats.setFuelEfficiency(ship.getBaseStats().getThrustCost());
		stats.setArmour(ship.getBaseStats().getArmour());
		stats.setEvasion(ship.getBaseStats().getEvasion());
		stats.setMoveCost(ship.getBaseStats().getMoveCost());
		stats.addResource("HULL", ship.getBaseStats().getMaxHullPoints(), ship.getBaseStats().getMaxHullPoints());
		for (int i=0;i<ship.getZone(0).getWidth();i++)
		{
			for (int j=0;j<ship.getZone(0).getHeight();j++)
			{
				//check tile
				Tile t=ship.getZone(0).getTile(i, j);
				if (t!=null && t.getWidgetObject()!=null)
				{
					if (t.getWidgetObject().getClass().getName().contains("WidgetAccomodation"))
					{
						WidgetAccomodation wa=(WidgetAccomodation)t.getWidgetObject();
						stats.setCrewCapacity(stats.getCrewCapacity()+wa.getCapacity());
					}
					if (t.getWidgetObject().getClass().getName().contains("WidgetItemPile"))
					{
						stats.setLooseItems(true);
					}
					if (t.getWidgetObject().getClass().getName().contains("WidgetSlot"))
					{
						WidgetSlot ws=(WidgetSlot)t.getWidgetObject();
						if (ws.getWidget()!=null)
						{
							if (ws.getWidget().getClass().getName().contains("WidgetAccomodation"))
							{
								WidgetAccomodation wa=(WidgetAccomodation)ws.getWidget();
								stats.setCrewCapacity(stats.getCrewCapacity()+wa.getCapacity());
							}
							if (ws.getWidget().getClass().getName().contains("WidgetSystem"))
							{
								WidgetSystem system=(WidgetSystem)ws.getWidget();
								
								for (int k=0;k<system.getShipAbilities().size();k++)
								{
									ShipAbility.AbilityType type=system.getShipAbilities().get(k).getAbilityType();
									
									switch (type)
									{
									case SA_RESOURCE:
										ShipResource res=(ShipResource)system.getShipAbilities().get(k);
										stats.addResource(res.getContainsWhat(), res.getAmountContained(), res.getContainedCapacity());
										break;		
										
									case SA_CONVERTER:
										ShipConverter con=(ShipConverter)system.getShipAbilities().get(k);

										stats.addConverter(con);
										break;		
									case SA_MODIFIER:
										ShipModifier mod=(ShipModifier)system.getShipAbilities().get(k);
										useShipMod(stats,mod,uids);
										break;		
									}
									
								}
							}		
						}
					}	
					if (t.getWidgetObject().getClass().getName().contains("WidgetDamage"))
					{
						WidgetDamage damage=(WidgetDamage)t.getWidgetObject();
						stats.subtractResource("HULL", damage.getDamageValue());
					}
				}
		
			}
		}		
		
		for (int i=0;i<ship.getZone(0).getActors().size();i++)
		{
			Actor actor=ship.getZone(0).getActors().get(i);
			if (NPC.class.isInstance(actor))
			{
				stats.addCrew((NPC)actor);
			}
		}
		
		
		return stats;
	}
	
	private void useShipMod(SpaceshipStats stats, ShipModifier mod,ArrayList<Integer> uids)
	{
		if (mod.getUid()!=0)
		{
			for (int i=0;i<uids.size();i++)
			{
				if (uids.get(i).equals(mod.getUid()))
				{
					return;
				}
			}
			
		}
		switch (mod.getModifies())
		{
			case ShipModifier.FUEL_EFFICIENCY:
			stats.setFuelEfficiency(stats.getFuelEfficiency()*mod.getModification());
			break;
		
		
		
		}
	}
	
	public void decomposeResources(SpaceshipStats stats, Spaceship ship)
	{
		//get all resources
		ArrayList<SpaceshipResource> resources=new ArrayList<SpaceshipResource>();
		Iterator <SpaceshipResource> it=stats.getIterator();
		while (it.hasNext())
		{
			resources.add(it.next());
		}
		//associate ship resources
		Map<String, ArrayList<ShipResource>> shipResources=new HashMap<String,ArrayList<ShipResource>>();

		for (int i=0;i<resources.size();i++)
		{
			if (!resources.get(i).getResourceName().equals("HULL"))
			{
				shipResources.put(resources.get(i).getResourceName(),new ArrayList<ShipResource>());
			}
		}
		//get all hull damage
		ArrayList<WidgetDamage> damage=new ArrayList<WidgetDamage>();
		int accounteddamage=0;
		int statdamage=(int) ((int)stats.getResource("HULL").getResourceCap()-stats.getResource("HULL").getResourceAmount());
		for (int i=0;i<ship.getZone(0).getWidth();i++)
		{
			for (int j=0;j<ship.getZone(0).getHeight();j++)
			{
				//check tile
				Tile t=ship.getZone(0).getTile(i, j);
				if (t!=null && t.getWidgetObject()!=null)
				{
					if (t.getWidgetObject().getClass().getName().contains("WidgetItemPile"))
					{
						stats.setLooseItems(true);
					}
					if (t.getWidgetObject().getClass().getName().contains("WidgetSystem"))
					{
						WidgetSystem system=(WidgetSystem)t.getWidgetObject();
						
						for (int k=0;k<system.getShipAbilities().size();k++)
						{
							ShipAbility.AbilityType type=system.getShipAbilities().get(k).getAbilityType();
							if (type==AbilityType.SA_RESOURCE)
							{
								ShipResource res=(ShipResource)system.getShipAbilities().get(k);
								shipResources.get(res.getContainsWhat()).add(res);
							}
						}
					}
					if (t.getWidgetObject().getClass().getName().contains("WidgetSlot"))
					{
						WidgetSlot ws=(WidgetSlot)t.getWidgetObject();
						if (ws.getWidget()!=null)
						{
							if (ws.getWidget().getClass().getName().contains("WidgetSystem"))
							{
								WidgetSystem system=(WidgetSystem)ws.getWidget();
								
								for (int k=0;k<system.getShipAbilities().size();k++)
								{
									ShipAbility.AbilityType type=system.getShipAbilities().get(k).getAbilityType();
									if (type==AbilityType.SA_RESOURCE)
									{
										ShipResource res=(ShipResource)system.getShipAbilities().get(k);
										shipResources.get(res.getContainsWhat()).add(res);
									}
								}
							}		
						}
					}	
					if (t.getWidgetObject().getClass().getName().contains("WidgetDamage"))
					{
						damage.add((WidgetDamage)t.getWidgetObject());
						accounteddamage+=((WidgetDamage)t.getWidgetObject()).getDamageValue();
					}
				}
		
			}
		}				
		
		if(accounteddamage<statdamage)
		{
			//create another damage impact holder
			createDamageWidget(ship,statdamage-accounteddamage);
		}
		
		//convey changes one to the other
		for (int i=0;i<resources.size();i++)
		{
			if (!resources.get(i).getResourceName().equals("HULL"))
			{
				float v=resources.get(i).getResourceAmount();
				ArrayList<ShipResource> shipres=shipResources.get(resources.get(i).getResourceName());

				for (int j=0;j<shipres.size();j++)
				{
					if (shipres.get(j).getContainedCapacity()<=v)
					{
						v-=shipres.get(j).getContainedCapacity();
						shipres.get(j).setAmountContained(shipres.get(j).getContainedCapacity());
					}
					else
					{
						shipres.get(j).setAmountContained(v);
					}
				}				
			}	
		}
	}
	
	private void createDamageWidget(Spaceship ship, int damage)
	{
		while (true)
		{
			int x=Universe.m_random.nextInt((int)ship.getSize().x);
			int y=Universe.m_random.nextInt((int)ship.getSize().y);
			if (ship.getZone(0).getTile(x, y)!=null && ship.getZone(0).getTile(x, y).getDefinition().getMovement()==TileMovement.WALK)
			{
				if (ship.getZone(0).getTile(x, y).getWidgetObject()!=null)
				{
					if (WidgetDamage.class.isInstance(ship.getZone(0).getTile(x, y).getWidgetObject()))
					{
						WidgetDamage d=(WidgetDamage)ship.getZone(0).getTile(x, y).getWidgetObject();
						d.setDamageValue(damage+d.getDamageValue());
						return;
					}
				}
				else
				{
					ship.getZone(0).getTile(x, y).setWidget(new WidgetDamage(damage));
					return;
				}		
			}			
		}
	}
	
	public int getNumSystems(Spaceship ship)
	{
		int count=0;
		for (int i=0;i<ship.getZone(0).getWidth();i++)
		{
			for (int j=0;j<ship.getZone(0).getHeight();j++)
			{
				//check tile
				Tile t=ship.getZone(0).getTile(i, j);
				if (t!=null && t.getWidgetObject()!=null)
				{
					if (!t.getWidgetObject().getClass().getName().contains("WidgetItemPile"))
					{
						count++;
					}
				}
		
			}
		}

		return count;
	}
}
