package spaceship.stats;

import java.util.HashMap;
import java.util.Map;

import spaceship.Spaceship;
import widgets.WidgetCapture;
import widgets.WidgetSlot;
import zone.Tile;

public class SpaceshipScanner {

	int freeSpecimenCapacity;
	Map<String, Integer> specimenCounts;

	public SpaceshipScanner(Spaceship ship) {
		specimenCounts = new HashMap<String, Integer>();
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
								for (int k = 0; k < wc.getCapacity(); k++) {
									if (wc.getNPC(k)!=null) {
										addSpecimen(wc.getNPC(k).getName());
									}
								}
							}
						}
					}

				}

			}
		}
	}

	private void addSpecimen(String name) {
		if (specimenCounts.get(name) != null) {
			specimenCounts.put(name, specimenCounts.get(name) + 1);
		}
		else {
			specimenCounts.put(name, 1);
		}
	}

	public int getFreeSpecimenCapacity() {
		return freeSpecimenCapacity;
	}

	public int getSpecimenCount(String attribute) {
		// TODO Auto-generated method stub
		return 0;
	}

}
