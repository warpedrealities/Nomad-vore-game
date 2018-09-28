package actor.npc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import shared.Vec2f;

public class Temp_NPC extends NPC {

	private int lifespan;
	
	public Temp_NPC()
	{
		super();
	}
	
	public Temp_NPC(Element node, Vec2f p, String filename) {
		super(node,p,filename);
		NodeList children = node.getElementsByTagName("volatility");
		Element e=(Element)children.item(0);
		lifespan=Integer.parseInt(e.getAttribute("value"));
	}
	
	@Override
	public void Update() {
		lifespan--;
		if (lifespan<=0)
		{
			Remove(false,false);
		}
		super.Update();
	}
	
	@Override
	public boolean Respawn(long time)
	{
		return true;
	}
	
	@Override
	public void Save(DataOutputStream dstream) throws IOException {
		dstream.write(2);
		dstream.writeInt(lifespan);
		saveRoutine(dstream);
	}	
	
	@Override
	public void Load(DataInputStream dstream) throws IOException {
		
		lifespan=dstream.readInt();
		super.Load(dstream);
	}
}
