package widgets.spawner;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;

import shared.ParserHelper;

public class Spawn_Data {

	private String filename;
	private int minCount;
	private int maxCount;
	private int chance;
	
	public Spawn_Data(Element element)
	{
		filename=element.getAttribute("npc");
		minCount=Integer.parseInt(element.getAttribute("min"));
		maxCount=Integer.parseInt(element.getAttribute("max"));
		chance=Integer.parseInt(element.getAttribute("chance"));
	}
	
	public Spawn_Data()
	{
		
	}
	
	public void save(DataOutputStream dstream) throws IOException
	{
		ParserHelper.SaveString(dstream, filename);
		dstream.writeInt(minCount);
		dstream.writeInt(maxCount);
		dstream.writeInt(chance);
	}
	
	public void load(DataInputStream dstream) throws IOException
	{
		filename=ParserHelper.LoadString(dstream);
		minCount=dstream.readInt();
		maxCount=dstream.readInt();
		chance=dstream.readInt();
	}

	public String getFilename() {
		return filename;
	}

	public int getMinCount() {
		return minCount;
	}

	public int getMaxCount() {
		return maxCount;
	}

	public int getChance() {
		return chance;
	}
	
	
}
