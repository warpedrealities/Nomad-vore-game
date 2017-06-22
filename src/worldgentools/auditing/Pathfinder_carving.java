package worldgentools.auditing;

import shared.Vec2f;
import zone.Tile;
import zone.Zone_int;
import zone.TileDef.TileMovement;
import artificial_intelligence.pathfinding.Pathfinder;

public class Pathfinder_carving extends Pathfinder {

	int carvethrough;

	public Pathfinder_carving(Zone_int zone, int carvethrough) {
		super(zone);
		this.carvethrough = carvethrough;

	}

	boolean checkTile_v(int x, int y) {
		Tile t = m_zone.getTile(x, y);
		if (t == null) {
			return true;
		}
		if (t.getDefinition().getMovement() == TileMovement.WALK) {
			return true;
		}
		if (t.getDefinition().getID() == carvethrough) {
			return true;
		}
		return false;
	}

	@Override
	protected boolean CheckTile(Vec2f p, boolean fly) {
		if (m_zone.getActor((int) p.x, (int) p.y) == null && checkTile_v((int) p.x, (int) p.y)) {
			for (int i = 0; i < m_closedlist.size(); i++) {
				if (m_closedlist.get(i).m_position.x == p.x && m_closedlist.get(i).m_position.y == p.y) {
					return false;
				}
			}
			for (int i = 0; i < m_openlist.size(); i++) {
				if (m_openlist.get(i).m_position.x == p.x && m_openlist.get(i).m_position.y == p.y) {
					return false;
				}
			}

			return true;
		}

		return false;
	}

}
