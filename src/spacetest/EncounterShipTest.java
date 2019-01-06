package spacetest;

import shared.Vec2f;
import solarview.spaceEncounter.effectHandling.EffectHandler_Interface;
import solarview.spaceEncounter.interfaces.CombatAction;
import solarview.spaceEncounter.interfaces.EncounterShip;

public class EncounterShipTest implements EncounterShip {

	Vec2f position;

	public EncounterShipTest() {
		position = new Vec2f(0, 0);
	}

	public void setPosition(Vec2f position) {
		this.position = position;
	}

	@Override
	public Vec2f getPosition() {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public void attack(float distance, CombatAction action, EffectHandler_Interface effectHandler) {
		// TODO Auto-generated method stub

	}

	@Override
	public Vec2f getEmitter(int weaponIndex) {
		return new Vec2f(0.5F, 0.5F);
	}

	@Override
	public Vec2f getLeading(float v) {
		return position;
	}

}
