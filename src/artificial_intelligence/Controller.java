package artificial_intelligence;

import actor.npc.NPC;
import artificial_intelligence.detection.Sense;

public interface Controller {

	public void RunAI(NPC controllable, Sense senses);

}
