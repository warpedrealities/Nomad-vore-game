package artificial_intelligence.senseCriteria.Criteria;

import actor.Actor;
import actorRPG.Actor_RPG;
import artificial_intelligence.senseCriteria.Sense_Criteria;

public class Criteria_Defeat implements Sense_Criteria {

	private boolean resolve;
	
	public Criteria_Defeat(String string1) {
		if (string1.equals("RESOLVE"))
		{
			resolve=true;
		}
	}

	@Override
	public boolean checkCriteria(Actor actor, Actor origin) {
		if (resolve)
		{
			if (actor.getRPG().getStat(Actor_RPG.RESOLVE)<=0)
			{
				return true;
			}		
		}
		else
		{
			if (actor.getRPG().getStat(Actor_RPG.HEALTH)<=0)
			{
				return true;
			}					
		}
		return false;
	}

}
