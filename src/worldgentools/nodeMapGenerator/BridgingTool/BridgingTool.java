package worldgentools.nodeMapGenerator.BridgingTool;

import java.util.ArrayList;
import java.util.List;

import shared.Vec2f;
import shared.Vec2i;

public class BridgingTool {

	private List<AreaIsland> list;
	
	public BridgingTool()
	{
		list=new ArrayList<AreaIsland>();
	}
	
	public boolean[][] run(boolean[][] extent) {
		//find all islands
		int width=extent.length;
		int height=extent[0].length;
		for (int i=0;i<width;i++)
		{
			for (int j=0;j<height;j++)
			{
				if (extent[i][j])
				{
					if (!addToExtant(i,j))
					{
						AreaIsland island=new AreaIsland(width,height);
						island.add(i, j);
						list.add(island);
					}				
				}

			}
		}
		//consolidate islands
		for (int i=0;i<list.size();i++)
		{
			for (int j=0;j<list.size();j++)
			{
				if (i!=j)
				{
					list.get(i).absorb(list.get(j));
				}
			}
		}
		for (int i=list.size()-1;i>=0;i--)
		{
			if (list.get(i).getSize()==0)
			{
				list.remove(i);
			}
		}
		//connect all islands
		int largest=getLargest();
		for (int i=0;i<list.size();i++)
		{
			if (i!=largest)
			{
				for (int j=0;j<5;j++)
				{
					connect(extent,list.get(largest),list.get(i));				
				}
			}
		}
		return extent;
	}
	
	private void connect(boolean [][]extent,AreaIsland source, AreaIsland destination) {
		
		Vec2i p0=source.findSpotInside();
		Vec2i p1=destination.findSpotInside();
		if (p0!=null && p1!=null)
		{
			Vec2f p2=new Vec2f(p1.x-p0.x,p1.y-p0.y);
			p2.normalize();
			Vec2f pc=new Vec2f(p0.x,p0.y);
			int intervalCount=(int)p0.getDistance(p1);
			for (int i=0;i<intervalCount;i++)
			{
				extent[(int)pc.x][(int)pc.y]=true;
				if (pc.y<=6)
				{
					extent[(int)pc.x][(int)pc.y+1]=true;				
				}		
				pc.add(p2);
			}			
		}	
	}

	private int getLargest()
	{
		int index=0;
		int size=0;
		for (int i=0;i<list.size();i++)
		{
			if (list.get(i).getSize()>size)
			{
				index=i;
				size=list.get(i).getSize();
			}
		}
		return index;
	}
	
	private boolean addToExtant(int x, int y)
	{
		for (int i=0;i<list.size();i++)
		{
			if (list.get(i).adjacent(x, y))
			{
				list.get(i).add(x, y);
				return true;
			}
		}
		return false;
	}

}
