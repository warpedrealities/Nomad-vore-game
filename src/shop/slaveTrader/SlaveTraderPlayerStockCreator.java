package shop.slaveTrader;

import java.util.ArrayList;
import java.util.List;

import actor.npc.NPC;
import actor.player.Inventory;
import actor.player.Player;
import actorRPG.npc.NPC_RPG;
import entities.Entity;
import entities.StarSystem;
import item.instances.ItemCaptureInstance;
import nomad.universe.Universe;
import spaceship.Spaceship;
import widgets.Widget;
import widgets.WidgetCapture;
import widgets.WidgetSlot;
import zone.Zone;

public class SlaveTraderPlayerStockCreator {

	private Zone shipZone;
	private List<SlaveLineItem> buyList;
	
	public SlaveTraderPlayerStockCreator(List<SlaveLineItem> buyList)
	{
		this.buyList=buyList;
	}
	
	public boolean attachShip(Player player)
	{
		ItemCaptureInstance ici=getCaptureDevice(player);
		if (ici==null) {return false;}
		shipZone=getShip(ici);
		if (shipZone!=null)
		{
			return true;
		}
		return false;
	}
	
	public SlaveTraderPlayerStock generateStock()
	{
		List <NPC> captives=shipCaptives();
		List <SlaveStockItem> slaves=new ArrayList<SlaveStockItem>();
		for (int i=0;i<captives.size();i++)
		{
			processCaptive(captives.get(i), slaves);
		}
		
		return new SlaveTraderPlayerStock(slaves, shipZone);
	}
	
	private void processCaptive(NPC npc, List<SlaveStockItem> slaves) {
		for (int i=0;i<buyList.size();i++)
		{
			if (buyList.get(i).getSlaveName().equals(npc.getName()))
			{
				float value=buyList.get(i).getValue();
				float multiplier=calcModifier(npc);
				value=value*multiplier;
				slaves.add(new SlaveStockItem(npc,(int)value));
				break;
			}
		}
	}

	private float calcModifier(NPC npc) {
		NPC_RPG rpg=(NPC_RPG)npc.getRPG();
		if (rpg.getValueCalculator()!=null)
		{
			return rpg.getValueCalculator().calcValue(npc.getFlags());			
		}
		else
		{
			return 1;
		}
	}

	private List<NPC> shipCaptives()
	{
		List<NPC> npcList=new ArrayList<NPC>();
		for (int i=0;i<shipZone.getWidth();i++)
		{
			for (int j=0;j<shipZone.getHeight();j++)
			{
				if (shipZone.getTile(i, j)!=null && shipZone.getTile(i, j).getWidgetObject()!=null)
				{
					Widget w=shipZone.getTile(i, j).getWidgetObject();
					if (WidgetSlot.class.isInstance(w))
					{
						WidgetSlot ws=(WidgetSlot)w;
						if (ws.getWidget()!=null && 
								WidgetCapture.class.isInstance(ws.getWidget()))
						{
							WidgetCapture wc=(WidgetCapture)ws.getWidget();
							addCaptives(npcList,wc);
						}
					}
					if (WidgetCapture.class.isInstance(w))
					{
						WidgetCapture wc=(WidgetCapture)w;		
						addCaptives(npcList,wc);	
					}
				}
			}
		}
		return npcList;
	}

	private void addCaptives(List<NPC> npcList, WidgetCapture wc) {
		for (int i=0;i<wc.getCapacity();i++)
		{
			if (wc.getNPC(i)!=null)
			{
				npcList.add(wc.getNPC(i));
			}
		}
	}

	private Zone getShip(ItemCaptureInstance ici) {
		String shipName=ici.getShip();
		Entity entity=Universe.getInstance().getCurrentEntity();
		Zone z=entity.getZone(shipName);
		if (z!=null)
		{
			return z;
		}
		else
		{
			return z=getZoneNearby(shipName);
		}
	}

	private Zone getZoneNearby(String shipName)
	{
		StarSystem system=Universe.getInstance().getCurrentStarSystem();
		Entity entity=Universe.getInstance().getCurrentEntity();
		for (int i=0;i<system.getEntities().size();i++)
		{
			if (system.getEntities().get(i).getName().equals(shipName) &&
				system.getEntities().get(i).getPosition().getDistance(entity.getPosition())<2)
			{
				if (Spaceship.class.isInstance(system.getEntities().get(i)))
				{
					return system.getEntities().get(i).getZone(shipName);
				}
			}
		}
		return null;
	}	
	private ItemCaptureInstance getCaptureDevice(Player player) {
		Inventory inventory=player.getInventory();
		for (int i=0;i<inventory.getNumItems();i++)
		{
			if (ItemCaptureInstance.class.isInstance(inventory.getItem(i)))
			{
				ItemCaptureInstance ici=(ItemCaptureInstance)inventory.getItem(i);
				if (ici.getShip()!=null)
				{
					return ici;
				}
			}
		}
		return null;
	}
}
