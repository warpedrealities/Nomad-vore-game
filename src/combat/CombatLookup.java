package combat;

import combat.CombatMove.AttackPattern;

import actorRPG.Actor_RPG;

public class CombatLookup {

	public static int getDefenceForAttack(int attackAttribute)
	{	
		switch (attackAttribute)
		{
		case Actor_RPG.RANGED:
			return Actor_RPG.DODGE;

		
		case Actor_RPG.MELEE:
			return Actor_RPG.PARRY;

		
		case Actor_RPG.SEDUCTION:
			return Actor_RPG.WILLPOWER;
		}
		return -1;
	}
	
	public static int getBaseDefence(float distance, int defenceAttribute)
	{
		return 8;

	}
	
}
