package artificial_intelligence.senseCriteria.Criteria;

import actor.Actor;
import artificial_intelligence.senseCriteria.Sense_Criteria;

public class Criteria_Faction implements Sense_Criteria {

	private String faction;
	
	public Criteria_Faction(String faction)
	{
		this.faction=faction;
	}
	
	@Override
	public boolean checkCriteria(Actor actor, Actor origin) {
		if (actor.getActorFaction().getFilename().equals(faction))
		{
			return true;
		}
		return false;
	}

}
