package zone;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;

import shared.ParserHelper;

public class TileDef {
	int m_sprite;
	TileVision vision;
	boolean m_autoTile;
	String m_description;

	public enum TileMovement {
		WALK, FLY, BLOCK,SLOW
	};
	
	public enum TileVision {
		EMPTY, TRANSPARENT, BLOCKING;
	};

	int indexID;
	TileMovement m_movement;
	ITileBehavior m_behavior;

	public TileDef(Element node, int index) {
		indexID = index;
		m_sprite = Integer.parseInt(node.getAttribute("sprite"));
		m_autoTile = false;
		if (node.getAttribute("vision") != null) {
			vision=TileVision.valueOf(node.getAttribute("vision"));
		}
		m_description = node.getTextContent().replace("\n", "");

		if (node.getAttribute("movement") != null) {
			int value = Integer.parseInt(node.getAttribute("movement"));
			switch (value) {
			case 0:
				m_movement = TileMovement.WALK;
				break;

			case 1:
				m_movement = TileMovement.FLY;
				break;

			case 2:
				m_movement = TileMovement.BLOCK;
				break;
			case 3:
				m_movement = TileMovement.SLOW;
				break;	
			}
		}

		if (!node.getAttribute("smart").isEmpty()) {
			boolean val = Boolean.parseBoolean(node.getAttribute("smart"));
			m_autoTile = val;
		}

		if (isAutoTile()) {
			m_behavior = new SmartTileBehavior();
		} else {
			m_behavior = new TileBehavior();
		}
	}

	public TileMovement getMovement() {
		return m_movement;
	}

	public int getSprite() {
		return m_sprite;
	}

//	public boolean getBlockVision() {
//		return m_blockvision;
//	}
	
	public TileVision getVision() {
		return vision;
	}

	public String getDescription() {
		return m_description;
	}

	public int getID() {
		return indexID;
	}

	public boolean isAutoTile() {
		return m_autoTile;
	}

	public void save(DataOutputStream dstream) throws IOException {
		dstream.writeInt(m_sprite);
		dstream.writeInt(vision.ordinal());
		ParserHelper.SaveString(dstream, m_description);
		ParserHelper.SaveString(dstream, m_movement.toString());

		dstream.writeInt(m_behavior.getType());
	}

	public TileDef(DataInputStream dstream, int index) throws IOException {
		m_sprite = dstream.readInt();
		vision=TileVision.values()[dstream.readInt()];
		m_description = ParserHelper.LoadString(dstream);
		String s = ParserHelper.LoadString(dstream);
		m_movement = TileMovement.valueOf(s);
		indexID = index;

		int v = dstream.readInt();

		if (v == 1) {
			m_behavior = new SmartTileBehavior();
		} else if (v == 0) {
			m_behavior = new TileBehavior();
		}

	}

	public ITileBehavior getBehavior() {
		return m_behavior;
	}

	public boolean getBlockVision() {
		return !vision.equals(TileVision.EMPTY);
	}

}
