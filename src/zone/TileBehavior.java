package zone;

public class TileBehavior implements ITileBehavior {

	@Override
	public int getSprite(Tile tile) {
		return tile.getDefinition().getSprite();
	}

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return 0;
	}

}
