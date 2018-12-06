package worldgentools;

import java.util.ArrayList;
import java.util.Random;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import shared.Vec2f;
import shared.Vec2i;
import shipsystem.droneSpawning.DroneHandling;
import vmo.GameManager;
import widgets.WidgetDescription;
import widgets.WidgetPortal;
import widgets.WidgetSprite;
import worldgentools.blockdungeon.BlockDungeonGenerator;
import worldgentools.blockdungeon.advanced.AdvancedBlockDungeonGenerator;
import worldgentools.decoratingTools.DecoratorTool;
import worldgentools.nodeMapGenerator.NodeMapGenerator;
import worldgentools.preload.PreloadTools;
import worldgentools.preload.PreloadUtilizer;
import zone.Landing;
import zone.Tile;
import zone.TileDefLibrary;
import zone.Zone;
import zone.Zone.zoneType;
import zonePreload.ZonePreloadVector;

public class ZoneBuildTools {

	Zone m_zone;
	public Tile[][] m_tiles;
	int m_width, m_height;
	TileDefLibrary m_library;
	Random m_random;
	String m_name;
	PointsOfInterest pointsOfInterest;

	public ZoneBuildTools(String name, Zone zone) {
		m_zone = zone;
		m_name = name;
		pointsOfInterest = new PointsOfInterest();
		m_random = GameManager.m_random;
		m_width = m_zone.zoneWidth;
		m_height = m_zone.zoneHeight;
		m_library = m_zone.zoneTileLibrary;
		if (m_zone.getTiles() != null) {
			m_tiles = m_zone.getTiles();
		} else {
			m_tiles = new Tile[m_width][];
			for (int i = 0; i < m_width; i++) {
				m_tiles[i] = new Tile[m_height];
			}
			m_zone.setZoneTileGrid(m_tiles);
		}

	}

	public ZoneBuildTools(Zone zone, Tile[][] tiles) {
		m_zone = zone;
		m_random = new Random();
		pointsOfInterest = new PointsOfInterest();
		m_library = m_zone.zoneTileLibrary;
		m_tiles = tiles;
		m_width = m_zone.zoneWidth;
		m_height = m_zone.zoneHeight;
	}

	public void BuildShips(ArrayList<Landing> ships, Vec2f position) {
		if (ships != null) {
			if (ships.size() > 0) {
				for (int i = 0; i < ships.size(); i++) {
					Landing ship = ships.get(i);
					if (ship.landingZoneX == (int) position.x && ship.landingZoneY == (int) position.y
							&& m_zone.getType() != zoneType.CLOSED) {
						AddShip(ships.get(i));
					}
				}
			}
		}

	}

