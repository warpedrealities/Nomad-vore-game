package combat.effect;

import actor.Actor;

public abstract class Effect {

	public abstract int applyEffect(Actor origin, Actor target, boolean critical);

	@Override
	public abstract Effect clone();

	public abstract void applyChange(Effect effect, int rank, boolean proportionate);

	public boolean harmless() { return false;}
}
