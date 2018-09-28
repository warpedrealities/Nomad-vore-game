package combat;

import actorRPG.Actor_RPG;

public class CombatLookup {

	public static int getDefenceForAttack(int attackAttribute) {
		switch (attackAttribute) {
		case Actor_RPG.RANGED:
			return Actor_RPG.DODGE;

		case Actor_RPG.MELEE:
			return Actor_RPG.PARRY;

		case Actor_RPG.WILLPOWER:
			return Actor_RPG.WILLPOWER;

		case Actor_RPG.SEDUCTION:
			return Actor_RPG.WILLPOWER;
		
		case Actor_RPG.PERCEPTION:
			return Actor_RPG.DODGE;
		}
		return Actor_RPG.DODGE;
	}

	public static int getBaseDefence(float distance, int defenceAttribute) {
		return 8;

	}

}
