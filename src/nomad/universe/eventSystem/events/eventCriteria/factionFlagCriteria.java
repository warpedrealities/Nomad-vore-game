package nomad.universe.eventSystem.events.eventCriteria;

import org.w3c.dom.Element;

import actor.player.Player;
import faction.FactionLibrary;

public class factionFlagCriteria implements Event_Criteria {

	private int flagValue;
	private String flagName;
	private String factionName;
	private Comparison comparison;

	public factionFlagCriteria(Element e) {
		flagValue = Integer.parseInt(e.getAttribute("value"));
		factionName = e.getAttribute("faction");
		comparison = Comparison.valueOf(e.getAttribute("comparison"));
		flagName = e.getAttribute("flag");
	}

	@Override
	public boolean eligible(Player player, FactionLibrary factionLibrary) {
		int c = factionLibrary.getFaction(factionName).getFactionFlags().readFlag(flagName);
		switch (comparison) {
		case lessthan:
			if (c < flagValue) {
				return true;
			}
			break;

		case equals:
			if (c == flagValue) {
				return true;
			}
			break;

		case greaterthanorequals:
			if (c >= flagValue) {
				return true;
			}
			break;

		}
		return false;
	}

}
