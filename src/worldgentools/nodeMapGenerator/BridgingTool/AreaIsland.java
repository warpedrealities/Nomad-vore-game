package worldgentools.nodeMapGenerator.BridgingTool;

import nomad.universe.Universe;
import shared.Geometry;
import shared.Vec2i;

public class AreaIsland {

	private boolean [][]extent;
	private int size,width,height;
	
	public AreaIsland(int width, int height)
	{
		this.width=width;
		this.height=height;
		size=0;
		extent=new boolean[width][];
		for (int i=0;i<width;i++)
		{
			extent[i]=new boolean[height];
			for (int j=0;j<height;j++)
			{
				extent[i][j]=false;
			}
		}
	}
	
	public void add(int x, int y)
	{
		extent[x][y]=true;
		size++;
	}
	
	public int getSize()
	{
		return size;
	}
	
	public boolean adjacent(int x, int y)
	{
		for (int i=0;i<4;i++)
		{
			Vec2i p=Geometry.getPos(i*2, x, y);	
			if (p.x<width && p.x>=0 && p.y>=0 && p.y<height)
			{
				if (extent[p.x][p.y])
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean contains(int x, int y)
	{
		if (extent[x][y])
		{
			return true;
		}	
		return false;
	}

	public void consume(AreaIsland island)
	{
		for (int i=0;i<width;i++)
		{
			for (int j=0;j<height;j++)
			{
				if (island.extent[i][j])
				{
					extent[i][j]=true;				
				}
			}
		}
	}
	
	public Vec2i findSpotInside()
	{
		for (int i=0;i<width;i++)
		{
			for (int j=0;j<height;j++)
			{
				if (extent[i][j] && Universe.m_random.nextBoolean())
				{
					return new Vec2i(i,j);
				}
			}
		}
		return null;
	}
	
	public void absorb(AreaIsland island) {
		for (int i=0;i<width;i++)
		{
			for (int j=0;j<height;j++)
			{
				if (extent[i][j] && island.adjacent(i, j))
				{
					consume(island);
					island.size=0;
					return;
				}
			}
		}
	}
}
