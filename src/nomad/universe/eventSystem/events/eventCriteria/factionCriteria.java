package nomad.universe.eventSystem.events.eventCriteria;

import org.w3c.dom.Element;

import actor.player.Player;
import faction.FactionLibrary;

public class factionCriteria implements Event_Criteria {
	private int factionValue;
	private String factionName;
	private Comparison comparison;

	public factionCriteria(Element e) {
		factionValue = Integer.parseInt(e.getAttribute("value"));
		factionName = e.getAttribute("faction");
		comparison = Comparison.valueOf(e.getAttribute("comparison"));
	}

	@Override
	public boolean eligible(Player player, FactionLibrary factionLibrary) {
		int c = factionLibrary.getFaction(factionName).getRelationship("player");
		switch (comparison) {
		case lessthan:
			if (c < factionValue) {
				return true;
			}
			break;

		case equals:
			if (c == factionValue) {
				return true;
			}
			break;

		case greaterthanorequals:
			if (c >= factionValue) {
				return true;
			}
			break;

		}
		return false;
	}

}
