package nomad;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class UIDGenerator {

	int shipUID;
	int npcUID;
	public UIDGenerator()
	{
		shipUID=0;
		npcUID=0;
	}
	
	public int getShipUID()
	{
		shipUID++;
		return shipUID-1;
	}
	
	public int getnpcUID()
	{
		return npcUID++;
	}
	public void reset()
	{
		shipUID=0;
		npcUID=0;
	}
	
	public void save(DataOutputStream dstream) throws IOException
	{
		dstream.writeInt(shipUID);
		dstream.writeInt(npcUID);
	}
	
	public void load(DataInputStream dstream) throws IOException
	{
		shipUID=dstream.readInt();
		npcUID=dstream.readInt();
	}
	
}
