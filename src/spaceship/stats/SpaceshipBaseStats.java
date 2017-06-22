package spaceship.stats;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SpaceshipBaseStats {

	private int maxHullPoints;
	private float thrustCost;
	private float moveCost;
	private int manouverability;
	private int armour;

	public SpaceshipBaseStats(Element n) {
		NodeList children = n.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) node;
				if (Enode.getTagName().contains("hullpoints")) {
					maxHullPoints = Integer.parseInt(Enode.getAttribute("value"));
				}
				if (Enode.getTagName().contains("movecost")) {
					moveCost = Float.parseFloat(Enode.getAttribute("value"));
				}
				if (Enode.getTagName().contains("thrustcost")) {
					thrustCost = Float.parseFloat(Enode.getAttribute("value"));
				}
				if (Enode.getTagName().contains("armour")) {
					armour = Integer.parseInt(Enode.getAttribute("value"));
				}
				if (Enode.getTagName().contains("manouver")) {
					manouverability = Integer.parseInt(Enode.getAttribute("value"));
				}
			}
		}
	}

	public SpaceshipBaseStats(DataInputStream dstream) throws IOException {

		maxHullPoints = dstream.readInt();
		thrustCost = dstream.readFloat();
		moveCost = dstream.readFloat();
		armour = dstream.readInt();
		manouverability = dstream.readInt();
	}

	public void save(DataOutputStream dstream) throws IOException {
		dstream.writeInt(maxHullPoints);
		dstream.writeFloat(thrustCost);
		dstream.writeFloat(moveCost);
		dstream.writeInt(armour);
		dstream.writeInt(manouverability);
	}

	public int getMaxHullPoints() {
		return maxHullPoints;
	}

	public float getThrustCost() {
		return thrustCost;
	}

	public float getMoveCost() {
		return moveCost;
	}

	public int getManouverability() {
		return manouverability;
	}

	public int getArmour() {
		return armour;
	}

}
