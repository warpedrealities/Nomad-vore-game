package actor.player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import description.BodyPart;

public class Player_LOOK {

	Map<String,BodyPart> bodyParts;
	
	
	public void addPart(BodyPart part)
	{
		bodyParts.put(part.getPartName(), part);
	}
	
	public BodyPart getPart(String name)
	{
		return bodyParts.get(name);
	}

	public Player_LOOK() {
		bodyParts=new HashMap<String,BodyPart>();
	
	}
	
	public Player_LOOK(DataInputStream dstream) throws IOException {

		bodyParts=new HashMap<String,BodyPart>();
		int count=dstream.readInt();
		for (int i=0;i<count;i++)
		{
			BodyPart p=new BodyPart(dstream);
			bodyParts.put(p.getPartName(), p);
		}
	}

	public void save(DataOutputStream dstream) throws IOException {
	
		Collection<BodyPart>parts=bodyParts.values();
		dstream.writeInt(parts.size());
		Iterator<BodyPart> it=parts.iterator();
		
		while (it.hasNext())
		{
			BodyPart part=it.next();
			part.save(dstream);
		}
		
		
	}

	public void removeBodyPart(String bodyPart) {

		bodyParts.remove(bodyPart);
	}

	
	
}
