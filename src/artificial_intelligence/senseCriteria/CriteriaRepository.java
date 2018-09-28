package artificial_intelligence.senseCriteria;

import java.util.HashMap;
import java.util.Map;

public class CriteriaRepository {

	private Map<String,Sense_Criteria> criteriaMap;
	
	private CriteriaFactory factory;
	
	public CriteriaRepository()
	{	
		criteriaMap=new HashMap<String,Sense_Criteria>();
		factory=new CriteriaFactory();
	}
	
	public Sense_Criteria getCriteria(String properties)
	{
		Sense_Criteria crit=criteriaMap.get(properties);
		if (crit!=null)
		{
			return crit;
		}
		
		crit=factory.buildCriteria(properties);
		
		criteriaMap.put(properties, crit);
		
		return crit;
	}
}
