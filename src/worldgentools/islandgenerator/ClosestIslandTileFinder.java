package worldgentools.islandgenerator;

import shared.Vec2i;

public class ClosestIslandTileFinder {

	static public Vec2i getClosestNorth(Island island,int width, int height, Vec2i p,int maxDistance)
	{
		for (int i=height-2;i>0;i--)
		{
			for (int j=0;j<width;j++)
			{
				if (island.isInside(j, i))
				{
					if (new Vec2i(j,i).getDistance(p)<maxDistance)
					{
						return new Vec2i(j,i);
					}
				}
			}
		}
		
		return null;
	}
	static public Vec2i getClosestSouth(Island island,int width, int height, Vec2i p,int maxDistance)
	{
		for (int i=0;i<height-2;i++)
		{
			for (int j=0;j<width;j++)
			{
				if (island.isInside(j, i))
				{
					if (new Vec2i(j,i).getDistance(p)<maxDistance)
					{
						return new Vec2i(j,i);
					}
				}
			}	
			
		}
		
		return null;
	}
	static public Vec2i getClosestEast(Island island,int width, int height, Vec2i p,int maxDistance)
	{
		for (int i=width-2;i>0;i--)
		{
			for (int j=0;j<height;j++)
			{
				if (island.isInside(i,j))
				{
					if (new Vec2i(i,j).getDistance(p)<maxDistance)
					{
						return new Vec2i(i,j);
					}
				}
			}
		}
		
		return null;
	}
	static public Vec2i getClosestWest(Island island,int width, int height, Vec2i p,int maxDistance)
	{
	
		for (int i=0;i<width;i++)
		{
			for (int j=0;j<height;j++)
			{
				if (island.isInside(i,j))
				{
					if (new Vec2i(i,j).getDistance(p)<maxDistance)
					{
						return new Vec2i(i,j);
					}
				}	
			}		
		}
		return null;
	}
}
