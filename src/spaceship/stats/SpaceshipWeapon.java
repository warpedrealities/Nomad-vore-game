package spaceship.stats;

import shipsystem.weapon.ShipWeapon;

public class SpaceshipWeapon {

	private int emitterNumber;
	private int facing;
	private ShipWeapon weapon;

	public SpaceshipWeapon(ShipWeapon shipWeapon, int emitterIndex, int facing) {
		this.weapon = shipWeapon;
		this.emitterNumber = emitterIndex;
		this.facing = facing;
	}

	public int getEmitterNumber() {
		return emitterNumber;
	}

	public int getFacing() {
		return facing;
	}

	public ShipWeapon getWeapon() {
		return weapon;
	}

}
