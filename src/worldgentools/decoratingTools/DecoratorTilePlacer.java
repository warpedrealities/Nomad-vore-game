package worldgentools.decoratingTools;

import org.w3c.dom.Element;

import zone.Tile;
import zone.Zone;

public class DecoratorTilePlacer implements DecoratorPlacer {

	int tileId;

	public DecoratorTilePlacer(Element enode) {
		tileId = Integer.parseInt(enode.getAttribute("tile"));
	}

	@Override
	public void place(int x, int y, Zone zone) {
		zone.getTiles()[x][y] = new Tile(x, y, zone.zoneTileLibrary.getDef(tileId), zone, zone.zoneTileLibrary);
	}

}
