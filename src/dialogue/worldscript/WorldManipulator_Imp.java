package dialogue.worldscript;

import java.util.List;

import nomad.Entity;
import zone.Landing;
import zone.Tile;
import zone.Zone;

public class WorldManipulator_Imp implements WorldManipulator {

	private Entity entity;
	private Zone zone;

	public WorldManipulator_Imp(Entity entity) {
		this.entity = entity;
	}

	@Override
	public void setZone(String zoneName) {
		zone = entity.getZone(zoneName);
	}

	@Override
	public void removeWidget(int x, int y) {
		if (zone != null) {
			Tile t = zone.getTile(x, y);
			if (t != null) {
				t.setWidget(null);
			}
		}
	}

	@Override
	public void RestoreShip(String shipName, int x, int y) {

		List<Landing> landings = entity.getLandings();
		for (int i = 0; i < landings.size(); i++) {
			if (landings.get(i).getShip().getName().equals(shipName) && landings.get(i).getPosition().x == x
					&& landings.get(i).getPosition().y == y) {
				landings.get(i).getShip().setUnusableState(null);
				break;
			}
		}
	}

}
