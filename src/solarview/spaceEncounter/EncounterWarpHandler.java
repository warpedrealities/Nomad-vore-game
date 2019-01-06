package solarview.spaceEncounter;

import solarview.spaceEncounter.EncounterEntities.EncounterShipImpl;

public class EncounterWarpHandler {
	private int warpLevel;
	private EncounterShipImpl playerShip;
	private boolean charging;
	public final static int MAXIMUMCHARGE=20;
	private int jumping;
	
	public EncounterWarpHandler(EncounterShipImpl playerShip)
	{
		this.playerShip=playerShip;
		warpLevel=0;
		jumping=0;
	}
	
	public void update()
	{	
		if (charging)
		{
			if (warpLevel>MAXIMUMCHARGE)
			{
				jumping++;
				return;
			}
			if (playerShip.getCourse()!=0)
			{
				charging=false;
				warpLevel=0;
			}
			else if (playerShip.getShip().getShipStats().getResource("ENERGY")!=null && 
					playerShip.getShip().getShipStats().getResource("ENERGY").getResourceAmount()>0)
			{
				int energy=(int)playerShip.getShip().getShipStats().getResource("ENERGY").getResourceAmount();
				int ftl=playerShip.getShip().getShipStats().getFTL();
				if (energy>ftl)
				{
					energy=ftl;
				}
				
				playerShip.getShip().getShipStats().getResource("ENERGY").subtractResourceAmount(energy);
				warpLevel+=energy;
			}		
		}			
	}
	
	public int getWarpLevel()
	{
		return warpLevel;
	}
	
	public boolean isCharging()
	{
		return charging;
	}
	
	public void toggleCharging()
	{
		
		if (jumping==0)
		{
			charging=!charging;		
			if (!charging)
			{
				warpLevel=0;
			}
		}
	}

	public boolean isJumping() {
		return jumping>=1;
	}
	
}
