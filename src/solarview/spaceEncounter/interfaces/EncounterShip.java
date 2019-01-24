package solarview.spaceEncounter.interfaces;

import shared.Vec2f;
import solarview.spaceEncounter.EncounterEntities.CombatShield;
import solarview.spaceEncounter.effectHandling.EffectHandler_Interface;

public interface EncounterShip {

	Vec2f getPosition();

	void attack(float distance, CombatAction action, EffectHandler_Interface effectHandler);

	Vec2f getEmitter(int weaponIndex);

	Vec2f getLeading(float v);

	CombatShield getShield();

}
