package actor;

import java.io.DataInputStream;
import java.io.IOException;

public class ActorLoader {

	public static Actor loadActor(DataInputStream dstream) throws IOException
	{
		int value=dstream.read();
		switch (value)
		{
		case 1:
			NPC npc=new NPC();
			npc.Load(dstream);
			return npc;
			
		
		
		
		
		}
				
				
		
		
		return null;
	}
	
}
