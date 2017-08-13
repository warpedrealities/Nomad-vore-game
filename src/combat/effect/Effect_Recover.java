package combat.effect;

import org.w3c.dom.Element;

import view.ViewScene;

import actor.Actor;
import actor.player.Player;
import actorRPG.Actor_RPG;
import actorRPG.RPG_Helper;
import actorRPG.player.Player_RPG;

public class Effect_Recover extends Effect {

	int m_modifier;
	float m_modstrength;
	int m_modvalue;
	int m_modifies;
	String m_description;
	public final static int SATIATION = 0;
	public final static int HEALTH = 1;
	public final static int RESOLVE = 2;
	public final static int ACTION = 3;

	public static int ModfromString(String str) {
		if (str.contains("SATIATION")) {
			return SATIATION;
		}

		if (str.contains("HEALTH")) {
			return HEALTH;
		}
		if (str.contains("RESOLVE")) {
			return RESOLVE;
		}
		if (str.contains("ACTION")) {
			return ACTION;
		}
		return -1;
	}

	public Effect_Recover(Element Enode) {
		m_modifier = RPG_Helper.abilityFromString(Enode.getAttribute("modifier"));
		m_modifies = ModfromString(Enode.getAttribute("modifies"));
		m_modstrength = Float.parseFloat(Enode.getAttribute("modstrength"));
		m_modvalue = Integer.parseInt(Enode.getAttribute("modvalue"));
		m_description = Enode.getTextContent().replace("\n", "");
	}

	public int getModifier() {
		return m_modifier;
	}

	public float getModStrength() {
		return m_modstrength;
	}

	public int getModifies() {
		return m_modifies;
	}

	@Override
	public int applyEffect(Actor origin, Actor actor, boolean critical) {
		// TODO Auto-generated method stub

		int value = m_modvalue;
		float mod = actor.getRPG().getAbility(getModifier()) * getModStrength();
		value = value + (int) mod;
		switch (getModifies()) {
		case Effect_Recover.SATIATION:
			((Player_RPG) actor.getRPG()).feed(value, false);
			break;

		case Effect_Recover.HEALTH:
			actor.getRPG().IncreaseStat(Actor_RPG.HEALTH, value);
			break;
		case Effect_Recover.RESOLVE:
			actor.getRPG().IncreaseStat(Actor_RPG.RESOLVE, value);
			break;
		case Effect_Recover.ACTION:
			actor.getRPG().IncreaseStat(Actor_RPG.ACTION, value);
			break;
		}
		ViewScene.m_interface.DrawText(m_description);
		return value;
	}

	@Override
	public Effect clone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void applyChange(Effect effect,int rank) {
		// TODO Auto-generated method stub

	}
}
