package combat.effect;

import org.w3c.dom.Element;

import actor.Actor;
import actorRPG.player.Player_RPG;
import view.ViewScene;

public class Effect_Modifier extends Effect {

	public static final int KARMA = 0;

	int modWhat;
	float strength;
	String m_description;

	public Effect_Modifier(Element enode) {
		// TODO Auto-generated constructor stub
		modWhat = modFromString(enode.getAttribute("modifies"));
		strength = Float.parseFloat(enode.getAttribute("modvalue"));
		m_description = enode.getTextContent().replace("\n", "");
	}

	public int modFromString(String string) {
		if (string.equals("KARMA")) {
			return 0;
		}
		return -1;
	}

	@Override
	public int applyEffect(Actor origin, Actor target, boolean critical) {
		// TODO Auto-generated method stub
		int v = 0;
		switch (modWhat) {
		case KARMA:
			((Player_RPG) target.getRPG()).modKarmaMeter(strength);
			v = ((Player_RPG) target.getRPG()).getKarmaMeter();
			break;

		}
		ViewScene.m_interface.DrawText(m_description.replace("VALUE", Integer.toString(v)));
		return 0;
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
