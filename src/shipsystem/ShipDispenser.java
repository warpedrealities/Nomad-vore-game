package shipsystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import shared.ParserHelper;
import shipsystem.ShipAbility.AbilityType;

public class ShipDispenser extends ShipAbility {

	String widgetName;
	int cost;
	String outputItem;
	String input;

	public ShipDispenser(DataInputStream dstream, String m_name) throws IOException {
		abilityType = AbilityType.SA_DISPENSER;
		this.widgetName = m_name;
		cost = dstream.readInt();
		outputItem = ParserHelper.LoadString(dstream);
		input = ParserHelper.LoadString(dstream);
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		dstream.writeInt(cost);
		ParserHelper.SaveString(dstream, outputItem);
		ParserHelper.SaveString(dstream, input);
	}

	public ShipDispenser(Element enode, String m_name) {
		abilityType = AbilityType.SA_DISPENSER;
		this.widgetName = m_name;
		NodeList children = enode.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			Node N = children.item(i);
			if (N.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) N;

				if (Enode.getTagName() == "input") {
					input = Enode.getAttribute("value");
				}
				if (Enode.getTagName() == "cost") {
					cost = Integer.parseInt(Enode.getAttribute("value"));
				}
				if (Enode.getTagName() == "output") {
					outputItem = Enode.getAttribute("value");
				}
			}
		}

	}

	public int getCost() {
		return cost;
	}

	public String getOutputItem() {
		return outputItem;
	}

	public String getInput() {
		return input;
	}

}
