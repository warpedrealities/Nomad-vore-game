package solarview.spaceEncounter.EncounterEntities;

import solarview.spaceEncounter.interfaces.CombatAction;

public class CombatActionImpl implements CombatAction {

	private CombatWeapon weapon;
	private EncounterShipImpl target;
	private int weaponIndex;

	public CombatActionImpl(CombatWeapon weapon,EncounterShipImpl target, int weaponIndex)
	{
		this.weapon=weapon;
		this.target=target;
		this.weaponIndex=weaponIndex;
	}

	public CombatWeapon getWeapon() {
		return weapon;
	}
	public EncounterShipImpl getTarget() {
		return target;
	}
	public int getWeaponIndex() {
		return weaponIndex;
	}

	@Override
	public String getEffectSheet() {
		return weapon.getWeapon().getWeapon().getEffectSheet();
	}

	@Override
	public String getEffectScript() {
		return weapon.getWeapon().getWeapon().getEffectScript();
	}



}
