package actor;

import combat.effect.Effect;

import shared.Vec2f;

public interface Attackable {

	public int applyEffect(Effect effect, Actor origin, boolean critical);
	public boolean getAttackable();

	public String getName();
	public Vec2f getPosition();
	public int getAttribute(int defAttribute);
}
