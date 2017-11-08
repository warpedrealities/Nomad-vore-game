package combat.effect.analyze;

import actor.Actor;

public interface Analysis_Type {

	boolean analyze(Actor target, int attributeValue);
	
}
