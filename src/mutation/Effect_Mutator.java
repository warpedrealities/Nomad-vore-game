package mutation;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import combat.effect.Effect;
import combat.effect.Effect_Criteria;
import combat.effect.Effect_Criteria_Assertion;
import combat.effect.Effect_Criteria_Value;

import view.ViewScene;

import actor.Actor;
import actor.player.Player;

public class Effect_Mutator extends Effect {

	ArrayList<Effect_Criteria> criteriaList;
	ArrayList<Mutation> mutationList;

	public Effect_Mutator(Element Enode) {
		criteriaList = new ArrayList<Effect_Criteria>();
		mutationList = new ArrayList<Mutation>();

		NodeList children = Enode.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element E = (Element) node;
				if (E.getTagName().equals("criteriaValue")) {
					criteriaList.add(new Effect_Criteria_Value(E));
				}
				if (E.getTagName().equals("criteriaAssertion")) {
					criteriaList.add(new Effect_Criteria_Assertion(E));
				}
				if (E.getTagName().equals("mutation")) {
					mutationList.add(new Mutation(E));
				}
			}
		}
	}

	@Override
	public int applyEffect(Actor origin, Actor actor, boolean critical) {

		Player player = (Player) actor;
		// check criteria
		for (int i = 0; i < criteriaList.size(); i++) {
			if (criteriaList.get(i).checkCriteria(player.getLook()) == false) {
				return 0;
			}
		}
		StringBuilder builder = new StringBuilder();
		// run mutations
		for (int i = 0; i < mutationList.size(); i++) {
			mutationList.get(i).processMutation(player.getLook(), builder);
		}
		if (ViewScene.m_interface != null && builder.length() > 0) {
			ViewScene.m_interface.DrawText(builder.toString());
		}
		return 1;
	}

	@Override
	public Effect clone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void applyChange(Effect effect) {
		// TODO Auto-generated method stub

	}

}
