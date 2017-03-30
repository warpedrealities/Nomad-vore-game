package combat.effect;

import org.w3c.dom.Element;

import actor.Actor;
import combat.effect.Effect;

public class Effect_Dialogue extends Effect {

	String dialogue;
	public Effect_Dialogue(Element enode) {
		dialogue=enode.getTextContent().replace("\n", "");
	}

	@Override
	public int applyEffect(Actor origin, Actor target, boolean critical) {
		view.ViewScene.m_interface.StartConversation(dialogue, null);
		return 1;
	}

	@Override
	public Effect clone() {
		return null;
	}

	@Override
	public void applyChange(Effect effect) {
		// TODO Auto-generated method stub

	}

}
