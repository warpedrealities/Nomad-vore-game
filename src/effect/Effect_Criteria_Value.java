package effect;

import org.w3c.dom.Element;

import description.BodyPart;

import actor.Player_LOOK;

public class Effect_Criteria_Value extends Effect_Criteria {

	boolean lessThan;
	String bodyPart;
	String variable;
	int value;
	
	public Effect_Criteria_Value(Element node)
	{
		if (node.getAttribute("lessthan").length()>0)
		{
			lessThan=true;
		}
		bodyPart=node.getAttribute("part");
		variable=node.getAttribute("variable");
		value=Integer.parseInt(node.getAttribute("value"));
		
	}
	
	@Override
	public boolean checkCriteria(Player_LOOK look) {
		
		BodyPart p=look.getPart(bodyPart);
		if (p!=null)
		{
			int v=p.getValue(variable);
			if (lessThan && v<value)
			{
				return true;
			}
			else if (lessThan==false && v>value)
			{
				return true;
			}
		}
		return false;
	}

}
