package effect;

import org.w3c.dom.Element;

import actor.player.Player;
import view.ViewScene;
import actorRPG.Actor_RPG;
import actorRPG.RPG_Helper;

public class Effect_Recover extends Effect {

	int m_modifier;
	float m_modstrength;
	int m_modvalue;
	int m_modifies;
	String m_description;
	public final static int SATIATION = 0;
	public final static int HEALTH = 1;

	public static int ModfromString(String str) {
		if (str.contains("SATIATION")) {
			return SATIATION;
		}

		if (str.contains("HEALTH")) {
			return HEALTH;
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
	public void applyEffect(Player player) {
		// TODO Auto-generated method stub

		int value = m_modvalue;
		float mod = player.getRPG().getAbility(getModifier()) * getModStrength();
		value = value + (int) mod;
		switch (getModifies()) {
		case Effect_Recover.SATIATION:
			player.getRPG().IncreaseStat(Actor_RPG.SATIATION, value);
			break;

		case Effect_Recover.HEALTH:
			player.getRPG().IncreaseStat(Actor_RPG.HEALTH, value);
			break;

		}
		ViewScene.m_interface.DrawText(m_description);
	}
}
