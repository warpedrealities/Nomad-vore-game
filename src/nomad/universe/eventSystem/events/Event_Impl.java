package nomad.universe.eventSystem.events;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actor.player.Player;
import faction.FactionLibrary;
import nomad.universe.Universe;
import nomad.universe.eventSystem.events.eventCriteria.Event_Criteria;
import nomad.universe.eventSystem.events.eventCriteria.factionCriteria;
import nomad.universe.eventSystem.events.eventCriteria.factionFlagCriteria;
import nomad.universe.eventSystem.events.eventCriteria.globalCriteria;

public abstract class Event_Impl implements Event {

	private List<Event_Criteria> criteriaList;

	public void construct(Element element) {
		criteriaList = new ArrayList<Event_Criteria>();
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) children.item(i);
				if (e.getTagName().equals("factionRelationship")) {
					criteriaList.add(new factionCriteria(e));
				}
				if (e.getTagName().equals("globalFlag")) {
					criteriaList.add(new globalCriteria(e));
				}
				if (e.getTagName().equals("factionFlag")) {
					criteriaList.add(new factionFlagCriteria(e));
				}
			}
		}
	}

	@Override
	public boolean eligible() {
		Player player=Universe.getInstance().getPlayer();
		FactionLibrary factionLibrary=FactionLibrary.getInstance();
		for (int i=0;i<criteriaList.size();i++)
		{
			if (!criteriaList.get(i).eligible(player, factionLibrary)) {
				return false;
			}
		}
		return true;
	}

}
