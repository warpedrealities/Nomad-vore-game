package worldgentools.gridtown;

import artificial_intelligence.pathfinding.PathNode;
import artificial_intelligence.pathfinding.Pathfinder;
import shared.Vec2f;
import view.ZoneInteractionHandler;
import zone.Zone_int;

public class GridTownPathfinder extends Pathfinder {

	int pathTile;

	public GridTownPathfinder(Zone_int zone, int pathTile) {
		super(zone);

		this.pathTile = pathTile;
	}

	protected int CheckTile(Vec2f p) {
		for (int i = 0; i < m_closedlist.size(); i++) {
			if (m_closedlist.get(i).m_position.x == p.x && m_closedlist.get(i).m_position.y == p.y) {
				return 0;
			}
		}
		for (int i = 0; i < m_openlist.size(); i++) {
			if (m_openlist.get(i).m_position.x == p.x && m_openlist.get(i).m_position.y == p.y) {
				return 0;
			}
		}
		if (m_zone.getTile((int) p.x, (int) p.y) == null) {
			return 1;
		}
		if (m_zone.getTile((int) p.x, (int) p.y).getDefinition().getID() == pathTile) {

			return 2;
		}

		return 0;
	}

	protected void UpdateOpenList(PathNode p, Vec2f d, int c, boolean fly) {
		for (int i = 0; i < 4; i++) {
			Vec2f pos = ZoneInteractionHandler.getPos(i * 2, p.m_position);
			if (pos.x >= 0 && pos.x < m_zone.getWidth() && pos.y >= 0 && pos.y < m_zone.getHeight()) {
				int v = CheckTile(pos);
				if (v > 0) {
					int add = 0;
					if (p.m_direction != i * 2) {
						add += 3;
					}
					if (v == 1) {
						add += 3;

					}
					if (v == 2) {
						add += 0;
					}
					m_openlist.add(new PathNode(pos, i * 2, c + 1, pos.getDistance(d) + add - 1, p));
				}

			}
		}
	}

}
