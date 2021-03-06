package solarview.spaceEncounter.EncounterEntities;

import spaceship.stats.SpaceshipWeapon;

public class CombatWeaponImpl implements CombatWeapon {

	private SpaceshipWeapon weapon;
	private int cooldown;

	public CombatWeaponImpl(SpaceshipWeapon weapon) {
		this.weapon = weapon;
		cooldown = 0;
	}

	public void decrementCooldown() {
		if (cooldown > 0) {
			cooldown--;
		}
	}

	public SpaceshipWeapon getWeapon() {
		return weapon;
	}

	public void useWeapon() {
		cooldown = this.weapon.getWeapon().getCooldown();
	}

	public int getCooldown() {
		return cooldown;
	}

	public boolean isReady() {
		if (cooldown == 0) {
			return true;
		}
		return false;
	}

	@Override
	public float getFiringArc() {
		return weapon.getWeapon().getFiringArc();
	}

	@Override
	public int getFacing() {

		return weapon.getFacing();
	}

}
