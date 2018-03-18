package worldgentools.preload;

import org.w3c.dom.Element;

import nomad.Entity;
import nomad.universe.Universe;
import shared.Vec2i;
import worldgentools.PointsOfInterest;
import zone.Tile;
import zone.TileDef.TileMovement;
import zone.Zone;
import zonePreload.ZonePreloadArray;
import zonePreload.ZonePreloadVector;

public class PreloadTools {

	private Zone m_zone;
	private PointsOfInterest pointsOfInterest;

	public PreloadTools(Zone zone, PointsOfInterest pointsOfInterest) {
		m_zone = zone;
		this.pointsOfInterest = pointsOfInterest;
	}

	public void recordEdges(Element enode) {
		String destination = enode.getAttribute("zone");
		Entity e = (Entity) m_zone.getZoneEntity();
		Zone z = e.getZone(destination);
		int preloadID = Integer.parseInt(enode.getAttribute("preloadID"));
		String side = enode.getAttribute("side");
		if (z != null) {
			if (side.equals("north")) {
				recordNorthEdge(z, preloadID);
			}
			if (side.equals("east")) {
				recordEastEdge(z, preloadID);
			}
			if (side.equals("south")) {
				recordSouthEdge(z, preloadID);
			}
			if (side.equals("west")) {
				recordWestEdge(z, preloadID);
			}

		}

	}

	private void recordNorthEdge(Zone z, int preloadID) {
		ZonePreloadArray zp = new ZonePreloadArray(preloadID);
		// top y line
		int y = m_zone.getHeight() - 1;
		int count = 0;
		for (int i = 0; i < m_zone.getWidth(); i++) {
			Tile t = m_zone.getTiles()[i][y];
			if (t != null && t.getDefinition().getMovement() == TileMovement.WALK) {
				zp.getPreloadArray().add(1);
				count++;
				if (count >= 3) {
					pointsOfInterest.addPOI(new Vec2i(i, y));
					count = 0;
				}
			} else {
				zp.getPreloadArray().add(0);
				count = 0;
			}
		}
		z.addPreload(zp);

	}

	private void recordEastEdge(Zone z, int preloadID) {
		ZonePreloadArray zp = new ZonePreloadArray(preloadID);
		// last x column
		int x = m_zone.getHeight() - 1;
		int count = 0;
		for (int i = 0; i < m_zone.getHeight(); i++) {
			Tile t = m_zone.getTiles()[x][i];
			if (t != null && t.getDefinition().getMovement() == TileMovement.WALK) {
				zp.getPreloadArray().add(1);
				count++;
				if (count >= 3) {
					pointsOfInterest.addPOI(new Vec2i(x, i));
					count = 0;
				}
			} else {
				zp.getPreloadArray().add(0);
				count = 0;
			}
		}
		z.addPreload(zp);
	}

	private void recordSouthEdge(Zone z, int preloadID) {
		ZonePreloadArray zp = new ZonePreloadArray(preloadID);
		// first y line
		int y = 0;
		int count = 0;
		for (int i = 0; i < m_zone.getWidth(); i++) {
			Tile t = m_zone.getTiles()[i][y];
			if (t != null && t.getDefinition().getMovement() == TileMovement.WALK) {
				zp.getPreloadArray().add(1);
				count++;
				if (count >= 3) {
					pointsOfInterest.addPOI(new Vec2i(i, y));
					count = 0;
				}
			} else {
				zp.getPreloadArray().add(0);
				count = 0;
			}
		}
		z.addPreload(zp);
	}

	private void recordWestEdge(Zone z, int preloadID) {
		ZonePreloadArray zp = new ZonePreloadArray(preloadID);
		// first x column
		int x = 0;
		int count = 0;
		for (int i = 0; i < m_zone.getHeight(); i++) {
			Tile t = m_zone.getTiles()[x][i];
			if (t != null && t.getDefinition().getMovement() == TileMovement.WALK) {
				zp.getPreloadArray().add(1);
				count++;
				if (count >= 3) {
					pointsOfInterest.addPOI(new Vec2i(x, i));
					count = 0;
				}
			} else {
				zp.getPreloadArray().add(0);
				count = 0;
			}
		}
		z.addPreload(zp);
	}

	public void recordVector(Element enode, int x, int y) {
		// TODO Auto-generated method stub
		int x0 = Integer.parseInt(enode.getAttribute("x"));
		int y0 = Integer.parseInt(enode.getAttribute("y"));
		int id = Integer.parseInt(enode.getAttribute("ID"));
		String zone = enode.getAttribute("zone");
		ZonePreloadVector zpv = new ZonePreloadVector(new Vec2i(x0 + x, y0 + y), id);
		Zone z = Universe.getInstance().getZone(zone);
		if (z != null) {
			z.addPreload(zpv);
		}
	}
}
