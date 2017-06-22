package landingScreen;

import java.util.ArrayList;

import landingScreen.LandingElement.LandingClass;
import nomad.World;
import zone.Zone;
import zone.Zone.zoneType;

public class LandingAnalyzer {

	static public LandingElement[][] generateGrid(World world) {
		ArrayList<LandingElement> elements = new ArrayList<LandingElement>();

		int width = 0;
		int height = 0;
		for (int i = 0; i < world.getNumZones(); i++) {
			Zone zone = world.getZone(i);
			if (zone.getType() != zoneType.CLOSED) {
				if (zone.getPosition().x + 1 > width) {
					width = (int) zone.getPosition().x + 1;
				}
				if (zone.getPosition().y + 1 > height) {
					height = (int) zone.getPosition().y + 1;
				}
				if (zone.getTiles() != null) {
					if (isOccupied(world, (int) zone.getPosition().x, (int) zone.getPosition().y)) {
						elements.add(new LandingElement(LandingClass.LC_OCCUPIED, (int) zone.getPosition().x,
								(int) zone.getPosition().y));
					} else {
						elements.add(new LandingElement(LandingClass.LC_OPEN, (int) zone.getPosition().x,
								(int) zone.getPosition().y));
					}
				} else {
					elements.add(new LandingElement(LandingClass.LC_INVALID, (int) zone.getPosition().x,
							(int) zone.getPosition().y));
				}
			}
		}

		LandingElement[][] grid = new LandingElement[width][];
		for (int i = 0; i < width; i++) {
			grid[i] = new LandingElement[height];
		}
		boolean b = false;

		for (int i = 0; i < elements.size(); i++) {
			LandingElement element = elements.get(i);
			if (b == false && element.getLandingType() == LandingClass.LC_OPEN) {
				element.setLandingType(LandingClass.LC_SELECTED);
				b = true;
			}
			grid[element.getxPosition()][element.getyPosition()] = element;
		}

		return grid;
	}

	static private boolean isOccupied(World world, int x, int y) {
		if (world.getLandings().size() > 0) {
			for (int i = 0; i < world.getLandings().size(); i++) {
				if (world.getLandings().get(i).getX() == x && world.getLandings().get(i).getY() == y) {
					return true;
				}

			}
		}
		return false;
	}
}
