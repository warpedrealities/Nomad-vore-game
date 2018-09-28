package effect;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actor.player.Player;
import view.ViewScene;

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
	public void applyEffect(Player player) {

		// check criteria
		for (int i = 0; i < criteriaList.size(); i++) {
			if (criteriaList.get(i).checkCriteria(player.getLook()) == false) {
				return;
			}
		}
		StringBuilder builder = new StringBuilder();
		// run mutations
		for (int i = 0; i < mutationList.size(); i++) {
			mutationList.get(i).processMutation(player.getLook(), builder);
		}
		ViewScene.m_interface.DrawText(builder.toString());
	}

}
