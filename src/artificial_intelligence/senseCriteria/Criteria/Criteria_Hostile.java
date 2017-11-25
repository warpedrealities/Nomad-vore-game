package artificial_intelligence.senseCriteria.Criteria;

import actor.Actor;
import artificial_intelligence.senseCriteria.Sense_Criteria;

public class Criteria_Hostile implements Sense_Criteria {

	@Override
	public boolean checkCriteria(Actor actor, Actor origin) {
		if (actor.isHostile(origin.getActorFaction().getFilename()))
		{
			return true;
		}
		return false;
	}

}
