package worldgentools;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import artificial_intelligence.pathfinding.PathNode;
import shared.Vec2i;
import view.ZoneInteractionHandler;
import vmo.GameManager;
import worldgentools.gridtown.GridTownPathfinder;
import zone.Tile;
import zone.TileDefLibrary;
import zone.Zone;

public class GridTownGenerator {

	Tile[][] m_tiles;
	boolean[][] m_grid;
	int m_width, m_height;
	boolean[][] m_worksheet;
	TileDefLibrary m_library;
	int m_failcount;
	Zone m_zone;

	boolean[][] houseGrid;

	PointsOfInterest pointsOfInterest;
	ArrayList<Vec2i> doors;

	public GridTownGenerator(Tile[][] tiles, boolean[][] grid, TileDefLibrary library, Zone zone,
			PointsOfInterest pointsOfInterest) {
		m_library = library;
		m_tiles = tiles;
		this.pointsOfInterest = pointsOfInterest;
		m_width = m_tiles.length;
		m_height = m_tiles[0].length;
		m_grid = grid;
		m_zone = zone;
		m_worksheet = new boolean[m_width][];
		for (int i = 0; i < m_height; i++) {
			m_worksheet[i] = new boolean[m_height];
		}
		doors = new ArrayList<Vec2i>();
		genHouseGrid();

	}

	private boolean checkGrid(int x, int y) {
		for (int i = x * 8; i < (x * 8) + 8; i++) {
			for (int j = y * 8; j < (y * 8) + 8; j++) {
				if (m_grid[i][j] == false) {
					return false;
				}
				if (m_tiles[i][j] != null) {
					return false;
				}
			}
		}
		return true;
	}

	private void genHouseGrid() {
		houseGrid = new boolean[m_width / 8][];
		for (int i = 0; i < houseGrid.length; i++) {
			houseGrid[i] = new boolean[m_height / 8];
			for (int j = 0; j < houseGrid[i].length; j++) {
				if (checkGrid(i, j)) {
					houseGrid[i][j] = true;
				} else {
					houseGrid[i][j] = false;
				}
			}
		}
	}

	private Vec2i getPosition() {
		// find a grid square that cant be
		while (true) {
			int rx = GameManager.m_random.nextInt(houseGrid.length);
			int ry = GameManager.m_random.nextInt(houseGrid[0].length);

			if (houseGrid[rx][ry] == true) {
				return new Vec2i(rx, ry);
			}
		}

	}

	private void buildHouse(int tile, boolean register) {
		Vec2i p = getPosition();

		int width = GameManager.m_random.nextInt(3) + 3;
		int height = GameManager.m_random.nextInt(3) + 3;
		int x = p.x * 8;
		int y = p.y * 8;
		x = x + 4;
		y = y + 4;
		x = x - (width / 2);
		y = y - (height / 2);

		for (int i = x; i < x + width; i++) {
			for (int j = y; j < y + height; j++) {
				m_worksheet[i][j] = true;
			}
		}
		houseGrid[p.x][p.y] = false;
		// build edges
		pointsOfInterest.addPOI(new Vec2i(x + 1, y + 1));
		buildWalls(tile, x, y, width, height);

		buildDoor(x, y, width, height);
	}

	private void buildWalls(int tile, int x, int y, int width, int height) {
		for (int i = y - 1; i < y + height + 1; i++) {
			m_tiles[x - 1][i] = new Tile(x - 1, i, m_library.getDef(tile), m_zone, m_library);
			m_tiles[x + width][i] = new Tile(x + width, i, m_library.getDef(tile), m_zone, m_library);
		}
		for (int i = x - 1; i < x + width + 1; i++) {
			m_tiles[i][y - 1] = new Tile(i, y - 1, m_library.getDef(tile), m_zone, m_library);
			m_tiles[i][y + height] = new Tile(i, y + height, m_library.getDef(tile), m_zone, m_library);
		}
	}

	private void buildDoor(int x, int y, int width, int height) {
		int r = GameManager.m_random.nextInt(4);
		r = r * 2;
		Vec2i offset = ZoneInteractionHandler.getPos(r, new Vec2i(0, 0));
		Vec2i p = new Vec2i(x + (width / 2), y + (height / 2));
		while (true) {
			p.x += offset.x;
			p.y += offset.y;
			if (m_worksheet[p.x][p.y] == false) {
				m_tiles[p.x][p.y] = null;
				m_worksheet[p.x][p.y] = true;
				doors.add(p);
				break;
			}
		}

	}

	private void buildHouses(int count, int tile, boolean register) {
		for (int i = 0; i < count; i++) {
			buildHouse(tile, register);
		}
	}

