package worldgentools.blockdungeon;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actor.npc.NPC;
import nomad.universe.Universe;
import shared.ParserHelper;
import shared.Vec2f;
import shared.Vec2i;

import zone.Tile;
import zone.Zone;

public class BlockDungeonGenerator {

	DungeonWidgetLoader widgetLoader;
	ArrayList<String> npcList;
	Zone zone;
	ArrayList<Block> blockList;
	ArrayList<Block> keyBlocks;

	int keyIndex;
	int lockedKeys;
	int extent;
	ArrayList<Opening> openings;

	int[][] blockGrid;
	ArrayList<Vec2i> keyLocations;
	Tile[][] zoneGrid;

	public BlockDungeonGenerator(Zone zone, Tile[][] grid) {
		widgetLoader = new DungeonWidgetLoader();
		zoneGrid = grid;
		this.zone = zone;
		blockList = new ArrayList<Block>();
		keyBlocks = new ArrayList<Block>();

		openings = new ArrayList<Opening>();
		keyLocations = new ArrayList<Vec2i>();
		npcList = new ArrayList<String>();
		blockGrid = new int[zone.getWidth() / 8][];

		for (int i = 0; i < zone.getWidth() / 8; i++) {
			blockGrid[i] = new int[zone.getHeight() / 8];

		}
	}

	public void run(Element element) {

		NodeList children = element.getChildNodes();
		extent = Integer.parseInt(element.getAttribute("extent"));

		int clean = 0;
		for (int i = 0; i < children.getLength(); i++) {
			Node Nnode = children.item(i);
			if (Nnode.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) Nnode;
				// run each step successively
				if (Enode.getTagName() == "loadBlocks") {
					blockList.addAll(BlockLoader.load(Enode.getAttribute("file")));
				}

				if (Enode.getTagName() == "lootTable") {
					widgetLoader.loadLootTable(Enode);
				}
				if (Enode.getTagName() == "npcType") {
					npcList.add(Enode.getAttribute("value"));
				}
				if (Enode.getTagName() == "clean") {
					clean = Integer.parseInt(Enode.getAttribute("value"));
				}
				if (Enode.getTagName() == "prefabBlock") {
					keyBlocks.add(new Prefab_Block(Enode));
				}
				if (Enode.getTagName() == "keyBlock") {
					keyBlocks.add(new KeyBlock(Enode));

					if (Enode.getAttribute("x").length() > 0) {
						int x = Integer.parseInt(Enode.getAttribute("x"));
						int y = Integer.parseInt(Enode.getAttribute("y"));
						blockGrid[x][y] = keyBlocks.size() * -1;
						calculateOpenings(x, y, blockGrid[x][y]);
						keyLocations.add(new Vec2i(x, y));
						keyIndex++;
						lockedKeys++;
					}

				}
			}
		}

