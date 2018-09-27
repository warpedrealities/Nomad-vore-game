package artificial_intelligence.senseCriteria.Criteria;

import actor.Actor;
import artificial_intelligence.senseCriteria.Sense_Criteria;

public class Criteria_Status implements Sense_Criteria {

	private boolean has;
	private int id;
	
	public Criteria_Status(boolean has, String string1) {
		this.has=has;
		id=Integer.parseInt(string1);
	}

	@Override
	public boolean checkCriteria(Actor actor, Actor origin) {
		if (actor.getRPG().hasStatus(id))
		{
			if (has)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			if (has)
			{
				return false;
			}
			else
			{
				return true;
			}
		}
	}

}
