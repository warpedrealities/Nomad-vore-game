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
	private String deathScript;

	public Spawn_Data(Element element) {
		filename = element.getAttribute("npc");
		minCount = Integer.parseInt(element.getAttribute("min"));
		maxCount = Integer.parseInt(element.getAttribute("max"));
		chance = Integer.parseInt(element.getAttribute("chance"));
		if (element.getAttribute("deathScript").length()>0)
		{
			deathScript=element.getAttribute("deathScript");
		}
	}

	public Spawn_Data() {

	}

	public void save(DataOutputStream dstream) throws IOException {
		ParserHelper.SaveString(dstream, filename);
		dstream.writeInt(minCount);
		dstream.writeInt(maxCount);
		dstream.writeInt(chance);
		if (deathScript!=null)
		{
			dstream.writeBoolean(true);
			ParserHelper.SaveString(dstream, deathScript);
		}
		else
		{
			dstream.writeBoolean(false);
		}
	}

	public void load(DataInputStream dstream) throws IOException {
		filename = ParserHelper.LoadString(dstream);
		minCount = dstream.readInt();
		maxCount = dstream.readInt();
		chance = dstream.readInt();
		if (dstream.readBoolean())
		{
			deathScript=ParserHelper.LoadString(dstream);
		}
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

	public String getDeathScript() {
		return deathScript;
	}

}
