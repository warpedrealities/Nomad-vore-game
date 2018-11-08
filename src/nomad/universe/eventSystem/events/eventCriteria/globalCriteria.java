package nomad.universe.eventSystem.events.eventCriteria;

import org.w3c.dom.Element;

import actor.player.Player;
import faction.FactionLibrary;

public class globalCriteria implements Event_Criteria {


	private int flagValue;
	private String flagName;
	private Comparison comparison;

	public globalCriteria(Element e) {
		flagValue = Integer.parseInt(e.getAttribute("value"));
		flagName = e.getAttribute("flag");
		comparison = Comparison.valueOf(e.getAttribute("comparison"));
	}

	@Override
	public boolean eligible(Player player, FactionLibrary factionLibrary) {
		int c = player.getFlags().readFlag(flagName);
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
