package spaceship.stats;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import nomad.Universe;
import shared.Vec2i;
import shipsystem.ShipResource;
import spaceship.Spaceship;
import spaceship.SpaceshipResource;

public class WarpHandler {
	
	private long initializationTime;
	private long flightDuration;
	private float chargeLevel;
	private float stress;
	private Vec2i destination;

	public WarpHandler()
	{
		
	}
	public WarpHandler(Vec2i destination, float stress) {
		this.destination=destination;
		this.stress=stress;

		
	}

	public float getCharge()
	{
		return Math.round(chargeLevel);
	}
	
	private boolean chargeUp(Spaceship ship)
	{
		SpaceshipResource resource=ship.getShipStats().getResource("ENERGY");
		if (resource==null)
		{
			return false;
		}
		
		if (resource.getResourceAmount()<=0.5F)
		{
			return false;
		}
		
		resource.setResourceAmount(resource.getResourceAmount()-0.5F);
		
		chargeLevel+=2.5F/stress;
		
		return true;
	}
	
	public boolean update(Spaceship ship)
	{
		//check we have enough energy to keep powering up the drive
		if (initializationTime==0 && chargeLevel<100)
		{
			if (!chargeUp(ship))
			{
				return false;
			}
			else 
			{
				if (chargeLevel>=100)
				{
					initializationTime=Universe.getClock();
					flightDuration=6000/ship.getShipStats().getFTL();
					float navBonus=((float)ship.getShipStats().getCrewStats().getNavigation())*0.05F;
					flightDuration=(long) (flightDuration*(1-navBonus));
					return true;
				}
			}
		}
		return true;
	}

	public boolean flightElapsed() {
		if (initializationTime+flightDuration<Universe.getClock())
		{
			return true;
		}
		return false;
	}

	public Vec2i getDestination() {
		return destination;
	}

	public long getTimeLeft() {
		return (initializationTime+flightDuration)-Universe.getClock();
	}

	public void save(DataOutputStream dstream) throws IOException
	{
		// TODO Auto-generated method stub
		dstream.writeLong(initializationTime);
		dstream.writeLong(flightDuration);
		dstream.writeFloat(chargeLevel);
		dstream.writeFloat(stress);
		if (destination!=null)
		{
			dstream.writeBoolean(true);
			dstream.writeInt(destination.x);
			dstream.writeInt(destination.y);
		}
		else
		{
			dstream.writeBoolean(false);
		}

	}

	public void load(DataInputStream dstream) throws IOException
	{
		initializationTime=dstream.readLong();
		flightDuration=dstream.readLong();
		chargeLevel=dstream.readFloat();
		stress=dstream.readFloat();
		if (dstream.readBoolean())
		{
			destination=new Vec2i(dstream.readInt(),dstream.readInt());
		}
	}
}
