package perks;

import org.w3c.dom.Element;

import combat.CombatMove;
import perks.moveModifier.Move_Modifier;

public class PerkWeaponMove extends PerkElement {

	Move_Modifier move;
	String tag;
	public PerkWeaponMove(Element enode) {
		move=new Move_Modifier(enode);
		tag=enode.getAttribute("tag");
	}

	public Move_Modifier getMove() {
		return move;
	}

	public String getTag() {
		return tag;
	}

	public CombatMove createMove(int rank, CombatMove move2) {
		CombatMove nuMove=move2.clone();
		move.applyModifier(nuMove, rank);
		nuMove.setName(move.getMoveName());
		return nuMove;
	}
	
}
