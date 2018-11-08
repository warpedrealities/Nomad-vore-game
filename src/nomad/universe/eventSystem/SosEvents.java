package nomad.universe.eventSystem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import nomad.universe.Universe;
import nomad.universe.eventSystem.events.Event;
import shared.ParserHelper;
import spaceship.Spaceship;
import spaceship.Spaceship.ShipState;
public class SosEvents {
	private int chance;
	private List<Event> events;

	public SosEvents() {
		chance = 0;
		generate();
	}

	private void generate() {
		events = new ArrayList<>();
		File file = new File("assets/data/systems/events/sos");

		// find the names of all files in the item folder
		File[] files = file.listFiles();
		// use reader to generate items
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().contains(".svn") == false) {
				Reader(files[i].getName());
			}
		}
	}

	private void Reader(String name) {
		Document doc = ParserHelper.LoadXML("assets/data/systems/events/sos/" + name);
		Element root = doc.getDocumentElement();
		Element n = (Element) doc.getFirstChild();
		NodeList children = n.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) children.item(i);
				if (e.getTagName().equals("conversation")) {
					events.add(new EventConversation(e));
				}
				if (e.getTagName().equals("boarding")) {
					events.add(new EventBoarding(e));
				}
			}
		}
	}

	public void update(Spaceship ship) {
		if (ship.getState() == ShipState.SOS) {
			chance += 1;
			int r = Universe.m_random.nextInt(10);
			if (r < chance) {
				runEvent(ship);
				chance = 0;
			}
		} else if (chance != 0) {
			chance = 0;
		}
	}

	private void runEvent(Spaceship ship) {
		List<Event> list = new ArrayList<Event>();
		for (int i = 0; i < events.size(); i++) {
			if (events.get(i).eligible()) {
				list.add(events.get(i));
			}
		}

		int r = Universe.m_random.nextInt(list.size());
		ship.setShipState(ShipState.ADRIFT);
		list.get(r).trigger();
	}

	public void reset() {
		chance = 0;
	}

}
