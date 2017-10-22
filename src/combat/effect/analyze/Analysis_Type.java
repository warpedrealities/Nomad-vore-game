package combat.effect.analyze;

import actor.Actor;

public interface Analysis_Type {

	
	
	int getAttribute();
	
	boolean analyze(Actor target, int attributeValue);
	
}
