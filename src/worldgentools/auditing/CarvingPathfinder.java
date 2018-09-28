package worldgentools.auditing;

import java.util.ArrayList;

import artificial_intelligence.pathfinding.PathNode;
import artificial_intelligence.pathfinding.Pathfinder;
import shared.Vec2f;
import shared.Vec2i;
import zone.Tile;
import zone.Zone;

public class CarvingPathfinder {

	Zone zone;
	int carvethrough;

	public CarvingPathfinder(Zone zone, int carvethrough) {
		this.carvethrough = carvethrough;
		this.zone = zone;
	}

	public void runCarving(ArrayList<Vec2i> reachable, ArrayList<Vec2i> unreachable, int replace) {
		for (int i = 0; i < unreachable.size(); i++) {
			float d = 99;
			Vec2i p = null;
			for (int j = 0; j < reachable.size(); j++) {
				float distance = reachable.get(j).getDistance(unreachable.get(i));
				if (distance < d) {
					d = distance;
					p = reachable.get(j);
				}
			}
			carve(unreachable.get(i), p, replace);
		}
	}

	public void carve(Vec2i source, Vec2i destination, int replace) {
		PathNode[] path = new Pathfinder(zone).genPath(source, destination, 32, false);
		if (path != null) {
			return;
		}

		path = new Pathfinder_carving(zone, carvethrough).genPath(new Vec2f(source.x, source.y),
				new Vec2f(destination.x, destination.y), 64, false);
		if (path != null) {
			for (int i = 0; i < path.length; i++) {
				Tile t = zone.getTile((int) path[i].m_position.x, (int) path[i].m_position.y);
				if (t != null) {
					if (t.getDefinition().getID() == carvethrough) {
						zone.getTiles()[(int) path[i].m_position.x][(int) path[i].m_position.y] = new Tile(
								(int) path[i].m_position.x, (int) path[i].m_position.y,
								zone.getZoneTileLibrary().getDef(replace), zone, zone.getZoneTileLibrary());
					}
				}
			}

		}

	}
}
