package combat.statusEffects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import actor.Actor;
import actorRPG.Actor_RPG;

public interface StatusEffect {

	public void load(DataInputStream dstream) throws IOException;

	public void save(DataOutputStream dstream) throws IOException;

	public void apply(Actor_RPG subject);

	public void update(Actor_RPG subject);

	public void remove(Actor_RPG subject, boolean messages);

	public boolean maintain();

	public StatusEffect cloneEffect();

	public int getStatusIcon();

	public int getUID();

	default void setOrigin(Actor origin) {
	};

	default void modifyStrength(float value, boolean proportional) {
	};

	default boolean isEffective() {
		return true;
	}

	public default void linkActors(ArrayList<Actor> actors) {
	};
}
