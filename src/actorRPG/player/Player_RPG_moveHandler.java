package actorRPG.player;

import perks.Perk;
import perks.PerkElement;
import perks.PerkInstance;
import perks.PerkMove;
import perks.PerkMoveModifier;
import perks.PerkWeaponMove;

import java.util.ArrayList;

import actor.player.Player;
import combat.CombatMove;
import item.ItemWeapon;

public class Player_RPG_moveHandler {

	private int moveLists[];

	public class moveModifierInstance {
		public PerkMoveModifier modifier;
		public int rank;

		public moveModifierInstance(int rank, PerkMoveModifier moveModifier) {
			this.rank = rank;
			this.modifier = moveModifier;
		}
	}

	public void handlePerkBasedMoves(Player player, ArrayList<CombatMove> moves, int[] moveLists,
			ArrayList<PerkInstance> perks) {
		this.moveLists = moveLists;
		ArrayList<moveModifierInstance> moveModifier = new ArrayList<>();

		for (int i = 0; i < perks.size(); i++) {
			Perk perk = perks.get(i).getPerk();
			for (int j = 0; j < perk.getNumElements(); j++) {
				if (PerkMove.class.isInstance(perk.getElement(j))) {
					handlePerkBasedMove(moves, perks.get(i).getPerkRank(), (PerkMove) perk.getElement(j));
				}
				if (PerkMoveModifier.class.isInstance(perk.getElement(j))) {
					moveModifier.add(new moveModifierInstance(perks.get(i).getPerkRank(),
							(PerkMoveModifier) perks.get(i).getPerk().getElement(j)));
				}
				if (PerkWeaponMove.class.isInstance(perk.getElement(j))) {
					handleWeaponMove((PerkWeaponMove) perks.get(i).getPerk().getElement(j), player, moves,
							perks.get(i).getPerkRank());
				}
			}
		}

		if (moveModifier.size() > 0) {
			handleMoveModifiers(moves, moveModifier);
		}
	}

	private void handleWeaponMove(PerkWeaponMove perk, Player player, ArrayList<CombatMove> moves, int rank) {
		if (player.getInventory().getSlot(0) != null
				&& ItemWeapon.class.isInstance(player.getInventory().getSlot(0).getItem())) {
			ItemWeapon weapon = (ItemWeapon) player.getInventory().getSlot(0).getItem();
			// weapon is eligible to use the move
			if (weapon.getTagSet().contains(perk.getTag())) {
				CombatMove move = weapon.getMove(0);

				addMove(moves, perk.createMove(rank, move));

			}
		}
	}

	private void handleMoveModifiers(ArrayList<CombatMove> moves, ArrayList<moveModifierInstance> modifiers) {
		ArrayList<String> nameList = new ArrayList<>();

		for (int i = 0; i < modifiers.size(); i++) {
			handleMoveModifier(moves, modifiers.get(i), nameList);
		}
	}

	private boolean checkNameList(ArrayList<String> list, String name) {
		for (int i = 0; i < list.size(); i++) {
			if (list.equals(name)) {
				return true;
			}
		}
		return false;
	}

	private void handleMoveModifier(ArrayList<CombatMove> moves, moveModifierInstance modifier,
			ArrayList<String> list) {
		CombatMove move = pickMove(moves, modifier);
		// create modifiable move
		if (!checkNameList(list, move.getMoveName())) {
			int d = moves.indexOf(move);
			moves.remove(move);
			move = move.clone();
			moves.add(d, move);
			list.add(move.getMoveName());
		} else {
			addMove(moves, move);
		}
		// apply modifiers over the top of the move

		modifier.modifier.getMove().applyModifier(move, modifier.rank);

	}

	private CombatMove pickMove(ArrayList<CombatMove> moves, moveModifierInstance modifier) {
		// TODO Auto-generated method stub
		for (int i = 0; i < moves.size(); i++) {
			if (moves.get(i).getMoveName().equals(modifier.modifier.getMove().getMoveName())) {
				return moves.get(i);
			}
		}
		return null;
	}

	private void handlePerkBasedMove(ArrayList<CombatMove> moves, int rank, PerkMove perk) {
		CombatMove move = perk.getMove(rank);
		// check for identical moves
		for (int i = 0; i < moves.size(); i++) {
			if (moves.get(i).getMoveName().equals(move.getMoveName())) {
				moves.remove(i);
				moves.add(i, move);
				return;
			}
		}
		addMove(moves, move);
	}

	private int getMoveCategoryOffset(int index) {
		switch (index) {
		case 0:
			return 1;

		case 1:

			return 1 + moveLists[0];

		case 2:

			return 1 + moveLists[0] + moveLists[1];

		case 3:

			return 1 + moveLists[0] + moveLists[1] + moveLists[2];
		case 4:

			return 1 + moveLists[0] + moveLists[1] + moveLists[2]+moveLists[3];
		}
		return 0;
	}

	private void addMove(ArrayList<CombatMove> moves, CombatMove move) {
		int index = 0;
		index = getMoveCategoryOffset(move.getMoveType().getValue() + 1);

		moves.add(index, move);
		moveLists[move.getMoveType().getValue()]++;
	}

}
