package effect;

import org.w3c.dom.Element;

import actor.player.Player_LOOK;

public class Effect_Criteria_Assertion extends Effect_Criteria {

	boolean negativeAssertion;
	String bodyPart;
	
	public Effect_Criteria_Assertion(Element node)
	{
		if (node.getAttribute("negative").length()>0)
		{
			negativeAssertion=true;
		}
		bodyPart=node.getAttribute("part");
	}
	
	@Override
	public boolean checkCriteria(Player_LOOK look) {
		if (negativeAssertion==true)
		{
			if (look.getPart(bodyPart)==null)
			{
				return true;
			}
		}
		else
		{
			if (look.getPart(bodyPart)!=null)
			{
				return true;
			}			
		}
		return false;
	}

}
