package shipsystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ShipShield extends ShipAbility {

	private int hitpoints, absorption, regeneration, restartTime;
	private float energyCost;

	public ShipShield(Element node, String name) {
		abilityType = AbilityType.SA_SHIELD;
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) children.item(i);
				if (e.getTagName().equals("hitpoints")) {
					hitpoints = Integer.parseInt(e.getAttribute("value"));
				}
				if (e.getTagName().equals("absorption")) {
					absorption = Integer.parseInt(e.getAttribute("value"));
				}
				if (e.getTagName().equals("regeneration")) {
					regeneration = Integer.parseInt(e.getAttribute("value"));
				}
				if (e.getTagName().equals("restartTime")) {
					restartTime = Integer.parseInt(e.getAttribute("value"));
				}
				if (e.getTagName().equals("energyCost")) {
					energyCost = Float.parseFloat(e.getAttribute("value"));
				}
			}
		}
	}

	public ShipShield(DataInputStream dstream, String m_name) throws IOException {
		abilityType = AbilityType.SA_SHIELD;
		hitpoints = dstream.readInt();
		absorption = dstream.readInt();
		regeneration = dstream.readInt();
		restartTime = dstream.readInt();
		energyCost = dstream.readFloat();
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		// TODO Auto-generated method stub
		dstream.writeInt(hitpoints);
		dstream.writeInt(absorption);
		dstream.writeInt(regeneration);
		dstream.writeInt(restartTime);
		dstream.writeFloat(energyCost);
	}

	public int getHitpoints() {
		return hitpoints;
	}

	public int getAbsorption() {
		return absorption;
	}

	public int getRegeneration() {
		return regeneration;
	}

	public int getRestartTime() {
		return restartTime;
	}

	public float getEnergyCost() {
		return energyCost;
	}

}
