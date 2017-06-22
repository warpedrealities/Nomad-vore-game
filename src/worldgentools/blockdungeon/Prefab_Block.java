package worldgentools.blockdungeon;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actor.npc.NPC;
import zone.Tile;
import zone.Zone;

public class Prefab_Block implements Block {

	StandardBlock block[];

	int width, height;
	int offsetX, offsetY;
	int heat;
	short edgeValue;

	public Prefab_Block(Element element) {
		block = new StandardBlock[Integer.parseInt(element.getAttribute("blockCount"))];
		width = Integer.parseInt(element.getAttribute("width"));
		height = Integer.parseInt(element.getAttribute("height"));
		heat = Integer.parseInt(element.getAttribute("heat"));
		NodeList nodeList = element.getChildNodes();

		int index = 0;
		for (int i = 0; i < nodeList.getLength(); i++) {
			if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) nodeList.item(i);
				if (e.getTagName().equals("block")) {
					block[index] = new StandardBlock(e);
					index++;
				}
				if (e.getTagName() == "edge") {
					if (e.getAttribute("value").equals("NORTH")) {
						edgeValue += 1;
					}
					if (e.getAttribute("value").equals("EAST")) {
						edgeValue += 2;
					}
					if (e.getAttribute("value").equals("SOUTH")) {
						edgeValue += 4;
					}
					if (e.getAttribute("value").equals("WEST")) {
						edgeValue += 8;
					}
				}
			}
		}

	}

	public void mark(int x, int y, int[][] grid, ArrayList<Opening> openings) {
		int index = 0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				grid[x + i + offsetX][y + j + offsetY] = 100;
				short edges = block[index].getEdgeValue();
				calcOpenings(x + i + offsetX, y + j + offsetY, edges, grid, openings);
				index++;
			}
		}
	}

	private boolean isInGrid(int x, int y, int[][] grid) {
		if (x < 0 || x >= grid.length) {
			return false;
		}
		if (y < 0 || y >= grid[0].length) {
			return false;
		}
		return true;
	}

	public void calcOpenings(int x, int y, short edges, int[][] grid, ArrayList<Opening> openings) {
		// check north
		if ((edges & 1) > 0) {
			if (isInGrid(x, y + 1, grid))
				if (grid[x][y + 1] == 0) {
					BlockDungeonGenerator.addOpening(4, x, y + 1, openings);
				}
		}

		// check east
		if ((edges & 2) > 0) {
			if (isInGrid(x + 1, y, grid))
				if (grid[x + 1][y] == 0) {
					BlockDungeonGenerator.addOpening(8, x + 1, y, openings);
				}
		}

		// check south
		if ((edges & 4) > 0) {
			if (isInGrid(x, y - 1, grid))
				if (grid[x][y - 1] == 0) {
					BlockDungeonGenerator.addOpening(1, x, y - 1, openings);
				}
		}

		// check west
		if ((edges & 8) > 0) {
			if (isInGrid(x - 1, y, grid))
				if (grid[x - 1][y] == 0) {
					BlockDungeonGenerator.addOpening(2, x - 1, y, openings);
				}
		}
	}

	@Override
	public boolean canPlace(int x, int y, int[][] grid) {

		// check each corner in turn
		if (BlockDungeonHelper.checkDR(grid, x, y, width, height)) {
			offsetX = 0;
			offsetY = 0;
			return true;
		}
		if (BlockDungeonHelper.checkDL(grid, x, y, width, height)) {
			offsetX = -(width - 1);
			offsetY = 0;
			return true;
		}
		if (BlockDungeonHelper.checkUR(grid, x, y, width, height)) {
			offsetX = 0;
			offsetY = -(height - 1);
			return true;
		}
		if (BlockDungeonHelper.checkUL(grid, x, y, width, height)) {
			offsetX = -(width - 1);
			offsetY = -(height - 1);
			return true;
		}
		return false;
	}

	@Override
	public short getEdgeValue() {
		return edgeValue;
	}

	@Override
	public void apply(int x, int y, Tile[][] grid, Zone zone, DungeonWidgetLoader loader, ArrayList<NPC> templates) {
		int index = 0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				block[index].apply(x + i + offsetX, y + j + offsetY, grid, zone, loader, templates);
				index++;
			}
		}
	}

	@Override
	public int getKeyHeat() {
		return heat;
	}

}
