package solarview.spaceEncounter.EncounterEntities.combatControllers;

import solarview.spaceEncounter.EncounterEntities.EncounterShipImpl;
import solarview.spaceEncounter.effectHandling.EffectHandler;

public interface CombatController {

	void run(EncounterShipImpl encounterShip,EncounterShipImpl[]allShips,EffectHandler effectHandler);

}
