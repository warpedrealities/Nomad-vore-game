package spacetest;

import solarview.spaceEncounter.EncounterEntities.CombatWeapon;
import solarview.spaceEncounter.interfaces.CombatAction;
import solarview.spaceEncounter.interfaces.EncounterShip;

public class CombatActionTest implements CombatAction {

	private EncounterShip target;

	public CombatActionTest(EncounterShip target) {
		this.target = target;
	}

	@Override
	public String getEffectSheet() {
		return "turbolaser";
	}

	@Override
	public String getEffectScript() {
		return "turbolaser";
	}

	@Override
	public EncounterShip getTarget() {
		return target;
	}

	@Override
	public CombatWeapon getWeapon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getWeaponIndex() {
		return 0;
	}

}
