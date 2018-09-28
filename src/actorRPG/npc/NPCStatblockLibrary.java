package actorRPG.npc;

import java.util.Iterator;
import java.util.TreeMap;

import org.w3c.dom.Element;

public class NPCStatblockLibrary {

	TreeMap<String, NPC_RPG_statblock> statBlocks;

	static private NPCStatblockLibrary singletonLibrary;

	static public NPCStatblockLibrary getInstance() {
		if (singletonLibrary == null) {
			singletonLibrary = new NPCStatblockLibrary();

		}
		return singletonLibrary;

	}

	private NPCStatblockLibrary() {
		statBlocks = new TreeMap<String, NPC_RPG_statblock>();

	}

	public NPC_RPG_statblock getStatblock(Element node, String name) {
		NPC_RPG_statblock block = statBlocks.get(name);
		if (block == null) {
			block = new NPC_RPG_statblock(node, name);
			statBlocks.put(name, block);
		}
		return block;

	}

	public NPC_RPG_statblock getStatblock(String name) {
		NPC_RPG_statblock block = statBlocks.get(name);
		if (block == null) {
			block = new NPC_RPG_statblock(name);
			statBlocks.put(name, block);
		}
		return block;
	}

	public void resetThreat()
	{
		Iterator it=statBlocks.values().iterator();
		
		while (it.hasNext())
		{
			NPC_RPG_statblock block=(NPC_RPG_statblock)it.next();
			block.resetThreat();
		}
	}
}
