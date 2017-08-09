package actor;

import java.io.DataInputStream;
import java.io.IOException;

import actor.npc.NPC;
import actor.npc.Temp_NPC;

public class ActorLoader {

	public static Actor loadActor(DataInputStream dstream) throws IOException {
		int value = dstream.read();
		switch (value) {
		case 1:
			NPC npc = new NPC();
			npc.Load(dstream);
			return npc;
		case 2:
			Temp_NPC temp=new Temp_NPC();
			temp.Load(dstream);
			return temp;
		}

		return null;
	}

}
