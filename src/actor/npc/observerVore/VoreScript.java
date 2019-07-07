package actor.npc.observerVore;

import java.io.DataOutputStream;
import java.io.IOException;

import actor.npc.NPC;
import zone.Zone;

public interface VoreScript {



	void update(boolean visible,boolean noEnemies);

	void attacked();

	boolean blocksAI();

	boolean isActive();

	boolean blocksConversation();

	void save(DataOutputStream dstream) throws IOException;

	void linkActors(Zone zone);

	NPC getTarget();

	void releasePrey();
}
