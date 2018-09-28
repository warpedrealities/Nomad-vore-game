package artificial_intelligence.senseCriteria.Criteria;

import actor.Actor;
import artificial_intelligence.senseCriteria.Sense_Criteria;

public class Criteria_Name implements Sense_Criteria {

	private String name;
	public Criteria_Name(String name) {
		this.name=name;
	}

	@Override
	public boolean checkCriteria(Actor actor, Actor origin) {
		if (actor.getName().equals(name))
		{
			return true;
		}
		return false;
	}

}
