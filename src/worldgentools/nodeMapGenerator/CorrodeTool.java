package worldgentools.nodeMapGenerator;

import shared.Geometry;
import shared.Vec2i;

public class CorrodeTool {

	private boolean [][] grid;
	private int width,height;
	private void genGrid(boolean [][] extent)
	{
		width=extent.length;
		height=extent[0].length;
		grid=new boolean[extent.length][];
		for (int i=0;i<extent.length;i++)
		{
			grid[i]=new boolean[extent[0].length];
			for (int j=0;j<extent[0].length;j++)
			{
				grid[i][j]=extent[i][j];
			}
		}
	}
	
	public boolean[][] run(boolean[][] extent) {
		genGrid(extent);
		
		for (int i=0;i<extent.length;i++)
		{
			for (int j=0;j<extent.length;j++)
			{
				if (adjacent(i,j))
				{
					extent[i][j]=true;
				}
			}
		}	
		return extent;
	}
	
	public boolean adjacent(int x, int y)
	{
		for (int i=0;i<4;i++)
		{
			Vec2i p=Geometry.getPos(i*2, x, y);	
			if (p.x<width && p.x>=0 && p.y>=0 && p.y<height)
			{
				if (grid[p.x][p.y])
				{
					return true;
				}
			}
		}
		return false;
	}
}
