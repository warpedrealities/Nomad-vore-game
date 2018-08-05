package worldgentools.blockdungeon.advanced;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actor.npc.NPC;
import shared.Vec2f;
import worldgentools.blockdungeon.DungeonWidgetLoader;
import worldgentools.blockdungeon.Opening;
import worldgentools.blockdungeon.SpawnDefinition;
import worldgentools.blockdungeon.WidgetDefinition;
import zone.Tile;
import zone.Zone;

public class AdvancedStandardBlock implements AdvancedBlock {

	String name;
	short edgeValue;
	short [] sideValues;
	int[][] tiles;
	ArrayList<WidgetDefinition> widgets;
	ArrayList<SpawnDefinition> spawns;
	
	public AdvancedStandardBlock(Element enode) {
		name = enode.getAttribute("name");
		sideValues=new short[4];
		widgets = new ArrayList<WidgetDefinition>();
		spawns = new ArrayList<SpawnDefinition>();
		tiles = new int[8][];
		for (int i = 0; i < 8; i++) {
			tiles[i] = new int[8];
		}
		NodeList children = enode.getChildNodes();

		int index = 0;
		for (int i = 0; i < children.getLength(); i++) {
			Node Nnode = children.item(i);
			if (Nnode.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) Nnode;
				// run each step successively
				if (Enode.getTagName() == "row") {
					String row = Enode.getTextContent();
					for (int j = 0; j < 8; j++) {

						int value = Integer.parseInt(row.substring(j, j + 1));
						if (value > 0) {
							tiles[j][index] = value;
						}
					}
					index++;
				}
				if (Enode.getTagName() == "edge") {
					if (Enode.getAttribute("value").equals("NORTH")) {
						edgeValue += 1;
						sideValues[0]=Short.parseShort(Enode.getAttribute("type"));
					}
					if (Enode.getAttribute("value").equals("EAST")) {
						edgeValue += 2;
						sideValues[1]=Short.parseShort(Enode.getAttribute("type"));
					}
					if (Enode.getAttribute("value").equals("SOUTH")) {
						edgeValue += 4;
						sideValues[2]=Short.parseShort(Enode.getAttribute("type"));
					}
					if (Enode.getAttribute("value").equals("WEST")) {
						edgeValue += 8;
						sideValues[3]=Short.parseShort(Enode.getAttribute("type"));
					}
				}
				if (Enode.getTagName() == "widget") {
					widgets.add(new WidgetDefinition(Enode));
				}
				if (Enode.getTagName() == "spawn") {
					spawns.add(new SpawnDefinition(Enode));
				}
			}
		}

	}

	@Override
	public boolean canPlace(int x, int y, int[][] grid) {
		return grid[x][y] == 0;
	}

	@Override
	public short getEdgeValue() {
		return edgeValue;
	}

	@Override
	public void apply(int x, int y, Tile[][] grid, Zone zone, DungeonWidgetLoader loader, ArrayList<NPC> templates) {
		int offsetx = x * 8;
		int offsety = y * 8;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (tiles[i][j] >= 1) {
					grid[i + offsetx][j + offsety] = new Tile(i + offsetx, j + offsety,
							zone.getZoneTileLibrary().getDef(tiles[i][j] - 1), zone, zone.getZoneTileLibrary());
				}

			}
		}
		for (int i = 0; i < widgets.size(); i++) {
			grid[offsetx + widgets.get(i).getPosition().x][offsety + widgets.get(i).getPosition().y]
					.setWidget(loader.loadWidget(widgets.get(i)));
		}

		for (int i = 0; i < spawns.size(); i++) {
			NPC npc = new NPC(templates.get(spawns.get(i).getTag()),
					new Vec2f(offsetx + spawns.get(i).getPosition().x, offsety + spawns.get(i).getPosition().y));
			zone.getActors().add(npc);
			npc.setCollisioninterface(zone);
		}
	}

	@Override
	public int getKeyHeat() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public short getValueSide(int side) {
		return sideValues[side];
	}

	@Override
	public boolean matchEdges(short[] edgeTypes) {
		for (int i=0;i<4;i++)
		{
			if (sideValues[i]!=edgeTypes[i] && edgeTypes[i]!=0)
			{
				return false;
			}
		}
		return true;
	}

}
