package perks;

import org.w3c.dom.Element;

import combat.CombatMove;
import perks.moveModifier.Move_Modifier;

public class PerkMoveModifier extends PerkElement {

	Move_Modifier move;

	public PerkMoveModifier(Element enode) {
		move = new Move_Modifier(enode);
	}

	public Move_Modifier getMove() {
		return move;
	}

}
