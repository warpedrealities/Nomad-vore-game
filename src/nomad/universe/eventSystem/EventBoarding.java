package nomad.universe.eventSystem;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import nomad.universe.Universe;
import nomad.universe.eventSystem.events.Event_Impl;
import spaceship.Spaceship;
import spaceship.boarding.BoardingHelper;
import view.ViewScene;

public class EventBoarding extends Event_Impl {

	private String description;
	private String[] spawns;
	private boolean randomSpawn;

	public EventBoarding(Element e) {
		// TODO Auto-generated constructor stub
		spawns = new String[Integer.parseInt(e.getAttribute("count"))];
		int index = 0;
		if (e.getAttribute("randomSpawn").equals("true")) {
			randomSpawn = true;
		}
		NodeList children = e.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) children.item(i);
				if (element.getTagName().equals("description")) {
					description = element.getTextContent();
				}
				if (element.getTagName().equals("spawn")) {
					spawns[index] = element.getAttribute("file");
					index++;
				}
				if (element.getTagName().equals("conditions")) {
					this.construct(element);
				}
			}
		}
	}

	@Override
	public void trigger() {
		BoardingHelper boardingHelper = new BoardingHelper((Spaceship) Universe.getInstance().getCurrentEntity());
		boardingHelper.addNPCs(spawns);
		ViewScene.m_interface.replaceScreen(null);
		ViewScene.m_interface.DrawText(description);
	}

}
