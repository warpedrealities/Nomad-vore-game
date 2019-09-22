package combat.effect;

import org.w3c.dom.Element;

import actor.Actor;
import actorRPG.Actor_RPG;
import actorRPG.RPG_Helper;
import nomad.universe.Universe;

public class Effect_Damage extends Effect {

	private int damageType;
	private int minValue;
	private int maxValue;
	private int modifierAbility;
	private float rangeDecay;
	private float penetration;

	public Effect_Damage(int damageType,int minValue, int maxValue,int modifierAbility)
	{
		this.damageType=damageType;
		this.minValue=minValue;
		this.maxValue=maxValue;
		this.modifierAbility=modifierAbility;
	}

	public Effect_Damage(Element enode)
	{
		damageType=RPG_Helper.AttributefromString(enode.getAttribute("type"));
		minValue=Integer.parseInt(enode.getAttribute("minValue"));
		maxValue=Integer.parseInt(enode.getAttribute("maxValue"));
		modifierAbility=RPG_Helper.abilityFromString(enode.getAttribute("modifier"));
		if (enode.getAttribute("penetration").length() > 0) {
			penetration = Float.parseFloat(enode.getAttribute("penetration"));
		}
		if (enode.getAttribute("rangeDecay").length()>0)
		{
			rangeDecay=Float.parseFloat(enode.getAttribute("rangeDecay"));
		}
	}

	public Effect_Damage() {
		// TODO Auto-generated constructor stub
	}

	private int applyCritical(Actor target, int strength,int bonus)
	{
		switch (damageType)
		{
		case Actor_RPG.THERMAL:
			strength=maxValue-target.getRPG().getAttribute(damageType)+bonus;
			break;

		case Actor_RPG.KINETIC:
			if (strength<1)
			{
				strength=1;
			}
			strength=strength*2;
			break;

		case Actor_RPG.SHOCK:
			target.addBusy(strength/2);
			break;

		case Actor_RPG.TEASE:
			if (strength < 1) {
				strength = 1;
			}
			strength = strength * 2;
			break;
		case Actor_RPG.PHEREMONE:
			if (strength<1)
			{
				strength=1;
			}
			strength=strength*2;
			break;
		case Actor_RPG.PSYCHIC:
			strength=maxValue-target.getRPG().getAttribute(damageType)+bonus;
			break;
		}
		return strength;
	}

	@Override
	public int applyEffect(Actor origin,Actor target, boolean critical) {

		//get bonus from originator
		int bonus=0;
		if (modifierAbility!=-1)
		{
			bonus=origin.getRPG().getAbilityMod(modifierAbility);
		}
		//roll damage
		int damage = minValue + bonus;
		if (maxValue>minValue)
		{
			damage+=Universe.m_random.nextInt(maxValue-minValue);
		}
		if (critical)
		{
			damage++;
		}
		damage=target.getRPG().getStatusEffectHandler().runDefenceStack(damage, damageType);
		//reduce damage by damage resistance
		damage -= ((target.getRPG().getAttribute(damageType)) * ((1.0F) - penetration));

		if (rangeDecay>0)
		{
			float d=target.getPosition().getDistance(origin.getPosition());
			if (d>2)
			{
				float rd=(d-1)*rangeDecay;
				damage=damage-(int)rd;
			}
		}

		if (damage<0)
		{
			damage=0;
		}
		if (damage>=999)
		{
			origin.Defeat(null, false);
			return 0;
		}
		if (critical)
		{
			damage=applyCritical(target,damage, bonus);
		}

		//check if physical harm
		if (damageType<=Actor_RPG.SHOCK)
		{
			if (target.getRPG().getStat(Actor_RPG.HEALTH)>0)
			{
				//if so apply to hp
				target.getRPG().ReduceStat(Actor_RPG.HEALTH, damage, true);
				if (target.getRPG().getStat(Actor_RPG.HEALTH)<=0)
				{
					target.Defeat(origin, false);
				}
			}
		}
		else if (target.getRPG().getStat(Actor_RPG.RESOLVE)>0)
		{
			//apply to resolve
			target.getRPG().ReduceStat(Actor_RPG.RESOLVE, damage, true);
			if (target.getRPG().getStat(Actor_RPG.RESOLVE)<=0)
			{
				target.Defeat(origin, true);
			}
		}



		return damage;
	}

	public int getDamageType() {
		return damageType;
	}

	public int getMinValue() {
		return minValue;
	}

	public int getMaxValue() {
		return maxValue;
	}

	public int getModifierAbility() {
		return modifierAbility;
	}

	@Override
	public boolean harmless() {

		if (damageType>=Actor_RPG.TEASE)
		{
			return true;
		}
		return false;
	}

	@Override
	public Effect clone() {

		Effect_Damage effect=new Effect_Damage();
		effect.damageType=damageType;
		effect.minValue = minValue;
		effect.maxValue=maxValue;
		effect.modifierAbility=modifierAbility;
		effect.rangeDecay=rangeDecay;
		effect.penetration = penetration;
		return effect;
	}

	@Override
	public void applyChange(Effect effect, int rank, boolean proportionate) {
		if (this.getClass().isInstance(effect))
		{
			Effect_Damage ed=(Effect_Damage)effect;
			if (proportionate) {
				float min = this.minValue;
				float max = this.maxValue;
				float minR = ((float) ed.getMinValue() * rank) / 100;
				float maxR = ((float) ed.getMaxValue() * rank) / 100;
				this.minValue += min * minR;
				this.maxValue += max * maxR;
			} else {
				this.minValue += ed.minValue * rank;
				this.maxValue += ed.maxValue * rank;
			}
			if (ed.rangeDecay!=0)
			{
				this.rangeDecay*=Math.pow(ed.rangeDecay, rank);
			}
			if (this.penetration == 0) {
				this.penetration = ed.penetration;
			} else {
				this.penetration += (ed.penetration * this.penetration);
			}

		}
	}


}
