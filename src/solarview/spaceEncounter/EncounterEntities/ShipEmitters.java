package solarview.spaceEncounter.EncounterEntities;

import shared.Vec2f;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ShipEmitters {

	private List<Vec2f> engineEmitters;
	private List<Vec2f> weaponEmitters;

	public ShipEmitters(Element node) {
		engineEmitters = new ArrayList<Vec2f>();
		weaponEmitters = new ArrayList<Vec2f>();

		NodeList list = node.getChildNodes();

		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) list.item(i);
				if (e.getTagName().equals("weapon")) {
					weaponEmitters.add(new Vec2f(Float.parseFloat(e.getAttribute("x")),Float.parseFloat(e.getAttribute("y"))));
				}
				if (e.getTagName().equals("engine")) {
					engineEmitters.add(new Vec2f(Float.parseFloat(e.getAttribute("x")),Float.parseFloat(e.getAttribute("y"))));
				}

			}
		}

	}

	public List<Vec2f> getEngineEmitters() {
		return engineEmitters;
	}

	public List<Vec2f> getWeaponEmitters() {
		return weaponEmitters;
	}

}
