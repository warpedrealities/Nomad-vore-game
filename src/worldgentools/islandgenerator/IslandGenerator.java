package worldgentools.islandgenerator;

import java.util.ArrayList;
import java.util.List;

import zone.TileDef.TileMovement;
import zone.Zone;

public class IslandGenerator {

	private List<Island> islandList;
	private Zone zone;

	public IslandGenerator(Zone zone) {
		islandList = new ArrayList<Island>();
		this.zone = zone;
	}

	public void run() {
		// build islands
		for (int i = 0; i < zone.getWidth(); i++) {
			for (int j = 0; j < zone.getHeight(); j++) {
				scanTile(i, j);
			}
		}
		doLinking();
	}

	private void doLinking() {
		for (int i = 0; i < islandList.size(); i++) {
			for (int j = i; j < islandList.size(); j++) {
				if (islandList.get(i).isNeighbour(islandList.get(j))) {
					islandList.remove(j);
					break;
				}
			}
		}
	}

	private void scanTile(int x, int y) {
		if (zone.getTile(x, y) == null) {
			return;
		}
		if (zone.getTile(x, y).getDefinition().getMovement() == TileMovement.WALK) {
			// check all islands
			for (int i = 0; i < islandList.size(); i++) {
				if (islandList.get(i).isAdjacent(x, y)) {
					return;
				}
			}
			Island is = new Island(zone.getWidth(), zone.getHeight());
			is.addTile(x, y);
			islandList.add(is);
		}
	}

	public Island getLargestIsland() {
		int size = 0;
		int index = 0;
		for (int i = 0; i < islandList.size(); i++) {
			if (size < islandList.get(i).getSize()) {
				size = islandList.get(i).getSize();
				index = i;
			}
		}

		return islandList.get(index);
	}
}
