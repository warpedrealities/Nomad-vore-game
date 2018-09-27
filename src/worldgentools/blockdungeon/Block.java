package worldgentools.blockdungeon;

import java.util.ArrayList;

import actor.npc.NPC;
import worldgentools.blockdungeon.advanced.AdvancedOpening;
import zone.Tile;
import zone.Zone;

public interface Block {

	boolean canPlace(int x, int y, int[][] grid);

	short getEdgeValue();

	void mark(int x, int y, int[][] grid, ArrayList<Opening> openings);

	void apply(int x, int y, Tile[][] grid, Zone zone, DungeonWidgetLoader loader, ArrayList<NPC> templates);

	int getKeyHeat();

}
