package combat;

import actor.player.Player;
import item.Item;
import nomad.Universe;
import shared.Vec2f;
import view.ViewScene;
import widgets.WidgetItemPile;
import zone.Tile;
import zone.Zone;
import zone.TileDef.TileMovement;

public class ThrownWeaponHandler {

	public static void throwWeapon(Vec2f position, Item weapon) {
		Zone zone = Universe.getInstance().getCurrentZone();
		Tile t = zone.getTile((int) position.x, (int) position.y);
		if (t.getWidgetObject() != null && WidgetItemPile.class.isInstance(t.getWidgetObject())) {
			WidgetItemPile pile = (WidgetItemPile) t.getWidgetObject();
			pile.AddItem(weapon);
		} else if (t.getDefinition().getMovement() == TileMovement.WALK) {
			WidgetItemPile pile = new WidgetItemPile(2, "a pile of items containing ", weapon);
			ViewScene.m_interface.placeWidget(pile, (int) position.x, (int) position.y, false);
		}
	}

}
