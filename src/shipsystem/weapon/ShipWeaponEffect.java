package shipsystem.weapon;

import java.io.DataOutputStream;
import java.io.IOException;

import solarview.spaceEncounter.EncounterEntities.EncounterShipImpl;
import solarview.spaceEncounter.effectHandling.EffectHandler_Interface;

public interface ShipWeaponEffect {

	public int getType();

	public void save(DataOutputStream dstream) throws IOException;

	public void apply(float distance, EncounterShipImpl ship, EffectHandler_Interface effectHandler);
}
