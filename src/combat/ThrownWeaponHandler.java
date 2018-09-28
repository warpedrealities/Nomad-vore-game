package combat;

import item.Item;
import nomad.universe.Universe;
import shared.Vec2f;
import view.ViewScene;
import widgets.WidgetItemPile;
import zone.Tile;
import zone.TileDef.TileMovement;
import zone.Zone;

public class ThrownWeaponHandler {

	public static void throwWeapon(Vec2f position, Item weapon) {
		Zone zone = Universe.getInstance().getCurrentZone();
		Tile t = zone.getTile((int) position.x, (int) position.y);
		if (t.getWidgetObject() != null && WidgetItemPile.class.isInstance(t.getWidgetObject())) {
			WidgetItemPile pile = (WidgetItemPile) t.getWidgetObject();
			pile.AddItem(weapon.getItem());
		} else if (t.getDefinition().getMovement() == TileMovement.WALK) {
			WidgetItemPile pile = new WidgetItemPile(2, "a pile of items containing ", weapon.getItem());
			ViewScene.m_interface.placeWidget(pile, (int) position.x, (int) position.y, false);
		}
	}

}