	public void AddShip(Landing landing) {
		// draw ship exterior
		Element node = landing.getShip().getExterior();
		int width = Integer.parseInt(node.getAttribute("width"));

		int x = (int) landing.landingLocation.x;
		int y = (int) landing.landingLocation.y;
		int yindex = y;
		NodeList children = node.getChildNodes();

		WidgetPortal portals[] = new WidgetPortal[2];

		for (int i = 0; i < children.getLength(); i++) {
			Node Nnode = children.item(i);
			if (Nnode.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) Nnode;
				// run each step successively
				if (Enode.getTagName() == "row") {
					String row = Enode.getTextContent();
					// load each value
					for (int j = 0; j < width; j++) {
						int value = Integer.parseInt(row.substring(j, j + 1));
						if (value > 0) {
							m_tiles[x + j][yindex] = new Tile(x + j, yindex, m_library.getDef(value - 1), m_zone,
									m_library);
						} else if (m_tiles[x + j][yindex] == null) {
							m_tiles[x + j][yindex] = new Tile(x + j, yindex, m_library.getDef(1), m_zone, m_library);
						}
					}
					yindex++;
				}
				if (Enode.getTagName() == "pairedportal") {
					portals[0] = PairedPortal(Enode, x, y);
					portals[0].setDestination(portals[0].getTarget() + landing.getShip().getUID(), portals[0].getID());
				}
				if (Enode.getTagName() == "describer") {
					Describer(Enode, x, y);
				}

			}
		}
		Tile tile = m_tiles[x][y];
		WidgetSprite sprite = new WidgetSprite(landing.getShip().getSprite(), width, yindex - y);
		tile.setWidget(sprite);
		// connect exterior portal
		new DroneHandling().droneDeployment(landing,m_zone);
	}

	public void RemoveShip(Landing landing) {
		// draw ship exterior
		Element node = landing.getShip().getExterior();
		int width = Integer.parseInt(node.getAttribute("width"));

		int x = (int) landing.landingLocation.x;
		int y = (int) landing.landingLocation.y;
		int yindex = y;
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node Nnode = children.item(i);
			if (Nnode.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) Nnode;
				// run each step successively
				if (Enode.getTagName() == "row") {
					String row = Enode.getTextContent();
					// load each value
					for (int j = 0; j < width; j++) {
						int value = Integer.parseInt(row.substring(j, j + 1));
						if (value > 0) {
							m_tiles[x + j][yindex] = new Tile(x + j, yindex, m_library.getDef(1), m_zone, m_library);
						}
						if (m_tiles[x + j][yindex].getWidgetObject() != null) {
							m_tiles[x + j][yindex].setWidget(null);
						}
					}
					yindex++;
				}
			}
		}
		if (m_tiles[x][y] != null) {
			m_tiles[x][y].setWidget(null);
		}
		new DroneHandling().droneRemoval(landing,m_zone);
	}

	void Floodfill(Element floodnode, boolean[][] grid) {
		int sprite = Integer.parseInt(floodnode.getAttribute("tile"));
		for (int i = 0; i < m_width; i++) {
			for (int j = 0; j < m_height; j++) {
				if (grid[i][j] == true && m_tiles[i][j] == null) {
					m_tiles[i][j] = new Tile(i, j, m_library.getDef(sprite), m_zone, m_library);
				} else {
					grid[i][j] = false;
				}
			}
		}

		NextPhase(floodnode, grid);
	}

	float[][] Noise() {
		float grid[][] = new float[m_width][];
		for (int i = 0; i < m_width; i++) {
			grid[i] = new float[m_height];
			for (int j = 0; j < m_height; j++) {
				grid[i][j] = m_random.nextFloat();
			}
		}
		return grid;
	}

	float[][] TripleNoise() {
		float grid0[][] = Noise();
		float grid1[][] = Noise();
		float grid2[][] = Noise();
		for (int i = 0; i < m_width; i++) {
			for (int j = 0; j < m_height; j++) {
				float zero = grid0[i][j];
				float one = grid1[i][j];
				float two = grid2[i][j];
				float average = (zero + one + two) / 3;
				grid0[i][j] = average;
			}
		}

		return grid0;
	}

	float[][] Average(float[][] noise) {
		float averaged[][] = new float[m_width][];
		for (int i = 0; i < m_width; i++) {
			averaged[i] = new float[m_height];
			for (int j = 0; j < m_height; j++) {
				averaged[i][j] = noise[i][j];
				// left right averaging

				if (i > 0 && i < m_width - 1) {
					float v = noise[i][j] + noise[i - 1][j] + noise[i + 1][j];
					v = v / 3;
					averaged[i][j] = v;
				}
				// up down averaging
				if (j > 0 && j < m_height - 1) {
					float v = noise[i][j + 1] + noise[i][j - 1] + averaged[i][j];
					v = v / 3;
					averaged[i][j] = v;
				}
			}
		}
		return averaged;
	}

	void Clumps(Element clumpnode, boolean[][] grid) {
		float noise[][] = TripleNoise();
		noise = Average(Average(noise));
		float lowerlimit = Integer.parseInt(clumpnode.getAttribute("lowerlimit"));
		float upperlimit = Integer.parseInt(clumpnode.getAttribute("upperlimit"));
		lowerlimit = lowerlimit / 100;
		upperlimit = upperlimit / 100;

		for (int i = 0; i < m_width; i++) {
			for (int j = 0; j < m_height; j++) {
				if (noise[i][j] > lowerlimit && noise[i][j] < upperlimit && grid[i][j] == true) {
					grid[i][j] = true;
				} else {
					grid[i][j] = false;
				}

			}
		}

		NextPhase(clumpnode, grid);
	}

	void Noise(Element noisenode, boolean[][] grid) {
		int sprite = Integer.parseInt(noisenode.getAttribute("tile"));
		int control = Integer.parseInt(noisenode.getAttribute("scarcity"));
		for (int i = 0; i < m_width; i++) {
			for (int j = 0; j < m_height; j++) {
				if (grid[i][j] == true && m_tiles[i][j] == null && (m_random.nextInt() % control) == 0) {

					m_tiles[i][j] = new Tile(i, j, m_library.getDef(sprite), m_zone, m_library);
				}
			}
		}

	}

	boolean[][] CloneOverlay(boolean[][] overlay) {
		boolean[][] grid = new boolean[m_width][];
		for (int i = 0; i < m_width; i++) {
			grid[i] = new boolean[m_height];
			for (int j = 0; j < m_height; j++) {
				grid[i][j] = overlay[i][j];
			}
		}

		return grid;
	}

	boolean[][] GenOverlay() {
		boolean[][] grid = new boolean[m_width][];
		for (int i = 0; i < m_width; i++) {
			grid[i] = new boolean[m_height];
			for (int j = 0; j < m_height; j++) {
				grid[i][j] = true;
			}
		}

		return grid;
	}

	void RandPrefab(Element Pnode, boolean[][] grid) {
		// check width and height
		int width = Integer.parseInt(Pnode.getAttribute("width"));
		int height = Integer.parseInt(Pnode.getAttribute("height"));
		// find a place to put it down
		int x = m_random.nextInt(m_width - width), y = m_random.nextInt(m_height - height);

		while (Check(x, y, width, height, grid) == false) {
			x = m_random.nextInt(m_width - width);
			y = m_random.nextInt(m_height - height);
		}

		// then use the regular prefab code
		Prefab(Pnode, grid, x, y, false);

	}

	void preloadPrefab(Element pnode, boolean[][] grid) {
		int id = Integer.parseInt(pnode.getAttribute("preloadID"));
		int width = Integer.parseInt(pnode.getAttribute("width"));
		int height = Integer.parseInt(pnode.getAttribute("height"));

		if (m_zone.preload != null && ZonePreloadVector.class.isInstance(m_zone.preload.getPreload(id))) {
			ZonePreloadVector zpv = (ZonePreloadVector) m_zone.preload.getPreload(id);
			Prefab(pnode, grid, zpv.getPosition().x - (width / 2), zpv.getPosition().y - (height / 2), false);

			return;
		}
		RandPrefab(pnode, grid);
	}

	private void POIPrefab(Element enode, boolean[][] grid) {

		Vec2i p = pointsOfInterest.getNextPOI();
		int width = Integer.parseInt(enode.getAttribute("width"));
		int height = Integer.parseInt(enode.getAttribute("height"));
		int x = p.x - (width / 2);
		int y = p.y - (height / 2);
		Prefab(enode, grid, x, y, false);
	}

	boolean Check(int x, int y, int width, int height, boolean[][] grid) {
		for (int i = x; i < x + width; i++) {
			for (int j = y; j < y + height; j++) {
				if (grid[i][j] == false) {
					return false;
				}
				if (m_tiles[i][j] != null) {
					return false;
				}
			}
		}

		return true;
	}

	void Prefab(Element Pnode, boolean[][] grid, int xoffset, int yoffset, boolean override) {
		int width = Integer.parseInt(Pnode.getAttribute("width"));
		int x = xoffset;
		if (Pnode.getAttribute("positionX").length() > 0) {
			x = Integer.parseInt(Pnode.getAttribute("positionX")) + x;
		}
		int y = yoffset;
		if (Pnode.getAttribute("positionY").length() > 0) {
			y = Integer.parseInt(Pnode.getAttribute("positionY")) + y;
		}
		int yindex = y;
		NodeList children = Pnode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) node;
				// run each step successively
				if (Enode.getTagName() == "row") {
					String row = Enode.getTextContent();
					// load each value
					int index=0, read=0;
					while (index<width && read<row.length())
					{
						char c=row.charAt(read);
						read++;
						if (Character.isDigit(c))
						{
							if (grid[x + index][yindex] == true && (m_tiles[x + index][yindex] == null || override)) {
								int value = Character.getNumericValue(c);
								if (value > 0) {
									m_tiles[x + index][yindex] = new Tile(x + index, yindex, m_library.getDef(value - 1), m_zone,
											m_library);
								}
							}
							index++;
						}

					}
					yindex++;
				}
				if (Enode.getTagName() == "pairedportal") {
					PairedPortal(Enode, x, y);
				}
				if (Enode.getTagName() == "placeTile") {
					int tilex = Integer.parseInt(Enode.getAttribute("x")) + x;
					int tiley = Integer.parseInt(Enode.getAttribute("y")) + y;
					int tile = Integer.parseInt(Enode.getAttribute("tile"));
					m_tiles[tilex][tiley] = new Tile(tilex, tiley, m_library.getDef(tile), m_zone, m_library);
				}
				if (Enode.getTagName() == "placeNPC") {
					new NPCPlacer(m_zone, pointsOfInterest).PlaceNPC(Enode, grid, x, y);
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
				if (Enode.getTagName() == "recordVector") {
					new PreloadTools(m_zone, pointsOfInterest).recordVector(Enode, x, y);
				}
			}
		}
	}

	void NextPhase(Element Pnode, boolean grid[][]) {
		boolean[][] ngrid;
		NodeList children = Pnode.getChildNodes();
		if (children.getLength() > 0) {
			for (int i = 0; i < children.getLength(); i++) {
				Node node = children.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element Enode = (Element) node;
					// run each step successively
					if (Enode.getTagName() == "floodfill") {
						ngrid = CloneOverlay(grid);
						Floodfill(Enode, ngrid);
					}
					if (Enode.getTagName() == "conditionalportal") {
						new WidgetPlacer(m_zone).placeConditionalPortal(Enode, 0, 0);
					}
					if (Enode.getTagName() == "perlin") {
						ngrid = CloneOverlay(grid);
						PerlinFramework.useFramework(ngrid, Enode, this);
					}
					if (Enode.getTagName() == "selectTile") {
						ngrid = CloneOverlay(grid);
						selectTile(Enode, ngrid);
					}
					if (Enode.getTagName() == "auditpaths") {
						AuditTool tool = new AuditTool(m_zone, pointsOfInterest);
						int replace = 0;
						if (Enode.getAttribute("replace").length() > 0) {
							replace = Integer.parseInt(Enode.getAttribute("replace"));
						}
						int widgetselection = 0;
						if (Enode.getAttribute("exclude").equals("impassable")) {
							widgetselection = 1;
						}
						tool.runPathCarver(Integer.parseInt(Enode.getAttribute("carve")), true, true, true, replace,
								widgetselection);
					}
					if (Enode.getTagName() == "auditpathsthroughvoid") {
						AuditTool tool = new AuditTool(m_zone, pointsOfInterest);
						int replace = 0; boolean random=false;
						if (Enode.getAttribute("replace").length() > 0) {
							replace = Integer.parseInt(Enode.getAttribute("replace"));
						}
						int widgetselection = 0;
						if (Enode.getAttribute("exclude").equals("impassable")) {
							widgetselection = 1;
						}
						if (Enode.getAttribute("random").equals("true")) {
							random=true;
						}
						tool.runMakePath(Integer.parseInt(Enode.getAttribute("carve")), true, true, true, replace,
								widgetselection,random);
					}
					if (Enode.getTagName() == "noise") {
						ngrid = CloneOverlay(grid);
						Noise(Enode, ngrid);
					}
					if (Enode.getTagName() == "clumps") {
						ngrid = CloneOverlay(grid);
						Clumps(Enode, ngrid);
					}
					if (Enode.getTagName() == "partition") {
						grid = CloneOverlay(grid);
						Partition(Enode, grid);
					}
					if (Enode.getTagName() == "blockDungeon") {
						grid = CloneOverlay(grid);
						new BlockDungeonGenerator(m_zone, m_tiles).run(Enode, grid);
					}

					if (Enode.getTagName() == "SeedNPCs") {
						grid = CloneOverlay(grid);
						new NPCPlacer(m_zone, pointsOfInterest).SeedNPCs(Enode, grid);
					}
					if (Enode.getTagName() == "PlaceNPC") {
						grid = CloneOverlay(grid);
						new NPCPlacer(m_zone, pointsOfInterest).PlaceNPC(Enode, grid, 0, 0);
					}
					if (Enode.getTagName() == "placeItem") {
						new WidgetPlacer(m_zone).placeItem(Enode, 0, 0);
					}
					if (Enode.getTagName() == "POINPC") {
						grid = GenOverlay();
						new NPCPlacer(m_zone, pointsOfInterest).placeNPCPOI(Enode, grid);
					}
					if (Enode.getTagName() == "POIITEM") {
						grid = GenOverlay();
						new WidgetPlacer(m_zone, pointsOfInterest).placeItemPOI(Enode, pointsOfInterest);
					}
					if (Enode.getTagName() == "Seedwidgets") {
						grid = CloneOverlay(grid);
						new WidgetPlacer(m_zone).SeedWidgets(Enode, grid);
					}
					if (Enode.getTagName() == "degrade") {
						grid = CloneOverlay(grid);
						Degrade(Enode, grid);
					}
					if (Enode.getTagName() == "cavegen") {
						grid = CloneOverlay(grid);
						CaveGen(Enode, grid);
					}
					if (Enode.getTagName() == "town") {
						grid = CloneOverlay(grid);
						TownGen(Enode, grid);
					}
					if (Enode.getTagName() == "towngrid") {
						grid = CloneOverlay(grid);
						gridTown(Enode, grid);
					}

					if (Enode.getTagName() == "randprefab") {
						grid = CloneOverlay(grid);
						RandPrefab(Enode, grid);
					}
					if (Enode.getTagName() == "POIprefab") {
						grid = GenOverlay();
						POIPrefab(Enode, grid);
					}
					if (Enode.getTagName() == "POIWidget") {
						grid = GenOverlay();
						new WidgetPlacer(m_zone, pointsOfInterest).placeWidgetPOI(Enode, grid);
					}
					if (Enode.getTagName() == "placeWidget") {
						new WidgetPlacer(m_zone).placeWidget(Enode, 0, 0);
					}
					if (Enode.getTagName() == "placeSpawner") {
						new WidgetPlacer(m_zone).placeSpawner(Enode);
					}
					if (Enode.getTagName().equals("nodeMapGen"))
					{
						grid = CloneOverlay(grid);
						NextPhase(Enode,new NodeMapGenerator(Enode).run(m_zone,grid).getGrid());
					}
					if (Enode.getTagName().equals("circles"))
					{
						grid = CloneOverlay(grid);
						NextPhase(Enode,new CircleTool(Enode).run(m_zone,grid).getGrid());
					}
					if (Enode.getTagName() == "preloadprefab") {
						grid = GenOverlay();
						preloadPrefab(Enode, grid);
					}
				}

			}

		}
	}

	void WallOff(Element node, boolean grid[][]) {
		// read sides and wall off appropriately
		int walldex = Integer.parseInt(node.getAttribute("wall"));
		String sides = node.getAttribute("sides");
		if (sides.contains("north")) {
			for (int i = 0; i < m_width; i++) {
				grid[i][m_height - 1] = false;
				m_tiles[i][m_height - 1] = new Tile(i, m_height - 1, m_library.getDef(walldex), m_zone, m_library);
			}
		}
		if (sides.contains("east")) {
			for (int i = 0; i < m_height; i++) {
				grid[m_width - 1][i] = false;
				m_tiles[m_width - 1][i] = new Tile(m_width - 1, i, m_library.getDef(walldex), m_zone, m_library);
			}
		}
		if (sides.contains("south")) {
			for (int i = 0; i < m_width; i++) {
				grid[i][0] = false;
				m_tiles[i][0] = new Tile(i, 0, m_library.getDef(walldex), m_zone, m_library);
			}
		}
		if (sides.contains("west")) {
			for (int i = 0; i < m_height; i++) {
				grid[0][i] = false;
				m_tiles[0][i] = new Tile(0, i, m_library.getDef(walldex), m_zone, m_library);
			}
		}

		NextPhase(node, grid);
	}

	void Describer(Element descnode, int offsetx, int offsety) {
		int x = Integer.parseInt(descnode.getAttribute("x")) + offsetx;
		int y = Integer.parseInt(descnode.getAttribute("y")) + offsety;

		WidgetDescription widget = new WidgetDescription(descnode.getTextContent());
		m_tiles[x][y].setWidget(widget);

	}

	WidgetPortal Portal(Element portalnode, int offsetx, int offsety) {
		int x = Integer.parseInt(portalnode.getAttribute("x")) + offsetx;
		int y = Integer.parseInt(portalnode.getAttribute("y")) + offsety;

		String string = null;
		Vec2f destination = null;
		if (portalnode.getAttribute("destination").length() > 0) {
			string = portalnode.getAttribute("destination");
		}
		if (portalnode.getAttribute("destinationx").length() > 0) {
			destination = new Vec2f(Integer.parseInt(portalnode.getAttribute("destinationx")),
					Integer.parseInt(portalnode.getAttribute("destinationy")));
		}
		WidgetPortal portal = new WidgetPortal(Integer.parseInt(portalnode.getAttribute("sprite")), string, destination,
				portalnode.getTextContent().replace("\n", ""));
		m_tiles[x][y].setWidget(portal);
		return portal;
	}

	WidgetPortal PairedPortal(Element portalnode, int offsetx, int offsety) {
		int x = Integer.parseInt(portalnode.getAttribute("x")) + offsetx;
		int y = Integer.parseInt(portalnode.getAttribute("y")) + offsety;

		String string = null;
		int id = 0;
		if (portalnode.getAttribute("destination").length() > 0) {
			string = portalnode.getAttribute("destination");
		}
		if (portalnode.getAttribute("ID") != null) {
			id = Integer.parseInt(portalnode.getAttribute("ID"));
		}
		WidgetPortal portal = new WidgetPortal(Integer.parseInt(portalnode.getAttribute("sprite")),
				portalnode.getTextContent().replace("\n", ""), id);
		portal.setDestination(string, id);
		if (portalnode.getAttribute("facing").length() > 0) {
			portal.setFacing(Integer.parseInt(portalnode.getAttribute("facing")));
		}
		m_tiles[x][y].setWidget(portal);
		return portal;
	}

	public void selectTile(Element node, boolean[][] grid) {
		int tile=Integer.parseInt(node.getAttribute("tile"));
		boolean nugrid[][] = new boolean[m_width][];
		for (int i = 0; i < m_width; i++) {
			nugrid[i] = new boolean[m_height];
			for (int j = 0; j < m_height; j++) {
				if (grid[i][j] && m_tiles[i][j]!=null && m_tiles[i][j].getDefinition().getID()==tile)
				{
					nugrid[i][j]=true;
				}
			}
		}
		NextPhase(node, nugrid);
	}

	public void Partition(Element node, boolean[][] grid) {
		boolean nugrid[][] = new boolean[m_width][];

		boolean invert = false;
		if (node.getAttribute("inverse").length() > 0) {
			if (node.getAttribute("inverse").equals("true")) {
				invert = true;
			}
		}
		int xmin = Integer.parseInt(node.getAttribute("xmin"));
		int ymin = Integer.parseInt(node.getAttribute("ymin"));
		int xmax = Integer.parseInt(node.getAttribute("xmax"));
		int ymax = Integer.parseInt(node.getAttribute("ymax"));
		for (int i = 0; i < m_width; i++) {
			nugrid[i] = new boolean[m_height];
			for (int j = 0; j < m_height; j++) {
				if (invert) {
					if (grid[i][j] == true) {
						nugrid[i][j] = true;
					}
					if ((i > xmin && i < xmax) && (j > ymin && j < ymax)) {
						if (grid[i][j] == true) {
							nugrid[i][j] = false;
						}

					}
				} else {

					if ((i > xmin && i < xmax) && (j > ymin && j < ymax)) {
						if (grid[i][j] == true) {
							nugrid[i][j] = true;
						}

					}
				}

			}
		}

		NextPhase(node, nugrid);
	}

	public Tile[][] BuildZone(Element MapNode) {

		NodeList children = MapNode.getChildNodes();
		boolean grid[][];
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) node;
				// run each step successively
				if (Enode.getTagName() == "floodfill") {
					grid = GenOverlay();
					Floodfill(Enode, grid);
				}
				if (Enode.getTagName() == "auditpaths") {
					AuditTool tool = new AuditTool(m_zone, pointsOfInterest);
					int replace = 0;
					if (Enode.getAttribute("replace").length() > 0) {
						replace = Integer.parseInt(Enode.getAttribute("replace"));
					}
					int widgetselection = 0;
					if (Enode.getAttribute("exclude").equals("impassable")) {
						widgetselection = 1;
					}
					tool.runPathCarver(Integer.parseInt(Enode.getAttribute("carve")), true, true, true, replace,
							widgetselection);
				}
				if (Enode.getTagName() == "selectTile") {
					grid = GenOverlay();
					selectTile(Enode, grid);
				}
				if (Enode.getTagName() == "conditionalportal") {
					new WidgetPlacer(m_zone).placeConditionalPortal(Enode, 0, 0);
				}
				if (Enode.getTagName() == "auditpathsthroughvoid") {
					AuditTool tool = new AuditTool(m_zone, pointsOfInterest);
					int replace = 0;
					boolean random=false;
					if (Enode.getAttribute("replace").length() > 0) {
						replace = Integer.parseInt(Enode.getAttribute("replace"));
					}
					int widgetselection = 0;
					if (Enode.getAttribute("exclude").equals("impassable")) {
						widgetselection = 1;
					}
					if (Enode.getAttribute("random").equals("true")) {
						random=true;
					}
					tool.runMakePath(Integer.parseInt(Enode.getAttribute("carve")), true, true, true, replace,
							widgetselection,random);
				}
				if (Enode.getTagName() == "blockDungeon") {
					grid = GenOverlay();
					new BlockDungeonGenerator(m_zone, m_tiles).run(Enode, grid);
				}
				if (Enode.getTagName() == "advancedBlockDungeon") {
					grid = GenOverlay();
					new AdvancedBlockDungeonGenerator(m_zone, m_tiles).run(Enode, grid);
				}
				if (Enode.getTagName() == "noise") {
					grid = GenOverlay();
					Noise(Enode, grid);
				}
				if (Enode.getTagName() == "clumps") {
					grid = GenOverlay();
					Clumps(Enode, grid);
				}
				if (Enode.getTagName() == "perlin") {
					grid = GenOverlay();
					PerlinFramework.useFramework(grid, Enode, this);
				}
				if (Enode.getTagName() == "prefab") {
					grid = GenOverlay();
					Prefab(Enode, grid, 0, 0, true);
				}
				if (Enode.getTagName() == "randprefab") {
					grid = GenOverlay();
					RandPrefab(Enode, grid);
				}
				if (Enode.getTagName() == "preloadprefab") {
					grid = GenOverlay();
					preloadPrefab(Enode, grid);
				}
				if (Enode.getTagName() == "SeedNPCs") {
					grid = GenOverlay();
					new NPCPlacer(m_zone, pointsOfInterest).SeedNPCs(Enode, grid);
				}
				if (Enode.getTagName() == "NPC") {
					grid = GenOverlay();
					new NPCPlacer(m_zone, pointsOfInterest).placeNPCPOI(Enode, grid);
				}
				if (Enode.getTagName() == "POINPC") {
					grid = GenOverlay();
					new NPCPlacer(m_zone, pointsOfInterest).placeNPCPOI(Enode, grid);
				}
				if (Enode.getTagName() == "POIITEM") {
					grid = GenOverlay();
					new WidgetPlacer(m_zone, pointsOfInterest).placeItemPOI(Enode, pointsOfInterest);
				}
				if (Enode.getTagName() == "POIprefab") {
					grid = GenOverlay();
					POIPrefab(Enode, grid);
				}
				if (Enode.getTagName() == "Seedwidgets") {
					grid = GenOverlay();
					new WidgetPlacer(m_zone).SeedWidgets(Enode, grid);
				}
				if (Enode.getTagName() == "portal") {
					Portal(Enode, 0, 0);
				}
				if (Enode.getTagName() == "placeNPC") {
					new NPCPlacer(m_zone, pointsOfInterest).PlaceNPC(Enode, GenOverlay(), 0, 0);
				}
				if (Enode.getTagName() == "pairedportal") {
					PairedPortal(Enode, 0, 0);
				}
				if (Enode.getTagName() == "describer") {
					Describer(Enode, 0, 0);
				}
				if (Enode.getTagName() == "partition") {
					grid = GenOverlay();
					Partition(Enode, grid);
				}
				if (Enode.getTagName() == "breakable") {
					new WidgetPlacer(m_zone).Breakable(Enode, 0, 0);
				}
				if (Enode.getTagName() == "walloff") {
					grid = GenOverlay();
					WallOff(Enode, grid);
				}
				if (Enode.getTagName() == "cavegen") {
					grid = GenOverlay();
					CaveGen(Enode, grid);
				}
				if (Enode.getTagName() == "town") {
					grid = GenOverlay();
					TownGen(Enode, grid);
				}
				if (Enode.getTagName() == "towngrid") {
					grid = GenOverlay();
					gridTown(Enode, grid);
				}
				if (Enode.getTagName() == "empty") {
					grid = Empty();
					NextPhase(Enode, grid);
				}
				if (Enode.getTagName() == "degrade") {
					grid = GenOverlay();
					Degrade(Enode, grid);
				}
				if (Enode.getTagName() == "placeWidget") {
					new WidgetPlacer(m_zone).placeWidget(Enode, 0, 0);
				}
				if (Enode.getTagName() == "placeItem") {
					new WidgetPlacer(m_zone).placeItem(Enode, 0, 0);
				}
				if (Enode.getTagName() == "POIWidget") {
					grid = GenOverlay();
					new WidgetPlacer(m_zone, pointsOfInterest).placeWidgetPOI(Enode, grid);
				}
				if (Enode.getTagName().equals("POICornerDecorate")) {
					grid = GenOverlay();
					NodeList list = Enode.getChildNodes();

					Element child = null;
					for (int k = 0; k < list.getLength(); k++) {
						if (list.item(k).getNodeType() == Node.ELEMENT_NODE) {
							child = (Element) Enode.getChildNodes().item(k);
							break;
						}
					}
					new DecoratorTool(m_tiles, m_library, m_zone).runCorners(pointsOfInterest.getCurrentPOI(), child,
							Integer.parseInt(Enode.getAttribute("floor")));
				}

				if (Enode.getTagName() == "placeItem") {
					new WidgetPlacer(m_zone).placeItem(Enode, 0, 0);
				}
				if (Enode.getTagName() == "placeSpawner") {
					new WidgetPlacer(m_zone).placeSpawner(Enode);
				}
				if (Enode.getTagName().equals("recordEdges")) {
					new PreloadTools(m_zone, pointsOfInterest).recordEdges(Enode);
				}
				if (Enode.getTagName().equals("carveEdges")) {
					new PreloadUtilizer(m_zone, pointsOfInterest).carveEdges(Enode);
				}
				if (Enode.getTagName().equals("overlayTrees")) {
					new OverlayTool(m_zone).runTrees(Enode);
				}
				if (Enode.getTagName().equals("nodeMapGen"))
				{
					grid = GenOverlay();
					NextPhase(Enode,new NodeMapGenerator(Enode).run(m_zone,grid).getGrid());
				}
				if (Enode.getTagName().equals("circles"))
				{
					grid = GenOverlay();
					NextPhase(Enode,new CircleTool(Enode).run(m_zone,grid).getGrid());
				}
			}

		}

		return m_tiles;
	}

	boolean[][] Empty() {
		boolean[][] grid = GenOverlay();
		for (int i = 0; i < m_width; i++) {
			for (int j = 0; j < m_height; j++) {
				if (m_tiles[i][j] == null) {
					grid[i][j] = true;
				} else {
					grid[i][j] = false;
				}

			}
		}
		return grid;
	}

	void TownGen(Element node, boolean[][] grid) {
		TownGenerator generator = new TownGenerator(m_tiles, grid, m_library, m_zone, pointsOfInterest);

		generator.Run(node);
		m_tiles = generator.getTiles();
		NextPhase(node, generator.getGrid());

	}

	void gridTown(Element node, boolean[][] grid) {
		GridTownGenerator generator = new GridTownGenerator(m_tiles, grid, m_library, m_zone, pointsOfInterest);

		generator.run(node);
		m_tiles = generator.getTiles();
		NextPhase(node, generator.getGrid());
	}

	void CaveGen(Element node, boolean[][] grid) {
		CaveGenerator generator = new CaveGenerator(m_tiles, grid, m_library, m_zone);

		int iterations = Integer.parseInt(node.getAttribute("iterations"));
		int tile = Integer.parseInt(node.getAttribute("tile"));
		int threshold = Integer.parseInt(node.getAttribute("threshold"));
		int bounds = Integer.parseInt(node.getAttribute("bounds"));
		float check = Float.parseFloat(node.getAttribute("minfloor"));
		generator.Run(bounds, threshold, iterations, tile, check);
		m_tiles = generator.getTiles();
		NextPhase(node, generator.getGrid());

	}

	void Degrade(Element node, boolean[][] grid) {
		DegraderTool tool = new DegraderTool(m_tiles, grid, m_library, m_zone);
		tool.Run(Integer.parseInt(node.getAttribute("chance")), Integer.parseInt(node.getAttribute("degradethis")),
				Integer.parseInt(node.getAttribute("replacewith")));

		m_tiles = tool.getTiles();
	}

}
