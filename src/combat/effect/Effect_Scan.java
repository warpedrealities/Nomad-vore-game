package combat.effect;

import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actor.Actor;
import actor.npc.NPC;
import actorRPG.RPG_Helper;
import actorRPG.npc.NPC_RPG;
import nomad.universe.Universe;
import view.ViewScene;

public class Effect_Scan extends Effect {

	private String statusTag;
	private int skill,minSkill,minStrength,minRange;
	private float rangeBonus,strengthBonus;

	public Effect_Scan(Element e)
	{
		minSkill=Integer.parseInt(e.getAttribute("minSkill"));
		skill=RPG_Helper.AttributefromString(e.getAttribute("skill"));
		NodeList children=e.getChildNodes();
		for (int i=0;i<children.getLength();i++)
		{
			if (children.item(i).getNodeType()==Node.ELEMENT_NODE)
			{
				Element enode=(Element)children.item(i);
				if (enode.getTagName().equals("statusTag"))
				{
					statusTag=enode.getAttribute("value");
				}
				if (enode.getTagName().equals("strength"))
				{
					minStrength=Integer.parseInt(enode.getAttribute("min"));
					strengthBonus=Float.parseFloat(enode.getAttribute("bonus"));
				}
				if (enode.getTagName().equals("range"))
				{
					minRange=Integer.parseInt(enode.getAttribute("min"));
					rangeBonus=Float.parseFloat(enode.getAttribute("bonus"));
				}
			}
		}
	}


	public Effect_Scan() {
	}

	private int getRange(Actor origin)
	{
		int range=minRange+(int)(rangeBonus*origin.getRPG().getAttribute(skill));
		if (range>12)
		{
			range=12;
		}
		return range;
	}

	private int getStrength(Actor origin)
	{
		return minStrength+(int)(strengthBonus*origin.getRPG().getAttribute(skill));

	}

	private void scan(Actor origin)
	{
		List<Actor> actors=Universe.getInstance().getCurrentZone().getActors();
		int range=getRange(origin);
		int strength=getStrength(origin);
		for (int i=0;i<actors.size();i++)
		{
			if (actors.get(i)!=origin &&
					actors.get(i).getPosition().getDistance(origin.getPosition())<range &&
					!actors.get(i).getVisible())
			{
				if (NPC.class.isInstance(actors.get(i)))
				{
					NPC_RPG rpg=(NPC_RPG)actors.get(i).getRPG();
					if (!rpg.getTagged(statusTag))
					{
						if (actors.get(i).getRPG().getStealthState()!=-1)
						{
							if (actors.get(i).getRPG().stealthCheck(strength,false))
							{
								ViewScene.m_interface.Flash(actors.get(i).getPosition(), 5);
							}
						}
						else
						{
							ViewScene.m_interface.Flash(actors.get(i).getPosition(), 5);
						}
					}
				}
			}
		}
	}


	@Override
	public int applyEffect(Actor origin, Actor target, boolean critical) {
		if (origin.getRPG().getAttribute(skill)<minSkill)
		{
			ViewScene.m_interface.DrawText("You don't meet the requirements to do this");
			return 0;
		}
		scan(origin);
		return 1;
	}

	@Override
	public Effect clone() {
		Effect_Scan effect=new Effect_Scan();
		effect.statusTag=statusTag;
		effect.skill=skill;
		effect.minSkill=minSkill;
		effect.minStrength=minStrength;
		effect.minRange=minRange;
		effect.rangeBonus=rangeBonus;
		effect.strengthBonus=strengthBonus;
		return effect;
	}

	@Override
	public void applyChange(Effect effect, int rank, boolean proportionate) {
		// TODO Auto-generated method stub

	}

}