		while (true) {
			grow(extent);

			if (keyIndex == keyBlocks.size()) {
				break;
			} else {
				reset();
			}

		}
		apply();
		if (clean > 0) {
			BlockDungeonHelper.cleanup(zoneGrid, zone, clean - 1);
		}
	}

	private void reset() {
		openings.clear();
		for (int i = 0; i < blockGrid.length; i++) {
			for (int j = 0; j < blockGrid[i].length; j++) {
				if (blockGrid[i][j] > 0) {
					blockGrid[i][j] = 0;
				}
				if (blockGrid[i][j] < 0) {
					int c = (blockGrid[i][j] * -1) - 1;
					if (c >= lockedKeys) {
						blockGrid[i][j] = 0;
					}
				}
			}
		}
		keyIndex = lockedKeys;
		keyLocations.clear();
		for (int i = 0; i < keyIndex; i++) {
			Vec2i position = null;
			for (int k = 0; k < blockGrid.length; k++) {
				for (int j = 0; j < blockGrid[0].length; j++) {
					if (blockGrid[k][j] < 0) {
						int c = (blockGrid[k][j] * -1) - 1;
						if (c < lockedKeys) {
							position = new Vec2i(k, j);
							calculateOpenings(k, j, blockGrid[k][j]);
						} else {
							blockGrid[k][j] = 0;
						}
					}

				}
			}
			keyLocations.add(position);
		}

		for (int k = 0; k < blockGrid.length; k++) {
			for (int j = 0; j < blockGrid[0].length; j++) {
				if (blockGrid[k][j] > 0) {
					blockGrid[k][j] = 0;
				}
			}
		}

	}

	private void grow(int blocksLeft) {
		// pick opening
		boolean b = false;
		int r = 0;
		if (openings.size() > 1) {
			r = Universe.m_random.nextInt(openings.size() - 1);
		}

		// pick if we need an absolute fit or not

		if (keyIndex < keyBlocks.size()) {
			float heat = calculateHeatMap(openings.get(r).position.x, openings.get(r).position.y);
			if (heat > keyBlocks.get(keyIndex).getKeyHeat() && Universe.m_random.nextBoolean()) {
				if ((openings.get(r).edgeValue & keyBlocks.get(keyIndex).getEdgeValue()) != 0 && keyBlocks.get(keyIndex)
						.canPlace(openings.get(r).position.x, openings.get(r).position.y, blockGrid)) {
					if (blockGrid[openings.get(r).position.x][openings.get(r).position.y] == 0) {
						int v = (keyIndex * -1) - 1;
						keyBlocks.get(keyIndex).mark(openings.get(r).position.x, openings.get(r).position.y, blockGrid,
								openings);
						blockGrid[openings.get(r).position.x][openings.get(r).position.y] = v;

						keyLocations.add(new Vec2i(openings.get(r).position.x, openings.get(r).position.y));
						openings.remove(r);
					}
					b = true;
					keyIndex++;
				}
			}
		}

		if (b == false) {
			// find blocks
			if (blockGrid[openings.get(r).position.x][openings.get(r).position.y] == 0) {
				int block = BlockDungeonHelper.getBlock(blockList, openings.get(r).edgeValue, false);
				blockGrid[openings.get(r).position.x][openings.get(r).position.y] = block + 1;
				Opening opening = openings.get(r);
				calculateOpenings(opening.position.x, opening.position.y, block + 1);
			}
			openings.remove(r);

		}
		if (blocksLeft > 0 && openings.size() > 0) {
			grow(blocksLeft - 1);
		}
	}

	private float calculateHeatMap(int x, int y) {
		Vec2i p = new Vec2i(x, y);
		float d = 32;
		for (int i = 0; i < keyLocations.size(); i++) {
			float distance = keyLocations.get(i).getDistance(p);
			if (distance < d) {
				d = distance;
			}
		}
		return d;
	}

	private Block getBlock(int value) {
		if (value > 0) {
			return blockList.get(value - 1);
		}
		if (value < 0) {
			return keyBlocks.get((value * -1) - 1);
		}
		return null;
	}

	private boolean isInGrid(int x, int y) {
		if (x < 0 || x >= blockGrid.length) {
			return false;
		}
		if (y < 0 || y >= blockGrid[0].length) {
			return false;
		}
		return true;
	}

	public static void addOpening(int value, int x, int y, ArrayList<Opening> openings) {
		// check for duplicate opening
		Opening opening = null;
		for (int i = 0; i < openings.size(); i++) {
			if (openings.get(i).position.x == x && openings.get(i).position.y == y) {
				opening = openings.get(i);
				break;
			}
		}
		if (opening == null) {
			opening = new Opening(value, new Vec2i(x, y));
			openings.add(opening);
		} else {
			opening.edgeValue = opening.edgeValue | value;
		}
	}

	private void calculateOpenings(int x, int y, int value) {
		Block block = getBlock(value);

		// check north
		if ((block.getEdgeValue() & 1) > 0) {
			if (isInGrid(x, y + 1))
				if (blockGrid[x][y + 1] == 0) {
					addOpening(4, x, y + 1, openings);
				}
		}

		// check east
		if ((block.getEdgeValue() & 2) > 0) {
			if (isInGrid(x + 1, y))
				if (blockGrid[x + 1][y] == 0) {
					addOpening(8, x + 1, y, openings);
				}
		}

		// check south
		if ((block.getEdgeValue() & 4) > 0) {
			if (isInGrid(x, y - 1))
				if (blockGrid[x][y - 1] == 0) {
					addOpening(1, x, y - 1, openings);
				}
		}

		// check west
		if ((block.getEdgeValue() & 8) > 0) {
			if (isInGrid(x - 1, y))
				if (blockGrid[x - 1][y] == 0) {
					addOpening(2, x - 1, y, openings);
				}
		}

	}

	private void apply() {
		// generate npc list for spawns
		ArrayList<NPC> templates = new ArrayList<NPC>();
		for (int i = 0; i < npcList.size(); i++) {
			Document doc = ParserHelper.LoadXML("assets/data/npcs/" + npcList.get(i) + ".xml");
			Element root = doc.getDocumentElement();
			Element n = (Element) doc.getFirstChild();
			NPC template = new NPC(n, new Vec2f(0, 0), npcList.get(i));
			templates.add(template);
		}

		for (int i = 0; i < blockGrid.length; i++) {
			for (int j = 0; j < blockGrid[i].length; j++) {
				if (blockGrid[i][j] < 0 && blockGrid[i][j] > -100) {
					keyBlocks.get((blockGrid[i][j] * -1) - 1).apply(i, j, zoneGrid, zone, widgetLoader, templates);
				}
				if (blockGrid[i][j] > 0 && blockGrid[i][j] < 100) {
					blockList.get(blockGrid[i][j] - 1).apply(i, j, zoneGrid, zone, widgetLoader, templates);
				}
			}
		}
	}
}
