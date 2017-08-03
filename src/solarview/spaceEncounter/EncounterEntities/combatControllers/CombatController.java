package solarview.spaceEncounter.EncounterEntities.combatControllers;

import solarview.spaceEncounter.EncounterEntities.EncounterShip;
import solarview.spaceEncounter.effectHandling.EffectHandler;

public interface CombatController {

	void run(EncounterShip encounterShip,EncounterShip[]allShips,EffectHandler effectHandler);

}
