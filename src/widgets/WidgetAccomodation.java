package widgets;

import interactionscreens.BedScreen;
import interactionscreens.ContainerScreen;
import item.Item;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actor.player.Player;
import view.ViewScene;
import actorRPG.RPG_Helper;

import nomad.Universe;

public class WidgetAccomodation extends WidgetBreakable {
	int capacity = 0;

	public int getCapacity() {
		return capacity;
	}

	public WidgetAccomodation(Element node) {
		isWalkable = false;
		isVisionBlocking = true;
		widgetSpriteNumber = Integer.parseInt(node.getAttribute("sprite"));
		m_name = node.getAttribute("name");
		m_resistances = new int[3];
		NodeList children = node.getChildNodes();
		hitpoints = Integer.parseInt(node.getAttribute("health"));

		for (int i = 0; i < children.getLength(); i++) {
			Node N = children.item(i);
			if (N.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) N;
				// run each step successively
				if (Enode.getTagName() == "description") {
					widgetDescription = Enode.getTextContent();
				}
				if (Enode.getTagName() == "health") {
					hitpoints = Integer.parseInt(Enode.getAttribute("value"));
				}
				if (Enode.getTagName() == "resistance") {
					m_resistances[RPG_Helper.AttributefromString(Enode.getAttribute("resists"))] = Integer
							.parseInt(Enode.getAttribute("strength"));
				}
				if (Enode.getTagName() == "capacity") {
					capacity = Integer.parseInt(Enode.getAttribute("value"));
				}
				if (Enode.getTagName() == "contains") {
					int min = Integer.parseInt(Enode.getAttribute("minimum"));
					int max = Integer.parseInt(Enode.getAttribute("maximum"));
					int c = Universe.m_random.nextInt(max - min) + min;
					m_contains = new Item[c];
					for (int j = 0; j < c; j++) {
						m_contains[j] = Universe.getInstance().getLibrary().getItem(Enode.getTextContent());
					}
				}
			}
		}
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		// TODO Auto-generated method stub
		dstream.write(14);
		commonSave(dstream);
		saveBreakable(dstream);
		dstream.writeInt(capacity);
	}

	public WidgetAccomodation(DataInputStream dstream) throws IOException {
		// TODO Auto-generated constructor stub
		commonLoad(dstream);
		load(dstream);
		capacity = dstream.readInt();
	}

	public boolean safeOnly() {
		return true;
	}

	@Override
	public boolean Interact(Player player) {
		ViewScene.m_interface.setScreen(new BedScreen());

		return true;
	}
}
