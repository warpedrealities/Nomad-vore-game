package worldgentools;

import java.util.ArrayList;

import org.lwjgl.util.Rectangle;
import org.w3c.dom.Element;

import nomad.universe.Universe;
import shared.Vec2f;
import shared.Vec2i;
import view.ZoneInteractionHandler;
import vmo.GameManager;
import zone.Tile;
import zone.TileDef;
import zone.TileDefLibrary;
import zone.Zone;

public class TownGenerator {

	Tile[][] m_tiles;
	boolean[][] m_grid;
	int m_width, m_height;
	boolean[][] m_worksheet;
	TileDefLibrary m_library;
	int m_failcount;
	Zone m_zone;
	ArrayList<Rectangle> houses;
	PointsOfInterest pointsOfInterest;

	public TownGenerator(Tile[][] tiles, boolean[][] grid, TileDefLibrary library, Zone zone,
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

		houses = new ArrayList<Rectangle>();

	}

	public void Run(Element element) {
		int minsize = Integer.parseInt(element.getAttribute("minsize"));
		int maxsize = Integer.parseInt(element.getAttribute("maxsize"));
		int minhouses = Integer.parseInt(element.getAttribute("minhouses"));
		int maxhouses = Integer.parseInt(element.getAttribute("maxhouses"));
		int tile = Integer.parseInt(element.getAttribute("tile"));
		int gap = Integer.parseInt(element.getAttribute("gap"));
		int houseCount = minhouses;
		if (maxhouses>minhouses) {
			houseCount=GameManager.m_random.nextInt(maxhouses - minhouses);
		}
		BuildHouses(houseCount, minsize, maxsize, tile);
		BuildDoors(gap);
		if (element.getAttribute("registerRooms").length() > 0) {
			registerPointsOfInterest();
		}
	}

	private void registerPointsOfInterest() {

		for (int i = 0; i < houses.size(); i++) {
			int x = houses.get(i).getX() + (houses.get(i).getWidth() / 2);
			int y = houses.get(i).getY() + (houses.get(i).getHeight() / 2);
			pointsOfInterest.addPOI(new Vec2i(x, y));
		}
	}

	void BuildDoors(int gap) {
		for (int i = 0; i < houses.size(); i++) {
			int x = houses.get(i).getWidth() / 2;
			int y = houses.get(i).getHeight() / 2;
			int r = Universe.m_random.nextInt(4) * 2;
			Vec2f v = ZoneInteractionHandler.getPos(r, new Vec2f(0, 0));
			Vec2f p = new Vec2f(x + houses.get(i).getX(), y + houses.get(i).getY());
			while (true) {
				if (p.x < 0 || p.y < 0 || p.x == m_width || p.y == m_height) {
					break;
				}
				if (m_tiles[(int) p.x][(int) p.y] != null
						&& m_tiles[(int) p.x][(int) p.y].getDefinition().getMovement() == TileDef.TileMovement.BLOCK) {
					Vec2f p0 = new Vec2f(p.x + v.x, p.y + v.y);
					if (p0.x < 0 || p0.y < 0 || p0.x == m_width || p0.y == m_height) {
						break;
					}
					if (m_tiles[(int) p0.x][(int) p0.y] != null && m_tiles[(int) p0.x][(int) p0.y].getDefinition()
							.getMovement() == TileDef.TileMovement.BLOCK) {

					} else {
						m_tiles[(int) p.x][(int) p.y] = new Tile((int) p.x, (int) p.y, m_library.getDef(gap), m_zone,
								m_library);
						m_tiles[(int) p0.x][(int) p0.y] = new Tile((int) p0.x, (int) p0.y, m_library.getDef(gap),
								m_zone, m_library);
						break;
					}
				}
				p.x += v.x;
				p.y += v.y;

			}

		}
	}

	private boolean ValidSpot(Rectangle spot) {
		// check out of bounds
		if (spot.getX() + spot.getWidth() >= m_width || spot.getY() + spot.getHeight() >= m_height) {
			return false;
		}
		// check valid tile
		if (m_grid[spot.getX()][spot.getY()] == false || m_tiles[spot.getX()][spot.getY()] != null) {
			return false;
		}
		// check all tiles are valid
		for (int i = spot.getX(); i < spot.getX() + spot.getWidth() + 1; i++) {
			for (int j = spot.getY(); j < spot.getY() + spot.getHeight() + 1; j++) {
				if (m_grid[i][j] == false || m_tiles[i][j] != null) {
					return false;
				}

			}
		}
		// check intersection with all existing houses
		if (houses.size() > 0) {
			for (int i = 0; i < houses.size(); i++) {
				if (houses.get(i).intersects(spot)) {
					return false;
				}
			}
		}

		return true;
	}

	private void BuildHouses(int housecount, int minsize, int maxsize, int tile) {
		while (houses.size() < housecount) {
			// try to build a house

			// pick a spot
			Rectangle r = new Rectangle(GameManager.m_random.nextInt(m_width), GameManager.m_random.nextInt(m_height),
					0, 0);
			int width=minsize; int height=minsize;
			if (maxsize>minsize)
			{
				width+=GameManager.m_random.nextInt(maxsize - minsize);
				height+=GameManager.m_random.nextInt(maxsize - minsize);
			}
			r.setSize(width,
					height);
			if (ValidSpot(r)) {
				m_failcount = 0;
				houses.add(r);
				PaintHouse(r, tile);
			} else {
				m_failcount++;
				if (m_failcount > 32) {
					break;
				}
			}
		}
	}

	private void PaintHouse(Rectangle r, int tile) {
		for (int i = r.getX(); i <= r.getX() + r.getWidth(); i++) {
			m_tiles[i][r.getY()] = new Tile(i, r.getY(), m_library.getDef(tile), m_zone, m_library);
			m_tiles[i][r.getY() + r.getHeight()] = new Tile(i, r.getY(), m_library.getDef(tile), m_zone, m_library);
		}
		for (int i = r.getY(); i <= r.getY() + r.getHeight(); i++) {
			m_tiles[r.getX()][i] = new Tile(r.getX(), i, m_library.getDef(tile), m_zone, m_library);
			m_tiles[r.getX() + r.getWidth()][i] = new Tile(r.getX() + r.getWidth(), i, m_library.getDef(tile), m_zone,
					m_library);
		}
		for (int i = r.getX() + 1; i <= r.getX() + r.getWidth() - 1; i++) {
			for (int j = r.getY() + 1; j <= r.getY() + r.getHeight() - 1; j++) {
				if (m_tiles[i][j] == null) {
					m_worksheet[i][j] = true;
				}

			}
		}
	}

	public Tile[][] getTiles() {
		return m_tiles;
	}

	public boolean[][] getGrid() {
		return m_worksheet;
	}

}
