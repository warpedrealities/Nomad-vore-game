package worldgentools.nodeMapGenerator;

import shared.Geometry;
import shared.Vec2i;

public class CellularAutomata {

	static private boolean [][] genGrid(int width, int height)
	{
		boolean [][] grid=new boolean[width][];
		for (int i=0;i<width;i++)
		{
			grid[i]=new boolean[width];
			for (int j=0;j<height;j++)
			{
				grid[i][j]=false;
			}
		}
		return grid;
	}
		
	public static boolean testAdjacent(int x, int y, boolean [][] grid)
	{
		if (x<0||x>=grid.length)
		{
			return false;
		}
		if (y<0||y>=grid[0].length)
		{
			return false;
		}
		if (grid[x][y]==true)
		{
			return true;
		}
		return false;
	}
	public static int testCell(int x, int y,boolean [][] grid)
	{
		int count=0;
		Vec2i core=new Vec2i(x,y);
		for (int i=0;i<7;i+=2)
		{
			Vec2i p=Geometry.getPos(i, core);
			if (testAdjacent(p.x,p.y,grid))
			{
				count++;
			}
		}
		return count;
	}
	
	public static boolean [][] run(boolean [][] grid)
	{
		boolean [][] nuGrid=genGrid(grid.length,grid[0].length);
		
		for (int i=0;i<grid.length;i++)
		{
			for (int j=0;j<grid[0].length;j++)
			{
				int c=testCell(i,j,grid);
				if (grid[i][j]==true)
				{
					if (c<2)
					{
						nuGrid[i][j]=false;
					}
					if (c>=2 && c<=3)
					{
						nuGrid[i][j]=true;
					}
					if (c>=4)
					{
						nuGrid[i][j]=false;
					}
				}
				else
				{
					if (c==3)
					{
						nuGrid[i][j]=true;
					}
					else
					{
						nuGrid[i][j]=false;
					}
				}
			}
		}
		return nuGrid;
	}

	public static boolean[][] clean(boolean[][] grid) {
		boolean [][] nuGrid=genGrid(grid.length,grid[0].length);
		
		for (int i=0;i<grid.length;i++)
		{
			for (int j=0;j<grid[0].length;j++)
			{
				int c=testCell(i,j,grid);
				if (grid[i][j]==true)
				{
					if (c<2)
					{
						nuGrid[i][j]=false;
					}
					if (c>=2)
					{
						nuGrid[i][j]=true;
					}
				}
				else
				{
					nuGrid[i][j]=false;
				}
			}
		}
		return nuGrid;
	}
}
