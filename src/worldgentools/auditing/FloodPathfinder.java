package worldgentools.auditing;

import java.util.ArrayList;

import nomad.universe.Universe;
import shared.Vec2i;
import view.ZoneInteractionHandler;

import zone.Tile;
import zone.TileDef.TileMovement;
import zone.Zone;

public class FloodPathfinder {

	Zone zone;
	ArrayList<Vec2i> openList;
	Boolean[][] grid;
	int tilesReached;

	public FloodPathfinder(Zone zone) {
		tilesReached=0;
		this.zone = zone;
		openList = new ArrayList<Vec2i>();

		grid = new Boolean[zone.getWidth()][];
		for (int i = 0; i < zone.getWidth(); i++) {
			grid[i] = new Boolean[zone.getHeight()];
			for (int j = 0; j < zone.getHeight(); j++) {
				grid[i][j] = false;
			}
		}
	}

	private void updateOpenList(Vec2i pos) {
		grid[pos.x][pos.y] = true;
		openList.remove(pos);

		int x = pos.x;
		int y = pos.y;

		for (int i = 0; i < 8; i++) {
			Vec2i p = ZoneInteractionHandler.getPos(i, pos);
			if (p.x >= 0 && p.x < zone.getWidth() && p.y >= 0 && p.y < zone.getHeight()) {

				if (grid[p.x][p.y] == false) {
					if (zone.getTile(p.x, p.y) != null) {
						Tile t = zone.getTile(p.x, p.y);
						if (t.getDefinition().getMovement() == TileMovement.WALK && !extant(p)) {
							openList.add(p);
							tilesReached++;
						}
					}
				}
			}
		}
	}

	private boolean extant(Vec2i p) {
		for (int i = 0; i < openList.size(); i++) {
			if (openList.get(i).x == p.x && openList.get(i).y == p.y) {
				return true;
			}
		}
		return false;
	}

	public ArrayList<Vec2i> runFlood(ArrayList<Vec2i> points) {
		updateOpenList(points.get(0));
		
		while (openList.size() > 0) {
			updateOpenList(openList.get(0));
		}

		ArrayList<Vec2i> unreachable = new ArrayList<Vec2i>();
		for (int i = 0; i < points.size(); i++) {
			if (grid[points.get(i).x][points.get(i).y] == false) {
				unreachable.add(points.get(i));
			}
		}

		return unreachable;
	}

}
