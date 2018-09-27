package widgets.traps;

import java.io.DataOutputStream;
import java.io.IOException;

import actor.Actor;

public interface Trap_Effect {

	void trigger(Actor target);

	void save(DataOutputStream dstream) throws IOException;

}
