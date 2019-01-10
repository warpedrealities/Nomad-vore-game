package worldgentools.blockdungeon.advanced;

import java.util.List;

import zone.TileDef;
import zone.TileDef.TileMovement;
import zone.Zone;

public class SanityChecker {

	public void checkSanity(List<AdvancedBlock> blocks, Zone zone) {
		for (int i = 0; i < blocks.size(); i++) {
			check(blocks.get(i), zone);
		}
	}

	private void check(AdvancedBlock block, Zone zone) {
		if ((block.getEdgeValue() & 1) > 0) {
			checkNorth(block, zone);
		}
		if ((block.getEdgeValue() & 2) > 0) {
			checkEast(block, zone);
		}
		if ((block.getEdgeValue() & 4) > 0) {
			checkSouth(block, zone);
		}
		if ((block.getEdgeValue() & 8) > 0) {
			checkWest(block, zone);
		}
	}

	private void checkWest(AdvancedBlock block, Zone zone) {
		for (int i = 0; i < 8; i++) {
			if (tilePassable(block.getTile(0, i), zone)) {
				return;
			}
		}
		System.out.println("north edge of block broken" + block.getName());
	}

	private void checkSouth(AdvancedBlock block, Zone zone) {
		for (int i = 0; i < 8; i++) {
			if (tilePassable(block.getTile(i, 0), zone)) {
				return;
			}
		}
		System.out.println("south edge of block broken" + block.getName());

	}

	private void checkEast(AdvancedBlock block, Zone zone) {
		for (int i = 0; i < 8; i++) {
			if (tilePassable(block.getTile(7, i), zone)) {
				return;
			}
		}
		System.out.println("north edge of block broken" + block.getName());
	}

	private void checkNorth(AdvancedBlock block, Zone zone) {
		for (int i = 0; i < 8; i++) {
			if (tilePassable(block.getTile(i, 7), zone)) {
				return;
			}
		}
		System.out.println("north edge of block broken" + block.getName());

	}

	private boolean tilePassable(int tile, Zone zone) {
		if (tile == 0) {
			return false;
		}
		TileDef tileDef = zone.getZoneTileLibrary().getDef(tile - 1);
		if (tileDef != null && tileDef.getMovement() == TileMovement.WALK) {
			return true;
		}
		return false;
	}

}
