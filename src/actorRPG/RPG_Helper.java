package actorRPG;

public class RPG_Helper {
	public static int abilityFromString(String str) {
		if (str.contains("STRENGTH")) {
			return Actor_RPG.STRENGTH;
		}
		if (str.contains("ENDURANCE")) {
			return Actor_RPG.ENDURANCE;
		}
		if (str.contains("DEXTERITY")) {
			return Actor_RPG.DEXTERITY;
		}
		if (str.contains("AGILITY")) {
			return Actor_RPG.AGILITY;
		}
		if (str.contains("INTELLIGENCE")) {
			return Actor_RPG.INTELLIGENCE;
		}
		if (str.contains("CHARM")) {
			return Actor_RPG.CHARM;
		}
		return -1;
	}

	public static int statFromString(String str) {
		if (str.contains("HEALTH")) {
			return Actor_RPG.HEALTH;
		}
		if (str.contains("RESOLVE")) {
			return Actor_RPG.RESOLVE;
		}
		if (str.contains("SATIATION")) {
			return Actor_RPG.SATIATION;
		}
		if (str.contains("ACTION")) {
			return Actor_RPG.ACTION;
		}
		return -1;

	}

	public static int AttributefromString(String str) {
		if (str.contains("PARRY")) {
			return Actor_RPG.PARRY;
		}
		if (str.contains("DODGE")) {
			return Actor_RPG.DODGE;
		}
		if (str.contains("KINETIC")) {
			return Actor_RPG.KINETIC;
		}
		if (str.contains("THERMAL")) {
			return Actor_RPG.THERMAL;
		}
		if (str.contains("SHOCK")) {
			return Actor_RPG.SHOCK;
		}
		if (str.contains("TEASE")) {
			return Actor_RPG.TEASE;
		}
		if (str.contains("PHEREMONE")) {
			return Actor_RPG.PHEREMONE;
		}
		if (str.contains("PSYCHIC")) {
			return Actor_RPG.PSYCHIC;
		}
		if (str.contains("MELEE")) {
			return Actor_RPG.MELEE;
		}
		if (str.contains("PARRY")) {
			return Actor_RPG.PARRY;
		}
		if (str.contains("RANGED")) {
			return Actor_RPG.RANGED;
		}
		if (str.contains("WILLPOWER")) {
			return Actor_RPG.WILLPOWER;
		}
		if (str.contains("SEDUCTION")) {
			return Actor_RPG.SEDUCTION;
		}
		if (str.contains("PLEASURE")) {
			return Actor_RPG.PLEASURE;
		}
		if (str.contains("STRUGGLE")) {
			return Actor_RPG.STRUGGLE;
		}
		if (str.contains("REPAIR")) {
			return Actor_RPG.TECH;
		}
		if (str.contains("CRAFTING")) {
			return Actor_RPG.TECH;
		}
		if (str.contains("WILLPOWER")) {
			return Actor_RPG.WILLPOWER;
		}
		if (str.contains("GUNNERY")) {
			return Actor_RPG.GUNNERY;
		}
		if (str.contains("NAVIGATION")) {
			return Actor_RPG.NAVIGATION;
		}
		if (str.contains("SCIENCE")) {
			return Actor_RPG.SCIENCE;
		}
		if (str.contains("SECURITY")) {
			return Actor_RPG.TECH;
		}
		if (str.contains("TECH")) {
			return Actor_RPG.TECH;
		}
		if (str.contains("PERSUADE")) {
			return Actor_RPG.PERSUADE;
		}
		if (str.contains("LEADERSHIP")) {
			return Actor_RPG.LEADERSHIP;
		}
		if (str.contains("PERCEPTION")) {
			return Actor_RPG.PERCEPTION;
		}
		return -1;
	}

	public static int subAttributeFromString(String string) {

		if (string.equals("METABOLISM")) {
			return Actor_RPG.METABOLISM;
		}
		if (string.equals("REGENTHRESHOLD")) {
			return Actor_RPG.REGENTHRESHOLD;
		}
		if (string.equals("REGENERATION")) {
			return Actor_RPG.REGENERATION;
		}
		if (string.equals("CARRY")) {
			return Actor_RPG.CARRY;
		}
		if (string.equals("MOVECOST")) {
			return Actor_RPG.MOVECOST;
		}
		if (string.equals("MOVEAPCOST")) {
			return Actor_RPG.MOVEAPCOST;
		}
		return 0;
	}
}
