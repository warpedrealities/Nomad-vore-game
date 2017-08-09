package solarview.spaceEncounter.EncounterEntities;

public class CombatAction {

	private CombatWeapon weapon;
	private EncounterShip target;
	private int weaponIndex;
	
	public CombatAction(CombatWeapon weapon,EncounterShip target, int weaponIndex)
	{
		this.weapon=weapon;
		this.target=target;
		this.weaponIndex=weaponIndex;
	}
	
	public CombatWeapon getWeapon() {
		return weapon;
	}
	public EncounterShip getTarget() {
		return target;
	}
	public int getWeaponIndex() {
		return weaponIndex;
	}
	
	
	
	
}
