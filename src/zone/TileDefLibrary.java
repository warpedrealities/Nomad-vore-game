package zone;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TileDefLibrary {
	public static final int DEFAULT_TILE_COUNT_X = 4;
	public static final int DEFAULT_TILE_COUNT_Y = 4;
	ArrayList<TileDef> m_tiledefinitions;

	int m_tileCountX = DEFAULT_TILE_COUNT_X;
	int m_tileCountY = DEFAULT_TILE_COUNT_Y;

	public TileDefLibrary(Element Defnode) {
		String countX = Defnode.getAttribute("count-x");
		if (!countX.isEmpty()) {
			m_tileCountX = Integer.parseInt(countX);
		}

		String countY = Defnode.getAttribute("count-y");
		if (!countY.isEmpty()) {
			m_tileCountY = Integer.parseInt(countY);
		}

		m_tiledefinitions = new ArrayList<TileDef>();
		// build tiles
		NodeList children = Defnode.getChildNodes();
		int index = 0;
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) node;
				if (Enode.getTagName() == "tile") {
					m_tiledefinitions.add(new TileDef(Enode, index));
					index++;

				}
			}

		}
	}

	public int getTileCountX() {
		return m_tileCountX;
	}

	public int getTileCountY() {
		return m_tileCountY;
	}

	public TileDef getDef(int value) {
		return m_tiledefinitions.get(value);
	}

	public void Save(DataOutputStream dstream) throws IOException {
		dstream.writeInt(m_tileCountX);
		dstream.writeInt(m_tileCountY);
		dstream.write(m_tiledefinitions.size());
		for (int i = 0; i < m_tiledefinitions.size(); i++) {
			m_tiledefinitions.get(i).save(dstream);
		}

	}

	public TileDefLibrary(DataInputStream dstream) throws IOException {
		// TODO Auto-generated constructor stub
		m_tileCountX = dstream.readInt();
		m_tileCountY = dstream.readInt();
		m_tiledefinitions = new ArrayList<TileDef>();
		int count = dstream.read();
		for (int i = 0; i < count; i++) {
			m_tiledefinitions.add(new TileDef(dstream, i));
		}
	}

}
