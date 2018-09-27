package combat.effect;

import org.w3c.dom.Element;

import perks.PerkLibrary;
import view.ViewScene;

import actor.Actor;
import actorRPG.player.Player_RPG;

public class Effect_Perk extends Effect {

	String perkName;
	int experienceValue;

	public Effect_Perk(Element node) {
		perkName = node.getAttribute("perk");
		experienceValue = Integer.parseInt(node.getAttribute("experiencevalue"));
	}

	@Override
	public int applyEffect(Actor origin, Actor target, boolean critical) {

		Player_RPG rpg = (Player_RPG) target.getRPG();
		if (rpg.getPerkInstance(perkName) == null) {
			rpg.addPerk(PerkLibrary.getInstance().findPerk(perkName));
			ViewScene.m_interface.DrawText("you gained perk " + perkName);
		} else {
			rpg.addEXP(experienceValue);
		}

		return 0;
	}

	@Override
	public Effect clone() {

		return null;
	}

	@Override
	public void applyChange(Effect effect,int rank) {
		// TODO Auto-generated method stub

	}

}
