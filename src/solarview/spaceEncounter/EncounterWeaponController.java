package solarview.spaceEncounter;

import solarview.spaceEncounter.EncounterEntities.CombatActionImpl;
import solarview.spaceEncounter.EncounterEntities.EncounterShipImpl;
import solarview.spaceEncounter.EncounterEntities.WeaponCheck;

public class EncounterWeaponController {

	private TargetingControls targeting;
	private EncounterShipImpl ship;
	private EncounterShipImpl [] ships;
	
	public EncounterWeaponController(EncounterShipImpl ship, EncounterShipImpl [] ships, TargetingControls targeting)
	{
		this.ships=ships;
		this.ship=ship;
		this.targeting=targeting;
	}

	/*
	 * return 0 for weapon not being usable due to cooldowns, arcs, range
	 * return 1 for weapon a weapon is added to the queue
	 * return 2 if the weapon is on the queue
	 */

	public int triggerWeapon(int i) {
		if (targeting.getIndex()==-1)
		{
			return 0;
		}
		for (int j=0;j<ship.getActions().size();j++)
		{
			if (ship.getActions().get(j).getWeaponIndex()==i)
			{
				ship.getActions().remove(j);
				return 2;
			}
		}
		
		if (!ship.getWeapons().get(i).isReady())
		{
			return 0;
		}
		
		if (!WeaponCheck.checkRange(ship, ships[targeting.getIndex()], ship.getWeapons().get(i)))
		{
			return 0;
		}
		if (!WeaponCheck.checkArc(ship, ships[targeting.getIndex()], ship.getWeapons().get(i)))
		{
			return 0;
		}		
		ship.getActions().add(new CombatActionImpl(ship.getWeapons().get(i),ships[targeting.getIndex()],i));
		return 1;
	}
	
	
	
}
