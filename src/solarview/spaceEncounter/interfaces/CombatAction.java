package solarview.spaceEncounter.interfaces;

import solarview.spaceEncounter.EncounterEntities.CombatWeapon;

public interface CombatAction {

	String getEffectSheet();

	String getEffectScript();

	EncounterShip getTarget();

	CombatWeapon getWeapon();

	int getWeaponIndex();

}
