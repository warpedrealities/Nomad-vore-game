package nomad.universe.eventSystem.events.eventCriteria;

import actor.player.Player;
import faction.FactionLibrary;

public interface Event_Criteria {
	enum Comparison {
		lessthan, equals, greaterthanorequals
	};
	boolean eligible(Player player, FactionLibrary factionLibrary);
}
