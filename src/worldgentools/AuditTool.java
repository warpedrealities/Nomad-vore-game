package worldgentools;

import java.util.ArrayList;

import nomad.universe.Universe;
import worldgentools.auditing.CarvingPathfinder;
import worldgentools.auditing.FloodPathfinder;
import worldgentools.auditing.VoidPathfinder;
import zone.TileDef.TileMovement;
import zone.Tile;
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

	private void createPoints(boolean widgets, boolean npcs, boolean pointsOfInterest, int exclude,int replace) {
		if (zone.getLandingSite()!=null)
		{
			points.add(new Vec2i((int)zone.getLandingSite().x,(int)zone.getLandingSite().y));
		}
		if (widgets == true) {
			for (int i = 0; i < zone.getWidth(); i++) {
				for (int j = 0; j < zone.getHeight(); j++) {
					if (zone.getTile(i, j) != null && zone.getTile(i, j).getWidgetObject() != null) {
						if (exclude == 0) {
							if (zone.getTile(i, j).getWidgetObject().Walkable()) {
								points.add(new Vec2i(i, j));
							} else {
								points.add(genNearbyP(i, j,replace));
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

	private Vec2i genNearbyP(int i, int j,int replace) {

		int r = Universe.m_random.nextInt(8);
		Vec2i p0 = new Vec2i(i, j);
		Vec2i p = ZoneInteractionHandler.getPos(r, p0);
		int d=0;
		while (checkTile(p) == false) {
			r = r + 1;
			if (r > 7) {
				r = 0;
			}
			p = ZoneInteractionHandler.getPos(r, p0);
			d++;
			if (d>8)
			{
				if (zone.getTile((int) p.x, (int) p.y).getWidgetObject()== null)
				{
					zone.getTiles()[(int) p.x][(int)p.y] = new Tile(
							(int) p.x, (int) p.y,
							zone.getZoneTileLibrary().getDef(replace), zone, zone.getZoneTileLibrary());
				}
			}
		}
		return p;
	}

	public void runPathCarver(int tunneledTile, boolean widgets, boolean npcs, boolean pointsOfInterest, int replace,
			int exclude) {
		createPoints(widgets, npcs, pointsOfInterest, exclude,replace);
		ArrayList<Vec2i> unreachedpoints = new FloodPathfinder(zone).runFlood(points);
		if (unreachedpoints.size() > 0) {
			ArrayList<Vec2i> reachable = new ArrayList<Vec2i>(points);
			reachable.removeAll(unreachedpoints);
			new CarvingPathfinder(zone, tunneledTile).runCarving(reachable, unreachedpoints, replace);
		}
	}
	public void runMakePath(int tunneledTile, boolean widgets, boolean npcs, boolean pointsOfInterest, int replace,
			int exclude, boolean random) {
		createPoints(widgets, npcs, pointsOfInterest, exclude,replace);
		ArrayList<Vec2i> unreachedpoints = new FloodPathfinder(zone).runFlood(points);
		if (unreachedpoints.size() > 0) {
			ArrayList<Vec2i> reachable = new ArrayList<Vec2i>(points);
			reachable.removeAll(unreachedpoints);
			new VoidPathfinder(zone, tunneledTile).run(reachable, unreachedpoints, replace,random);
			unreachedpoints= new FloodPathfinder(zone).runFlood(points);
			if (unreachedpoints.size() > 0) {
				new VoidPathfinder(zone, tunneledTile).run(reachable, unreachedpoints, replace,random);			
			}
		}
	}
}
