package combat.effect.analyze;

import actor.Actor;

public interface Analysis_Type {

	public final static int VAGUE=0;
	public final static int DETAIL=1;
	public final static int PRECISE=2;
	
	boolean analyze(Actor target, int attributeValue);
	
}
