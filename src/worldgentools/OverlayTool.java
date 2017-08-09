package worldgentools;

import org.w3c.dom.Element;

import zone.Tile;
import zone.Zone;

public class OverlayTool {

	Zone m_zone;

	public OverlayTool(Zone zone) {
		m_zone = zone;
	}

	public void runTrees(Element enode) {
		// find tile we need to place over
		int base = Integer.parseInt(enode.getAttribute("base"));
		int top = Integer.parseInt(enode.getAttribute("top"));

		for (int i = 0; i < m_zone.getWidth(); i++) {
			for (int j = 0; j < m_zone.getHeight() - 1; j++) {
				if (m_zone.getTile(i, j) != null) {
					Tile t = m_zone.getTile(i, j);
					int id = t.getDefinition().getID();
					if (id == base) {
						if (m_zone.getTile(i, j + 1) != null) {
							m_zone.getTile(i, j + 1).setOverlayImage(top);
						}
					}
				}
			}
		}
	}

}