	public void run(Element element) {
		int minhouses = Integer.parseInt(element.getAttribute("minhouses"));
		int maxhouses = Integer.parseInt(element.getAttribute("maxhouses"));
		int tile = Integer.parseInt(element.getAttribute("tile"));
		int path = Integer.parseInt(element.getAttribute("path"));
		int housecount = GameManager.m_random.nextInt(maxhouses - minhouses) + minhouses;

		boolean b = false;
		if (element.getAttribute("registerRooms").length() > 0) {
			b = true;
		}
		// build here
		NodeList children = element.getElementsByTagName("gridprefab");
		for (int i = 0; i < children.getLength(); i++) {
			buildPrefab((Element) children.item(i));
		}

		buildHouses(housecount, tile, b);
		buildPaths(path);

	}

	private Vec2i getPrefabLocation(int width, int height) {
		while (true) {
			int rx = GameManager.m_random.nextInt(houseGrid.length);
			int ry = GameManager.m_random.nextInt(houseGrid[0].length);
			if (rx < houseGrid.length - width && ry < houseGrid[0].length - height) {
				for (int i = 0; i < width; i++) {
					for (int j = 0; j < height; j++) {
						if (houseGrid[rx + i][ry + j] == true && m_grid[(rx + i) * 8][(ry + j) * 8] == true) {
							return new Vec2i(rx * 8, ry * 8);
						}
					}
				}
			}

		}
	}

	private void buildPrefab(Element element) {
		// figure out size
		int width = Integer.parseInt(element.getAttribute("width"));
		int height = Integer.parseInt(element.getAttribute("height"));
		NodeList children = element.getChildNodes();

		Vec2i p = getPrefabLocation(width, height);

		int x = p.x;
		int y = p.y;
		int yindex = y;
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) node;
				// run each step successively
				if (Enode.getTagName() == "row") {
					String row = Enode.getTextContent();
					// load each value
					for (int j = 0; j < width * 8; j++) {
						if (m_grid[x + j][yindex] == true && m_tiles[x + j][yindex] == null) {
							int value = Integer.parseInt(row.substring(j, j + 1));
							if (value > 0) {
								m_tiles[x + j][yindex] = new Tile(x + j, yindex, m_library.getDef(value - 1), m_zone,
										m_library);
							}
						}
					}
					yindex++;
				}
				if (Enode.getTagName() == "placeDoor") {
					doors.add(new Vec2i(x + Integer.parseInt(Enode.getAttribute("x")),
							y + Integer.parseInt(Enode.getAttribute("y"))));
				}
				if (Enode.getTagName() == "placeTile") {
					int tilex = Integer.parseInt(Enode.getAttribute("x")) + x;
					int tiley = Integer.parseInt(Enode.getAttribute("y")) + y;
					int tile = Integer.parseInt(Enode.getAttribute("tile"));
					m_tiles[tilex][tiley] = new Tile(tilex, tiley, m_library.getDef(tile), m_zone, m_library);
				}
				if (Enode.getTagName() == "placeNPC") {
					new NPCPlacer(m_zone, pointsOfInterest).PlaceNPC(Enode, m_grid, x, y);
				}
				if (Enode.getTagName() == "placeItem") {
					new WidgetPlacer(m_zone).placeItem(Enode, x, y);
				}
				if (Enode.getTagName() == "placeWidget") {
					m_zone.setZoneTileGrid(m_tiles);
					new WidgetPlacer(m_zone).placeWidget(Enode, x, y);
				}
				if (Enode.getTagName() == "conditionalportal") {
					new WidgetPlacer(m_zone).placeConditionalPortal(Enode, x, y);
				}
				if (Enode.getTagName() == "scriptportal") {
					new WidgetPlacer(m_zone).placeScriptedPortal(Enode, x, y);
				}
			}
		}
		x = x / 8;
		y = y / 8;
		for (int i = x; i < x + width; i++) {
			for (int j = y; j < y + height; j++) {
				houseGrid[i][j] = false;
			}
		}
	}

	private void buildPaths(int path) {
		GridTownPathfinder pathfinder = new GridTownPathfinder(m_zone, path);
		for (int i = 0; i < doors.size() - 1; i++) {
			PathNode[] pathNodes = pathfinder.genPath(doors.get(i), doors.get(i + 1), 32, false);
			if (pathNodes != null) {
				for (int j = 0; j < pathNodes.length; j++) {
					PathNode node = pathNodes[j];
					if (m_tiles[(int) node.m_position.x][(int) node.m_position.y] == null
							&& !m_worksheet[(int) node.m_position.x][(int) node.m_position.y]) {
						m_tiles[(int) node.m_position.x][(int) node.m_position.y] = new Tile((int) node.m_position.x,
								(int) node.m_position.y, m_library.getDef(path), m_zone, m_library);
					}

				}

			}

		}
	}

	public boolean[][] getGrid() {
		return m_worksheet;
	}

	public Tile[][] getTiles() {
		return m_tiles;
	}

}
