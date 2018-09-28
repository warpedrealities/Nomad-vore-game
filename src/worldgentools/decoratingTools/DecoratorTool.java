package worldgentools.decoratingTools;

import org.w3c.dom.Element;

import shared.Vec2i;
import zone.Tile;
import zone.TileDefLibrary;
import zone.Zone;

public class DecoratorTool {

	Tile[][] m_tiles;

	int m_width, m_height;
	TileDefLibrary m_library;
	Zone m_zone;
	DecoratorPlacer placer;

	public DecoratorTool(Tile[][] tiles, TileDefLibrary library, Zone zone) {
		m_library = library;
		m_tiles = tiles;
		m_width = m_tiles.length;
		m_height = m_tiles[0].length;

		m_zone = zone;
	}

	private void setPlacer(Element placenode) {
		if (placenode.getTagName().equals("tileplacer")) {
			placer = new DecoratorTilePlacer(placenode);
		}
		if (placenode.getTagName().equals("widgetplacer")) {
			placer = new DecoratorWidgetPlacer(placenode);
		}
	}

	public void runCorners(Vec2i point, Element placenode, int floor) {
		setPlacer(placenode);
		doNWCorner(point, floor);
		doNECorner(point, floor);
		doSECorner(point, floor);
		doSWCorner(point, floor);
	}

	private boolean boundsCheck(int x, int y) {
		if (x < 0 || x >= m_width) {
			return false;
		}
		if (y < 0 || y >= m_height) {
			return false;
		}
		if (m_tiles[x][y] == null) {
			return false;
		}
		return true;
	}

	private boolean checkTile(int floor, int x, int y, boolean north, boolean east, boolean south, boolean west) {
		if (!boundsCheck(x + 1, y) || !boundsCheck(x - 1, y) || !boundsCheck(x, y + 1) || !boundsCheck(x, y - 1)) {
			return false;
		}
		if (m_tiles[x][y].getDefinition().getID() != floor) {
			return false;
		}
		if (!north && m_tiles[x][y + 1].getDefinition().getID() != floor) {
			return false;
		}
		if (!south && m_tiles[x][y - 1].getDefinition().getID() != floor) {
			return false;
		}
		if (!west && m_tiles[x - 1][y].getDefinition().getID() != floor) {
			return false;
		}
		if (!east && m_tiles[x + 1][y].getDefinition().getID() != floor) {
			return false;
		}
		if (north && m_tiles[x][y + 1].getDefinition().getID() == floor) {
			return false;
		}
		if (south && m_tiles[x][y - 1].getDefinition().getID() == floor) {
			return false;
		}
		if (west && m_tiles[x - 1][y].getDefinition().getID() == floor) {
			return false;
		}
		if (east && m_tiles[x + 1][y].getDefinition().getID() == floor) {
			return false;
		}
		return true;
	}

	private void doSWCorner(Vec2i point, int floor) {
		for (int i = point.x; i > point.x - 5; i--) {
			for (int j = point.y; j > point.y - 5; j--) {
				if (boundsCheck(i, j)) {
					if (checkTile(floor, i, j, false, false, true, true)) {
						placer.place(i, j, m_zone);

						return;
					}
				}

			}
		}
	}

	private void doNECorner(Vec2i point, int floor) {
		for (int i = point.x; i < point.x + 5; i++) {
			for (int j = point.y; j < point.y + 5; j++) {
				if (boundsCheck(i, j)) {
					if (checkTile(floor, i, j, true, true, false, false)) {
						placer.place(i, j, m_zone);
						return;
					}
				}

			}
		}
	}

	private void doSECorner(Vec2i point, int floor) {
		for (int i = point.x; i < point.x + 5; i++) {
			for (int j = point.y; j > point.y - 5; j--) {
				if (boundsCheck(i, j)) {
					if (checkTile(floor, i, j, false, true, true, false)) {
						placer.place(i, j, m_zone);
						return;
					}
				}

			}
		}
	}

	private void doNWCorner(Vec2i point, int floor) {
		for (int i = point.x; i > point.x - 5; i--) {
			for (int j = point.y; j < point.y + 5; j++) {
				if (boundsCheck(i, j)) {
					if (checkTile(floor, i, j, true, false, false, true)) {
						placer.place(i, j, m_zone);
						return;
					}
				}

			}
		}
	}

}
