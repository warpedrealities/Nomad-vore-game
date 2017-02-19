package zone;

import shared.Vec2f;

public class ZoneTools {

	
	static boolean LandingLocationSizeCheck(int width ,int height, int x, int y, Zone zone)
	{
		for (int i=x;i<x+width;i++)
		{
			for (int j=y;j<y+height;j++)
			{
				if (zone.zoneTileGrid[i][j].getDefinition().getMovement()!=TileDef.TileMovement.WALK)
				{
					return false;
				}
			}
		}
		
		
		return true;
	}
	
	public static Vec2f getLandingLocation(int width, int height, Zone zone, float sw, float sh)
	{
		if (zone.getLandingSite()!=null)
		{
			Vec2f p=new Vec2f(zone.getLandingSite().x-(sw/2), zone.getLandingSite().y-(sh/2));
			if (zone.getTiles()!=null)
			{
				if (LandingLocationSizeCheck(width, height,(int)p.x,(int)p.y,zone))
				{
					return new Vec2f(zone.getLandingSite().x,zone.getLandingSite().y);
				}			
			}
			else
			{
				return new Vec2f(zone.getLandingSite().x,zone.getLandingSite().y);
			}

		}
		
		for (int i=1;i<zone.zoneWidth-width;i++)
		{
			for (int j=1;j<zone.zoneHeight-height;j++)
			{
				if (zone.zoneTileGrid[i][j].getDefinition().getMovement()==TileDef.TileMovement.WALK)
				{
					//check to see if its wide enough
					if (LandingLocationSizeCheck(width, height, i, j, zone))
					{
						return new Vec2f(i,j);
					}
					
				}
			}
		}
		return null;
	}
}
