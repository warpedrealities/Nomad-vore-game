package spacetest;

import solarview.spaceEncounter.EncounterEntities.CombatWeapon;

public class CombatWeaponTest implements CombatWeapon {

	private float arc;

	public CombatWeaponTest(float arc) {
		this.arc = arc;
	}

	@Override
	public int getFacing() {

		return 0;
	}

	@Override
	public float getFiringArc() {
		return arc;
	}

}
