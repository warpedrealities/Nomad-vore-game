package combat.effect;

import actor.Actor;
import actor.player.Player;

public abstract class Effect {

	public abstract int applyEffect(Actor origin, Actor target, boolean critical);

	public abstract Effect clone();

	public abstract void applyChange(Effect effect);
}
