package combat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import shared.ParserHelper;

public class MoveCooldown {

	int cooldown;
	int coolmax;
	int icon;
	String moveName;
	
	public MoveCooldown()
	{
		
	}
	
	public MoveCooldown(int icon, String name,int max)
	{
		cooldown=0;
		moveName=name;
		this.icon=icon;
		coolmax=max;
	}
	
	public void save (DataOutputStream dstream) throws IOException
	{
		ParserHelper.SaveString(dstream, moveName);
		dstream.writeInt(cooldown);
		dstream.writeInt(icon);
		dstream.writeInt(coolmax);
	}
	
	public void load(DataInputStream dstream) throws IOException
	{
		moveName=ParserHelper.LoadString(dstream);
		cooldown=dstream.readInt();
		icon=dstream.readInt();
		coolmax=dstream.readInt();
	}
	
	public void update(int time)
	{
		if (cooldown>0)
		{
			cooldown-=time;
			if (cooldown<0)
			{
				cooldown=0;
			}
		}
	}

	public int getCooldown() {
		return cooldown;
	}

	public int getIcon() {
		return icon;
	}

	public String getMoveName() {
		return moveName;
	}

	public int getCoolmax() {
		return coolmax;
	}

	public void use()
	{
		cooldown=coolmax;
	}
}
