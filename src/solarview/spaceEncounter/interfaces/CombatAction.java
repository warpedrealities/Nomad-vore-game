package solarview.spaceEncounter.interfaces;

import solarview.spaceEncounter.EncounterEntities.CombatWeaponImpl;

public interface CombatAction {

	String getEffectSheet();

	String getEffectScript();

	EncounterShip getTarget();

	CombatWeaponImpl getWeapon();

	int getWeaponIndex();

}
