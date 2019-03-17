package spaceship.stats;

import spaceship.Spaceship;
import widgets.WidgetCapture;
import widgets.WidgetSlot;
import zone.Tile;

public class SpaceshipScanner {

	int freeSpecimenCapacity;

	public SpaceshipScanner(Spaceship ship) {
		freeSpecimenCapacity = 0;
		for (int i = 0; i < ship.getZone(0).getWidth(); i++) {
			for (int j = 0; j < ship.getZone(0).getHeight(); j++) {
				// check tile
				Tile t = ship.getZone(0).getTile(i, j);
				if (t != null && t.getWidgetObject() != null) {

					if (t.getWidgetObject().getClass().getName().contains("WidgetSlot")) {
						WidgetSlot ws = (WidgetSlot) t.getWidgetObject();
						if (ws.getWidget() != null) {
							if (WidgetCapture.class.isInstance(ws.getWidget())) {
								WidgetCapture wc = (WidgetCapture) ws.getWidget();
								freeSpecimenCapacity += wc.getFreeCapacity();
							}
						}
					}

				}

			}
		}
	}

	public int getFreeSpecimenCapacity() {
		return freeSpecimenCapacity;
	}

}
