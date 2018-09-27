package vmo;

import widgets.Widget;
import zone.TileDef;
import zone.TileDefLibrary;

public interface Tile_Int {

	public int getImage();

	public int getWidget();

	public boolean getVisible();

	public boolean getExplored();

	public Widget getWidgetObject();

	public int getX();

	public int getY();

	public TileDef getDefinition();

	public TileDefLibrary getTileset();

	public int getOverlayImage();
}
