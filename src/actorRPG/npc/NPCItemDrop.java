package actorRPG.npc;

import org.w3c.dom.Element;

import item.Item;
import item.ItemCoin;
import nomad.universe.Universe;
import shared.Vec2f;
import view.ViewScene;
import widgets.Widget;
import widgets.WidgetItemPile;
import zone.Tile;
import zone.Zone;

public class NPCItemDrop {

	private int probability;
	private String itemName;
	private boolean defeatOnly=false;
	
	public NPCItemDrop(Element element) {
		probability = Integer.parseInt(element.getAttribute("chance"));
		itemName = element.getAttribute("item");
		if (element.getAttribute("defeatOnly").equals("true"))
		{
			defeatOnly=true;
		}
	}

	public void useDrop(Vec2f p) {
		if (probability == 100) {
			placeDrop(p);
		} else {
			int r = Universe.m_random.nextInt(100);
			if (r < probability) {
				placeDrop(p);
			}
		}
	}

	private Item getItem()
	{
		if (itemName.contains("GOLD"))
		{
			ItemCoin coins=new ItemCoin();
			String count=itemName.replace("GOLD", "");
			coins.setCount(Integer.parseInt(count));
			return coins;
		}

		Item item=Universe.getInstance().getLibrary().getItem(itemName);
		return item;
	}
	
	private void placeDrop(Vec2f p) {
		
		WidgetItemPile pile = null;
		Widget w=ViewScene.m_interface.getSceneController().getWidget((int)p.x,(int)p.y);
		if (WidgetItemPile.class.isInstance(w))
		{
			pile=(WidgetItemPile)w;
			pile.AddItem(getItem());
		}
		else
		{
			pile=new WidgetItemPile(2, "a pile of items containing ",
					getItem());
			ViewScene.m_interface.placeWidget(pile, (int) p.x, (int) p.y, true);
		
		}

	}

	public boolean isDefeatOnly() {
		return defeatOnly;
	}
	
	
}
