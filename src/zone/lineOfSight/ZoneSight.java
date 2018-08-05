package zone.lineOfSight;

import rlforj.los.ILosBoard;
import zone.Tile;
import zone.TileDef.TileVision;

public class ZoneSight implements ILosBoard {

	private boolean pierceTransparent;
	private int width,height;
	private Tile [][] tiles;
	
	public ZoneSight(Tile [][] tiles, int width, int height, boolean pierceTransparent)
	{
		this.tiles=tiles;
		this.width=width;
		this.height=height;
		this.pierceTransparent=pierceTransparent;
	}
	
	
	@Override
	public boolean contains(int x, int y) {
		if (x < 0 || y < 0) {
			return false;
		}
		if (x >= width || y >= height) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isObstacle(int x, int y) {
		if (x >= 0 && x < width) {
			if (y >= 0 && y < height) {
				if (tiles[x][y] != null) {
					if (tiles[x][y].getWidgetObject() != null) {
						if (tiles[x][y].getWidgetObject().BlockVision() == true) {
							return true;
						}
					}
					if (!pierceTransparent)
					{
						return !tiles[x][y].getDefinition().getVision().equals(TileVision.EMPTY);
					}
					else
					{
						return tiles[x][y].getDefinition().getVision().equals(TileVision.BLOCKING);	
					}
	
				}
			}
		}
		return false;
	}

	@Override
	public void visit(int x, int y) {
		if (x >= 0 && x < width) {
			if (y >= 0 && y < height) {
				if (tiles[x][y] != null) {
					tiles[x][y].Explore();
					tiles[x][y].Reveal();
				}

			}
		}
	}

}
