package zone;

public class SmartTileBehavior implements ITileBehavior {

	@Override
	public int getSprite(Tile tile) {
		return getSmartSprite(tile);
	}

	private int getSmartSprite(Tile tile) {
		Zone parent = tile.getZone();
		int x = tile.getX();
		int y = tile.getY();
		boolean occupied[] = new boolean[] { isSameTile(tile, parent.getTile(x, y + 1)),
				isSameTile(tile, parent.getTile(x + 1, y)), isSameTile(tile, parent.getTile(x, y - 1)),
				isSameTile(tile, parent.getTile(x - 1, y)) };

		return tile.getDefinition().getSprite() + getNeighborMask(occupied);
	}

	private boolean isSameTile(Tile a, Tile b) {
		return a != null && b != null && a.getDefinition().getID() == b.getDefinition().getID();
	}

	// expected order is TOP RIGHT DOWN LEFT
	private int getNeighborMask(boolean[] occupied) {
		int bitmasked = 0;
		int mask = 1;
		for (int i = 0; i < occupied.length; i++, mask <<= 1) {
			if (occupied[i]) {
				bitmasked = bitmasked | mask;
			}
		}

		return bitmasked;
	}

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return 1;
	}
}
