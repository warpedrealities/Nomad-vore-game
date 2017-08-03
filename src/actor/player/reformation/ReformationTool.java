package actor.player.reformation;

import java.util.List;

import actor.player.Player;
import item.Item;
import item.ItemCoin;
import nomad.Entity;
import nomad.Universe;
import shared.Vec2f;
import view.ZoneInteractionHandler;
import widgets.Widget;
import widgets.WidgetItemPile;
import widgets.WidgetReformer;
import widgets.WidgetSlot;
import zone.Tile;
import zone.Zone;

public class ReformationTool {

	private boolean canReform;
	private ReformationHandler handler;
	private Entity entity;
	
	private Zone zone;
	private Vec2f destination;
	
	public ReformationTool(Entity entity, ReformationHandler handler)
	{
		this.entity=entity;
		this.handler=handler;
		canReform=checkReform();
	}
	
	private Vec2f findMachine(Zone z, Long l)
	{
		for (int i=0;i<z.getWidth();i++)
		{
			for (int j=0;j<z.getHeight();j++)
			{
				Widget w=z.getWidget(i, j);
				if (w!=null && WidgetSlot.class.isInstance(w))
				{
					WidgetSlot ws=(WidgetSlot)w;
					w=ws.getWidget();
				}
				if (w!=null && WidgetReformer.class.isInstance(w))
				{
					WidgetReformer reformer=(WidgetReformer)w;
					if (reformer.getUid()==l && 
							reformer.isActive() && 
							!reformer.isSuppressed())
					{	
						return new Vec2f(i,j);
					}
				}
			}
		}
		return null;
	}
	
	private boolean checkReform()
	{
		for (int i=0;i<handler.getMachines().size();i++)
		{
			Zone z=entity.getZone(handler.getMachines().get(i).getZoneName());
			if (z!=null)
			{
				//find the actual machine
				Vec2f p=findMachine(z,handler.getMachines().get(i).getUid());
				if (p!=null)
				{
					destination=p;
					zone=z;
					return true;
				}
			}
		}
		return false;
	}

	public boolean getCanReform() {
		return canReform;
	}
	
	private void placePile(WidgetItemPile pile)
	{
		Player player=Universe.getInstance().getPlayer();
		Zone current=Universe.getInstance().getCurrentZone();
		
		Tile t=current.getTile((int)player.getPosition().x, (int)player.getPosition().y);
		if (t.getWidgetObject()==null)
		{
			t.setWidget(pile);
			return;
		}
		else
		{
			for (int i=0;i<8;i++)
			{
				Vec2f p=ZoneInteractionHandler.getPos(i, player.getPosition());
				t=current.getTile((int)p.x, (int)p.y);
				if (t!=null && t.getWidgetObject()==null)
				{
					t.setWidget(pile);
					return;
				}
				
			}
		}
	}
	
	public void reform()
	{
		Player player=Universe.getInstance().getPlayer();
		//drop all equipment at location

		List <Item> items=player.getInventory().getItems();
		
		WidgetItemPile pile=new WidgetItemPile(2, "a pile of items containing ", items.get(0));
		for (int i=1;i<items.size();i++)
		{
			pile.AddItem(items.get(i));
		}
		if (player.getInventory().getPlayerGold()>0)
		{
			ItemCoin coins=new ItemCoin();
			coins.setCount(player.getInventory().getPlayerGold());
			player.getInventory().setPlayerGold(0);
			pile.AddItem(coins);
		}
		for (int i=0;i<5;i++)
		{
			if (player.getInventory().getSlot(i)!=null)
			{
				pile.AddItem(player.UnEquip(i));		
			}
		}
		player.getInventory().setWeight(0);
		player.getInventory().getItems().clear();
		//place pile
		placePile(pile);
		//pass time
		Universe.AddClock(250);
		//move player to reformation system
		Universe.getInstance().getPlayer().setPosition(new Vec2f(destination.x,destination.y));
		Universe.getInstance().setZone(zone);
		//set health, resolve and satiation to 50%
		for (int i=0;i<3;i++)
		{
			if (player.getRPG().getStat(i)<player.getRPG().getStatMax(i)/4)
			{
				player.getRPG().setStat(i, player.getRPG().getStatMax(i)/4);		
			}
		}

	}
}
