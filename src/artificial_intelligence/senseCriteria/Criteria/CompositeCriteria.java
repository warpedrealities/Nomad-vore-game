package artificial_intelligence.senseCriteria.Criteria;

import java.util.ArrayList;
import java.util.List;

import actor.Actor;
import artificial_intelligence.senseCriteria.Sense_Criteria;

public class CompositeCriteria implements Sense_Criteria {

	private List<Sense_Criteria> criteria;
	
	public CompositeCriteria()
	{
		criteria=new ArrayList<Sense_Criteria>();
	}
	
	@Override
	public boolean checkCriteria(Actor actor,Actor origin) {
		for (int i=0;i<criteria.size();i++)
		{
			if (!criteria.get(i).checkCriteria(actor,origin))
			{
				return false;
			}
		}
		return true;
	}

	public void add(Sense_Criteria genCriteria) {
		criteria.add(genCriteria);
	}

}
