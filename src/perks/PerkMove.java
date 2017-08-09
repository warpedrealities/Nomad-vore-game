package perks;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import combat.CombatMove;

public class PerkMove extends PerkElement {

	CombatMove[] moves;

	PerkMove(Element node) {
		moves = new CombatMove[Integer.parseInt(node.getAttribute("count"))];
		NodeList children = node.getElementsByTagName("combatmove");

		for (int i = 0; i < children.getLength(); i++) {
			Element e = (Element) children.item(i);
			moves[i] = new CombatMove(e);
		}
	}

	public int getCount() {
		return moves.length;
	}

	public CombatMove getMove(int index) {
		return moves[index - 1];
	}
}
