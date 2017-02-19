package actorRPG;
import perks.Perk;
import perks.PerkElement;
import perks.PerkInstance;
import perks.PerkMove;
import perks.PerkMoveModifier;
import perks.PerkWeaponMove;

import java.util.ArrayList;

import actor.Player;
import combat.CombatMove;
import item.ItemWeapon;

public class Player_RPG_moveHandler {

	
	public class moveModifierInstance
	{
		public PerkMoveModifier modifier;
		public int rank;
		
		public moveModifierInstance(int rank, PerkMoveModifier moveModifier)
		{
			this.rank=rank;
			this.modifier=moveModifier;
		}
	}
	
	public void handlePerkBasedMoves(Player player,ArrayList<CombatMove> moves, ArrayList<PerkInstance> perks)
	{
		ArrayList<moveModifierInstance> moveModifier=new ArrayList<>();
		
		for (int i=0;i<perks.size();i++)
		{
			Perk perk=perks.get(i).getPerk();
			for (int j=0;j<perk.getNumElements();j++)
			{
				if (PerkMove.class.isInstance(perk.getElement(j)))
				{
					handlePerkBasedMove(moves,perks.get(i).getPerkRank(),(PerkMove)perk.getElement(j));
				}
				if (PerkMoveModifier.class.isInstance(perk.getElement(j)))
				{
					moveModifier.add(new moveModifierInstance(perks.get(i).getPerkRank(),
							(PerkMoveModifier)perks.get(i).getPerk().getElement(j)));
				}
				if (PerkWeaponMove.class.isInstance(perk.getElement(j)))
				{
					handleWeaponMove((PerkWeaponMove)perks.get(i).getPerk().getElement(j),player,moves,perks.get(i).getPerkRank());
				}
			}
		}
		
		if (moveModifier.size()>0)
		{
			handleMoveModifiers(moves,moveModifier);
		}
	}
	
	private void handleWeaponMove(PerkWeaponMove perk,Player player,ArrayList<CombatMove> moves, int rank)
	{
		if (player.getInventory().getSlot(0)!=null && ItemWeapon.class.isInstance(player.getInventory().getSlot(0).getItem()))
		{
			ItemWeapon weapon=(ItemWeapon)player.getInventory().getSlot(0).getItem();
			//weapon is eligible to use the move
			if (weapon.getTagSet().contains(perk.getTag()))
			{
				CombatMove move=weapon.getMove(0);
				
				moves.add(perk.createMove(rank,move));
				
			}
		}
	}
	
	private void handleMoveModifiers(ArrayList<CombatMove> moves, ArrayList<moveModifierInstance> modifiers)
	{
		ArrayList<String> nameList=new ArrayList<>();
		
		for (int i=0;i<modifiers.size();i++)
		{
			handleMoveModifier(moves,modifiers.get(i), nameList);
		}
	}
	
	private boolean checkNameList(ArrayList<String> list, String name)
	{
		for (int i=0;i<list.size();i++)
		{
			if (list.equals(name))
			{
				return true;
			}
		}
		return false;
	}
	
	private void handleMoveModifier(ArrayList<CombatMove> moves, moveModifierInstance modifier, ArrayList<String> list)
	{
		CombatMove move=pickMove(moves,modifier);
		//create modifiable move
		if (!checkNameList(list,move.getMoveName()))
		{
			moves.remove(move);
			move=move.clone();
			list.add(move.getMoveName());
		}	
		//apply modifiers over the top of the move
		
		modifier.modifier.getMove().applyModifier(move, modifier.rank);
		
		
		moves.add(move);
	}
	
	private CombatMove pickMove(ArrayList<CombatMove> moves, moveModifierInstance modifier) {
		// TODO Auto-generated method stub
		for (int i=0;i<moves.size();i++)
		{
			if (moves.get(i).getMoveName().equals(modifier.modifier.getMove().getMoveName()))
			{
				return moves.get(i);
			}
		}
		return null;
	}

	private void handlePerkBasedMove(ArrayList<CombatMove> moves, int rank, PerkMove perk)
	{
		CombatMove move=perk.getMove(rank);
		//check for identical moves
		for (int i=0;i<moves.size();i++)
		{
			if (moves.get(i).getMoveName().equals(move.getMoveName()))
			{
				moves.remove(i);
				moves.add(i, move);
				return;
			}
		}
		moves.add(move);
	}

}
