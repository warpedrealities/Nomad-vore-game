package worldgentools.auditing;

import shared.Vec2f;
import zone.Zone_int;

public class Pathfinder_void extends Pathfinder_carving {

	public Pathfinder_void(Zone_int zone, int carvethrough) {
		super(zone, carvethrough);
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
