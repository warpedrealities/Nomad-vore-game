package worldgentools;

import java.util.ArrayList;

import nomad.Universe;
import worldgentools.auditing.CarvingPathfinder;
import worldgentools.auditing.FloodPathfinder;
import zone.TileDef.TileMovement;
import zone.Zone;
import shared.Vec2f;
import shared.Vec2i;
import view.ZoneInteractionHandler;;

public class AuditTool {

	Zone zone;
	PointsOfInterest pointsOfInterest;

	ArrayList<Vec2i> points;

	public AuditTool(Zone zone, PointsOfInterest poi) {
		pointsOfInterest = poi;
		this.zone = zone;
		points = new ArrayList<Vec2i>();
	}

	private void createPoints(boolean widgets, boolean npcs, boolean pointsOfInterest, int exclude) {
		if (widgets == true) {
			for (int i = 0; i < zone.getWidth(); i++) {
				for (int j = 0; j < zone.getHeight(); j++) {
					if (zone.getTile(i, j) != null && zone.getTile(i, j).getWidgetObject() != null) {
						if (exclude == 0) {
							if (zone.getTile(i, j).getWidgetObject().Walkable()) {
								points.add(new Vec2i(i, j));
							} else {
								points.add(genNearbyP(i, j));
							}

						}
						if (exclude == 1 && zone.getTile(i, j).getWidgetObject().Walkable() == true) {
							points.add(new Vec2i(i, j));
						}
					}
				}
			}
		}
		if (npcs == true) {
			for (int i = 0; i < zone.getActors().size(); i++) {
				points.add(new Vec2i((int) zone.getActors().get(i).getPosition().x,
						(int) zone.getActors().get(i).getPosition().y));
			}
		}
		if (pointsOfInterest == true) {
			for (int i = 0; i < this.pointsOfInterest.pointList.size(); i++) {
				points.add(
						new Vec2i(this.pointsOfInterest.pointList.get(i).x, this.pointsOfInterest.pointList.get(i).y));
			}
		}
	}

	private boolean checkTile(Vec2i t) {
		if (zone.getTile((int) t.x, (int) t.y) == null) {
			return false;
		}
		if (zone.getTile((int) t.x, (int) t.y).getWidgetObject() != null) {
			return false;
		}
		if (zone.getTile((int) t.x, (int) t.y).getDefinition().getMovement() != TileMovement.WALK) {
			return false;
		}
		return true;
	}

	private Vec2i genNearbyP(int i, int j) {

		int r = Universe.m_random.nextInt(8);
		Vec2i p0 = new Vec2i(i, j);
		Vec2i p = ZoneInteractionHandler.getPos(r, p0);
		while (checkTile(p) == false) {
			r = r + 1;
			if (r > 7) {
				r = 0;
			}
			p = ZoneInteractionHandler.getPos(r, p0);
		}
		return p;
	}

	public void runPathCarver(int tunneledTile, boolean widgets, boolean npcs, boolean pointsOfInterest, int replace,
			int exclude) {
		createPoints(widgets, npcs, pointsOfInterest, exclude);
		ArrayList<Vec2i> unreachedpoints = new FloodPathfinder(zone).runFlood(points);
		if (unreachedpoints.size() > 0) {
			ArrayList<Vec2i> reachable = new ArrayList<Vec2i>(points);
			reachable.removeAll(unreachedpoints);
			new CarvingPathfinder(zone, tunneledTile).runCarving(reachable, unreachedpoints, replace);
		}
	}

}
