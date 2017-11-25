package artificial_intelligence.senseCriteria;

import artificial_intelligence.senseCriteria.Criteria.CompositeCriteria;
import artificial_intelligence.senseCriteria.Criteria.Criteria_Defeat;
import artificial_intelligence.senseCriteria.Criteria.Criteria_Faction;
import artificial_intelligence.senseCriteria.Criteria.Criteria_Hostile;
import artificial_intelligence.senseCriteria.Criteria.Criteria_Name;
import artificial_intelligence.senseCriteria.Criteria.Criteria_Stat;
import artificial_intelligence.senseCriteria.Criteria.Criteria_Status;

public class CriteriaFactory {
	
	public Sense_Criteria buildCriteria(String properties) {
		String [] substrings=properties.split(",");
		if (substrings.length<=2)
		{
			return genCriteria(substrings[0],substrings[1]);
		}
		else
		{
			CompositeCriteria composite=new CompositeCriteria();
			for (int i=0;i<substrings.length;i+=2)
			{
				composite.add(genCriteria(substrings[i],substrings[i+1]));
			}
			return composite;
		}
		
	}

	private Sense_Criteria genCriteria(String string0, String string1) {
		// TODO Auto-generated method stub
		if (string0.equals("name"))
		{
			return new Criteria_Name(string1);
		}
		if (string0.equals("faction"))
		{
			return new Criteria_Faction(string1);
		}
		if (string0.equals("hasStatus"))
		{
			return new Criteria_Status(true,string1);
		}
		if (string0.equals("hasNotStatus"))
		{
			return new Criteria_Status(false,string1);
		}
		if (string0.equals("defeated"))
		{
			return new Criteria_Defeat(string1);
		}
		if (string0.equals("healthBelow"))
		{
			return new Criteria_Stat(false,false,string1);
		}
		if (string0.equals("healthAbove"))
		{
			return new Criteria_Stat(true,false,string1);
		}
		if (string0.equals("resolveBelow"))
		{
			return new Criteria_Stat(false,true,string1);
		}
		if (string0.equals("resolveAbove"))
		{
			return new Criteria_Stat(true,true,string1);
		}
		if (string0.equals("hostile"))
		{
			return new Criteria_Hostile();
		}
		return null;
	}

}
