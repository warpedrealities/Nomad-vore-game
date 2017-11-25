package artificial_intelligence.senseCriteria.Criteria;

import actor.Actor;
import actorRPG.Actor_RPG;
import artificial_intelligence.senseCriteria.Sense_Criteria;

public class Criteria_Stat implements Sense_Criteria {

	private boolean above;
	private boolean resolve;
	private float value;
	
	public Criteria_Stat(boolean above, boolean resolve, String string1) {
		this.above=above;
		this.resolve=resolve;
		this.value=Float.parseFloat(string1);
	}

	private boolean eval(Actor actor)
	{
		float v=0;
		float m=0;
		if (resolve)
		{
			v=actor.getRPG().getStat(Actor_RPG.RESOLVE);
			m=actor.getRPG().getStatMax(Actor_RPG.RESOLVE);
		}
		else
		{
			v=actor.getRPG().getStat(Actor_RPG.HEALTH);
			m=actor.getRPG().getStatMax(Actor_RPG.HEALTH);	
		}
		float ratio=v/m;
		if (ratio<value)
		{
			return false;
		}
		return true;
	}
	
	@Override
	public boolean checkCriteria(Actor actor, Actor origin) {

		boolean greaterThan=eval(actor);
		if (above && greaterThan)
		{
			return true;
		}
		if (!above && !greaterThan)
		{
			return true;
		}
		return false;
	}

}
