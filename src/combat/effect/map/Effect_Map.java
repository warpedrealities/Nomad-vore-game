package combat.effect.map;

import org.w3c.dom.Element;

import actor.Actor;
import actorRPG.RPG_Helper;
import combat.effect.Effect;
import view.ViewScene;

public class Effect_Map extends Effect {

	private int attribute;
	private int []thresholds;
	private final static int VAGUE=0;
	private final static int DETAIL=1;
	private final static int PRECISE=2;

	public Effect_Map(Element e) {
		attribute=RPG_Helper.AttributefromString(e.getAttribute("skill"));
		thresholds=new int[3];
		thresholds[VAGUE]=Integer.parseInt(e.getAttribute("vague"));
		thresholds[DETAIL]=Integer.parseInt(e.getAttribute("detail"));
		thresholds[PRECISE]=Integer.parseInt(e.getAttribute("precise"));
	}

	@Override
	public int applyEffect(Actor origin, Actor target, boolean critical) {
		int strength=0;
		int attributeValue=origin.getRPG().getAttribute(attribute);
		if (attributeValue>=thresholds[PRECISE])
		{
			strength=3;
		}
		else
		{
			if (attributeValue>=thresholds[DETAIL])
			{
				strength=2;
			}
			else
			{
				if (attributeValue>=thresholds[VAGUE])
				{
					strength=1;
				}
			}
		}
		if (strength>0)
		{

			ViewScene.m_interface.replaceScreen(new MapScreen(strength));
		}
		else
		{
			ViewScene.m_interface.DrawText("You lack the necessary skills to do this");
			return 0;
		}

		return 1;
	}

	@Override
	public Effect clone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void applyChange(Effect effect, int rank, boolean proportionate) {
		// TODO Auto-generated method stub

	}

}
