package solarview.spaceEncounter;

import solarview.spaceEncounter.EncounterEntities.EncounterShip;

public class EncounterLogic {

	private EncounterShip [] shipList;
	
	public EncounterLogic(EncounterShip[] ships) {
		shipList=ships;
	}

	public EncounterShip[] getShipList() {
		return shipList;
	}

}
