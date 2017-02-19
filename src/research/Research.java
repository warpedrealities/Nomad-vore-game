package research;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import shared.ParserHelper;

public class Research {

	
	private int roll;
	private int DC;
	private String name;
	private String group;
	
	public Research()
	{
		
	}
	
	public Research(int DC, int roll, String name)
	{
		this.DC=DC;
		this.roll=roll;
		this.name=name;
	}
	
	
	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public int getRoll() {
		return roll;
	}

	public int getDC() {
		return DC;
	}

	public String getName() {
		return name;
	}

	public void save(DataOutputStream dstream) throws IOException
	{
		dstream.writeInt(DC);
		dstream.writeInt(roll);
		ParserHelper.SaveString(dstream, name);
	}
	
	public void load(DataInputStream dstream) throws IOException
	{
		DC=dstream.readInt();
		roll=dstream.readInt();
		name=ParserHelper.LoadString(dstream);
	}
}
