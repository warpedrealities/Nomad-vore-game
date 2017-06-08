package dialogue;

import actor.Inventory;
import actor.Player;
import actor.npc.NPC;
import item.ItemCaptureInstance;
import nomad.Entity;
import nomad.Universe;
import view.ViewScene;
import widgets.WidgetCapture;
import widgets.WidgetSlot;
import zone.Zone;

public class CaptureHandler {
	
	private Entity entity;
	private Player player;
	
	public CaptureHandler(Entity entity,Player player)
	{
		this.entity=entity;
		this.player=player;
	}
	
	private ItemCaptureInstance getCaptureItem()
	{
		String s=Universe.getInstance().getCurrentZone().getName();
		Inventory inventory=Universe.getInstance().getPlayer().getInventory();
		for (int i=0;i<inventory.getNumItems();i++)
		{
			if (ItemCaptureInstance.class.isInstance(inventory.getItem(i)))
			{
				return (ItemCaptureInstance) inventory.getItem(i);
			}
		}
		return null;
	}
	
	private Zone getZone(ItemCaptureInstance item)
	{
		if (item.getShip()!=null)
		{
			Zone zone=entity.getZone(item.getShip());
			if (zone!=null)
			{
				return zone;
			}
		}
		return null;
	}
	
	private WidgetCapture getCaptureWidget(Zone zone)
	{
		for (int i=0;i<zone.getWidth();i++)
		{
			for (int j=0;j<zone.getHeight();j++)
			{
				if (zone.getTile(i, j)!=null && zone.getTile(i, j).getWidgetObject()!=null)
				{
					if (WidgetSlot.class.isInstance(zone.getTile(i, j).getWidgetObject()))
					{
						WidgetSlot slot=(WidgetSlot)zone.getTile(i, j).getWidgetObject();
						if (WidgetCapture.class.isInstance(slot.getWidget()))
						{
							WidgetCapture capture=(WidgetCapture)slot.getWidget();
							for (int k=0;k<capture.getCapacity();k++)
							{
								if (capture.getNPC(k)==null)
								{
									return capture;
								}
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	public boolean capture(NPC npc)
	{
		ItemCaptureInstance item=getCaptureItem();
		if (item!=null)
		{
			if (item.getShip()==null)
			{
				ViewScene.m_interface.DrawText("capture device not synchronized");		
				return false;
			}
			Zone zone=getZone(item);
			if (zone==null)
			{
				ViewScene.m_interface.DrawText("specimen containment out of range");	
				return false;
			}
			WidgetCapture capture=getCaptureWidget(zone);
			if (capture==null)
			{
				ViewScene.m_interface.DrawText("no specimen containment available");		
				return false;
			}
			for (int k=0;k<capture.getCapacity();k++)
			{
				if (capture.getNPC(k)==null)
				{
					capture.setNPC(npc, k);
					ViewScene.m_interface.DrawText("specimen transported to containment");		
					return true;
				}
			}	
		}
		ViewScene.m_interface.DrawText("no capture device available");
		return false;
	}
	
}
