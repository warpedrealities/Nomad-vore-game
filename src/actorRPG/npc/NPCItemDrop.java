package actorRPG.npc;

import org.w3c.dom.Element;

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

	public NPCItemDrop(Element element) {
		probability = Integer.parseInt(element.getAttribute("chance"));
		itemName = element.getAttribute("item");
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

	private void placeDrop(Vec2f p) {
		WidgetItemPile pile = new WidgetItemPile(2, "a pile of items containing ",
				Universe.getInstance().getLibrary().getItem(itemName));
		ViewScene.m_interface.placeWidget(pile, (int) p.x, (int) p.y, true);

	}
}
