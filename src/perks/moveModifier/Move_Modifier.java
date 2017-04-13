package perks.moveModifier;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import combat.CombatMove;
import combat.CombatMove.AttackPattern;
import combat.effect.Effect;
import combat.effect.Effect_Damage;
import combat.effect.Effect_Recover;
import combat.effect.Effect_Status;
import perks.moveModifier.Effect_Change.modifierType;

public class Move_Modifier {

	private String moveName;
	int timeModifier;
	int attackBonus;
	int actionCost;
	boolean basicAction=false;
	private AttackPattern attackPattern;
	
	private ArrayList<Effect_Change> modifiers;
	
	public Move_Modifier(Element Enode)
	{
		moveName=Enode.getAttribute("name");
		if (Enode.getAttribute("basicAction").equals("true"))
		{
			basicAction=true;
		}
		//action cost
		if (Enode.getAttribute("actionCost").length()>0)
		{
			actionCost=Integer.parseInt(Enode.getAttribute("actionCost"));
		}
		if (Enode.getAttribute("bonusToHit").length()>0)
		{
			attackBonus=Integer.parseInt(Enode.getAttribute("bonusToHit"));
		}
		if (Enode.getAttribute("pattern").length()>0)
		{
			attackPattern=CombatMove.strToPattern(Enode.getAttribute("pattern"));
		}
		if (Enode.getAttribute("timeModifier").length()>0)
		{
			timeModifier=Integer.parseInt(Enode.getAttribute("timeModifier"));
		}	
		modifiers=new ArrayList<Effect_Change>();
		NodeList list=Enode.getChildNodes();
		for (int i=0;i<list.getLength();i++)
		{
			if (list.item(i).getNodeType()==Node.ELEMENT_NODE)
			{
				Element e=(Element)list.item(i);
				if (e.getTagName().equals("change"))
				{
					Effect_Change modifier=new Effect_Change(e);
					modifiers.add(modifier);
				}
			}
		}
	}

	public String getMoveName() {
		return moveName;
	}
	
	public void applyModifier(CombatMove move, int rank)
	{
		move.setAttackBonus(move.getAttackBonus()+attackBonus);
		if (timeModifier>0)
		{
			move.setTimeCost(move.getTimeCost()*timeModifier);		
		}
		move.setActionCost(move.getActionCost()*actionCost);
		move.setAttackPattern(attackPattern);
		
		int index=0;
		for (int i=0;i<modifiers.size();i++)
		{
			if (modifiers.get(i).getType()==modifierType.ADD)
			{
				move.getEffects().add(modifiers.get(i).getEffect());
			}
			else if (modifiers.get(i).getEffect().getClass().isInstance(move.getEffects().get(index)))
			{
				
				applyChange(move.getEffects().get(index),modifiers.get(i).getEffect());			
				index++;
			}
		}
	}
	
	private void applyChange(Effect effect, Effect modifier)
	{
		effect.applyChange(modifier);
	}

}
