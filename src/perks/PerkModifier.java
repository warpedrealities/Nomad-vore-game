package perks;

import actorRPG.RPG_Helper;

public class PerkModifier extends PerkElement {
	public enum PerkModType {
		BASE, ATTRIBUTE, ABILITY, STAT, SUBABILITY
	};

	private int index;
	private int value;

	private PerkModType perkType;

	public PerkModifier(String modifies, int value, PerkModType perkType) {

		this.value = value;
		this.perkType = perkType;
		this.index = indexFromString(modifies);
	}

	public int getIndex() {
		return index;
	}

	public int getValue() {
		return value;
	}

	private int indexFromString(String string) {
		switch (perkType) {
		case BASE:
			return 0;

		case ABILITY:
			return RPG_Helper.abilityFromString(string);

		case STAT:
			return RPG_Helper.statFromString(string);

		case ATTRIBUTE:
			return RPG_Helper.AttributefromString(string);

		case SUBABILITY:
			return RPG_Helper.subAttributeFromString(string);

		}
		return 0;
	}

	public PerkModType getPerkType() {
		return perkType;
	}

}
