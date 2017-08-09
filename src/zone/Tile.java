package zone;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import actor.Actor;
import vmo.Tile_Int;
import widgets.Widget;
import widgets.WidgetLoader;

public class Tile implements Tile_Int {

	TileDefLibrary m_tileset;
	TileDef m_definition;
	boolean m_visible, m_explored;
	Widget m_widget;
	Zone m_zone;
	int m_x;
	int m_y;
	int tileImage = -1;
	int overlayImage = -1;
	Actor actorInTile;

	public Tile(int x, int y, TileDef definition, Zone zone, TileDefLibrary tileset) {
		m_visible = false;
		m_explored = false;
		m_definition = definition;
		m_tileset = tileset;
		m_widget = null;
		m_zone = zone;
		m_x = x;
		m_y = y;
	}

	public int getOverlayImage() {
		return overlayImage;
	}

	public void setOverlayImage(int overlayImage) {
		this.overlayImage = overlayImage;
	}

	@Override
	public int getImage() {
		if (tileImage == -1) {
			tileImage = m_definition.getBehavior().getSprite(this);
		}
		return tileImage;
	}

	public Zone getZone() {
		return m_zone;
	}

	@Override
	public int getWidget() {
		if (m_widget != null) {
			return m_widget.getSprite();
		}
		return 0;
	}

	public void setWidget(Widget widget) {
		m_widget = widget;
	}

	@Override
	public boolean getVisible() {
		// TODO Auto-generated method stub
		return m_visible;
	}

	public void Step() {
		if (m_widget != null) {
			m_widget.Step();
		}
	}

	@Override
	public boolean getExplored() {
		// TODO Auto-generated method stub
		return m_explored;
	}

	public void Explore() {
		m_explored = true;
		if (m_widget != null) {
			m_widget.Visit();
		}
	}

	public void Hide() {
		m_visible = false;
	}

	public void Reveal() {
		m_visible = true;

	}

	public TileDef getDefinition() {
		return m_definition;
	}

	public TileDefLibrary getTileset() {
		return m_tileset;
	}

	public Widget getWidgetObject() {
		return m_widget;
	}

	public void Save(DataOutputStream dstream) throws IOException {
		dstream.writeInt(m_definition.indexID);
		dstream.writeBoolean(m_visible);
		dstream.writeBoolean(m_explored);
		// save widget
		if (m_widget != null) {
			dstream.writeBoolean(true);
			m_widget.save(dstream);
		} else {
			dstream.writeBoolean(false);
		}
		dstream.writeInt(m_x);
		dstream.writeInt(m_y);
		dstream.writeInt(overlayImage);
	}

	public void Load(DataInputStream dstream, TileDefLibrary library) throws IOException {
		// TODO Auto-generated constructor stub
		m_definition = library.getDef(dstream.readInt());
		m_visible = dstream.readBoolean();
		m_explored = dstream.readBoolean();
		m_tileset = library;
		boolean b = dstream.readBoolean();
		if (b == true) {
			m_widget = WidgetLoader.loadWidget(dstream);
		} else {
			m_widget = null;
		}
		m_x = dstream.readInt();
		m_y = dstream.readInt();
		overlayImage = dstream.readInt();
	}

	@Override
	public int getX() {
		// TODO Auto-generated method stub
		return m_x;
	}

	@Override
	public int getY() {
		// TODO Auto-generated method stub
		return m_y;
	}

	public Actor getActorInTile() {
		return actorInTile;
	}

	public void setActorInTile(Actor actorInTile) {
		this.actorInTile = actorInTile;
	}

}
