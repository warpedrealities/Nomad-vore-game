package worldgentools.preload;

import org.w3c.dom.Element;

import shared.Vec2i;
import worldgentools.PointsOfInterest;
import zone.Tile;
import zone.Zone;
import zonePreload.ZonePreload;
import zonePreload.ZonePreloadArray;

public class PreloadUtilizer {
	private Zone m_zone;
	private PointsOfInterest pointsOfInterest;

	public PreloadUtilizer(Zone zone, PointsOfInterest pointsOfInterest) {
		m_zone = zone;
		this.pointsOfInterest = pointsOfInterest;
	}

	public void carveEdges(Element enode) {

		int preloadID = Integer.parseInt(enode.getAttribute("preloadID"));
		String side = enode.getAttribute("side");
		int tile = Integer.parseInt(enode.getAttribute("tile"));
		int replace = Integer.parseInt(enode.getAttribute("replace"));
		if (m_zone.preload == null) {
			return;
		}
		ZonePreload zp = m_zone.preload.getPreload(preloadID);
		if (zp == null || zp.getPreloadType() != ZonePreload.PRELOADARRAY) {
			return;
		}
		ZonePreloadArray preload = (ZonePreloadArray) zp;

		if (side.equals("north")) {
			carveNorthEdge(preload, tile, replace);
		}
		if (side.equals("east")) {
			carveEastEdge(preload, tile, replace);
		}
		if (side.equals("south")) {
			carveSouthEdge(preload, tile, replace);
		}
		if (side.equals("west")) {
			carveWestEdge(preload, tile, replace);
		}
	}

	private void carveNorthEdge(ZonePreloadArray preload, int tile, int replace) {

		new PreloadGuaranteePaths(m_zone).run(0, tile, replace, preload.getPreloadArray());

		int count = 0;
		int y = m_zone.getHeight() - 1;
		for (int i = 0; i < m_zone.getWidth(); i++) {
			if (preload.getPreloadArray().get(i) > 0) {
				m_zone.getTiles()[i][y] = new Tile(i, y, m_zone.getZoneTileLibrary().getDef(tile), m_zone,
						m_zone.getZoneTileLibrary());
				m_zone.getTiles()[i][y - 1] = new Tile(i, y - 1, m_zone.getZoneTileLibrary().getDef(tile), m_zone,
						m_zone.getZoneTileLibrary());
				count++;
				if (count >= 3) {
					pointsOfInterest.addPOI(new Vec2i(i, y));
					count = 0;
				}
			} else {
				m_zone.getTiles()[i][y] = new Tile(i, y, m_zone.getZoneTileLibrary().getDef(0), m_zone,
						m_zone.getZoneTileLibrary());
				count = 0;
			}
		}
	}

	private void carveEastEdge(ZonePreloadArray preload, int tile, int replace) {

		new PreloadGuaranteePaths(m_zone).run(1, tile, replace, preload.getPreloadArray());

		int count = 0;
		int x = m_zone.getWidth() - 1;
		for (int i = 0; i < m_zone.getWidth(); i++) {
			if (preload.getPreloadArray().get(i) > 0) {
				m_zone.getTiles()[x][i] = new Tile(x, i, m_zone.getZoneTileLibrary().getDef(tile), m_zone,
						m_zone.getZoneTileLibrary());
				m_zone.getTiles()[x - 1][i] = new Tile(x - 1, i, m_zone.getZoneTileLibrary().getDef(tile), m_zone,
						m_zone.getZoneTileLibrary());
				count++;
				if (count >= 3) {
					pointsOfInterest.addPOI(new Vec2i(x, i));
					count = 0;
				}
			} else {
				m_zone.getTiles()[x][i] = new Tile(x, i, m_zone.getZoneTileLibrary().getDef(0), m_zone,
						m_zone.getZoneTileLibrary());
				count = 0;
			}
		}
	}

	private void carveSouthEdge(ZonePreloadArray preload, int tile, int replace) {
		new PreloadGuaranteePaths(m_zone).run(2, tile, replace, preload.getPreloadArray());

		int count = 0;
		int y = 0;
		for (int i = 0; i < m_zone.getWidth(); i++) {
			if (preload.getPreloadArray().get(i) > 0) {
				m_zone.getTiles()[i][y] = new Tile(i, y, m_zone.getZoneTileLibrary().getDef(tile), m_zone,
						m_zone.getZoneTileLibrary());
				m_zone.getTiles()[i][y + 1] = new Tile(i, y + 1, m_zone.getZoneTileLibrary().getDef(tile), m_zone,
						m_zone.getZoneTileLibrary());
				count++;
				if (count >= 3) {
					pointsOfInterest.addPOI(new Vec2i(i, y));
					count = 0;
				}
			} else {
				m_zone.getTiles()[i][y] = new Tile(i, y, m_zone.getZoneTileLibrary().getDef(0), m_zone,
						m_zone.getZoneTileLibrary());
				count = 0;
			}
		}
	}

	private void carveWestEdge(ZonePreloadArray preload, int tile, int replace) {

		new PreloadGuaranteePaths(m_zone).run(3, tile, replace, preload.getPreloadArray());

		int count = 0;
		int x = 0;
		for (int i = 0; i < m_zone.getWidth(); i++) {
			if (preload.getPreloadArray().get(i) > 0) {
				m_zone.getTiles()[x][i] = new Tile(x, i, m_zone.getZoneTileLibrary().getDef(tile), m_zone,
						m_zone.getZoneTileLibrary());
				m_zone.getTiles()[x + 1][i] = new Tile(x + 1, i, m_zone.getZoneTileLibrary().getDef(tile), m_zone,
						m_zone.getZoneTileLibrary());
				count++;
				if (count >= 3) {
					pointsOfInterest.addPOI(new Vec2i(x, i));
					count = 0;
				}
			} else {
				m_zone.getTiles()[x][i] = new Tile(x, i, m_zone.getZoneTileLibrary().getDef(0), m_zone,
						m_zone.getZoneTileLibrary());
				count = 0;
			}
		}

	}

}
