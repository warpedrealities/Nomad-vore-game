package perks;

import perks.PerkModifier.PerkModType;

public class PerkProcessor {

	private int[] attributes;
	private int[] stats;
	private int[] abilities;
	private float[] subAbilities;

	public PerkProcessor(int[] attributes, int[] stats, int[] abilities, float[] subAbilities) {
		this.attributes = attributes;
		this.stats = stats;
		this.abilities = abilities;
		this.subAbilities = subAbilities;
	}

	public void processPerk(PerkInstance perk) {
		for (int i = 0; i < perk.getPerk().getNumElements(); i++) {
			if (PerkModifier.class.isInstance(perk.getPerk().getElement(i))) {
				processModifier((PerkModifier) perk.getPerk().getElement(i), perk.getPerkRank());
			}
			;
		}
	}

	public void processPerkPhaseOne(PerkInstance perk) {
		for (int i = 0; i < perk.getPerk().getNumElements(); i++) {
			if (PerkModifier.class.isInstance(perk.getPerk().getElement(i))) {
				processModifierPhaseOne((PerkModifier) perk.getPerk().getElement(i), perk.getPerkRank());
			}

		}
	}

	private void processModifierPhaseOne(PerkModifier modifier, int perkRank) {
		PerkModifier.PerkModType perkType = modifier.getPerkType();
		if (perkType == PerkModType.ABILITY) {
			abilities[modifier.getIndex()] += modifier.getValue() * perkRank;
		}
	}

	private void processModifier(PerkModifier modifier, int perkRank) {
		PerkModifier.PerkModType perkType = modifier.getPerkType();
		switch (perkType) {
		case ATTRIBUTE:
			attributes[modifier.getIndex()] += modifier.getValue() * perkRank;
			break;

		case ABILITY:

			break;

		case STAT:
			stats[modifier.getIndex()] += modifier.getValue() * perkRank;
			break;

		case SUBABILITY:
			subAbilities[modifier.getIndex()] += ((float) (modifier.getValue())) / 100;
			break;
		}
	}

}
