package zone;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import shared.ParserHelper;
import shared.Vec2f;
import spaceship.Spaceship;

public class Landing {
	
	public Vec2f landingLocation;//location within zone
	public int landingZoneX;//zone
	public int landingZoneY;
	public Spaceship landedShip;
	
	public Landing(Spaceship ship, int x, int y, Vec2f position)
	{
		landedShip=ship;
		landingZoneX=x;landingZoneY=y;
		landingLocation=position;
	}


	public Spaceship getShip()
	{
		return landedShip;
	}
	
	public Vec2f getPosition()
	{
		return landingLocation;
	}
	public int getX()
	{
		return landingZoneX;
	}
	
	public int getY()
	{
		return landingZoneY;
	}
	
	public void Save(DataOutputStream dstream) throws IOException
	{
		//save location
		landingLocation.Save(dstream);
		//save zone location
		dstream.writeInt(landingZoneX);
		dstream.writeInt(landingZoneY);
		//save ship
		landedShip.save(dstream);
	}
	
	public Landing(DataInputStream dstream) throws IOException
	{
		
		landingLocation=new Vec2f(dstream);
		landingZoneX=dstream.readInt();
		landingZoneY=dstream.readInt();
		landedShip=new Spaceship();
		int mystery=dstream.readInt();
		landedShip.load(dstream);
	}
}
