package worldgentools.blockdungeon;

import java.util.ArrayList;

import zone.Tile;
import zone.Zone;

import nomad.Universe;

public class BlockDungeonHelper {

	
	
	static int getBlock(ArrayList<Block> blocks, int mask, boolean exclusiveMask)
	{
		//return a block that satisfies the requirements of the mask
		
		//first assemble a sublist
		ArrayList <Integer> sublist=new ArrayList<Integer>();
		for (int i=0;i<blocks.size();i++)
		{
			if ((blocks.get(i).getEdgeValue() & mask) ==mask)
			{
				if (exclusiveMask==true && blocks.get(i).getEdgeValue()-mask==0)
				{
					sublist.add(i);
				}
				if (exclusiveMask==false)
				{
					sublist.add(i);
				}
				
			}
		}		
		int r=0;
		if (sublist.size()>1)
		{
			r=Universe.m_random.nextInt(sublist.size());
		}	
		//then roll dice		
		return sublist.get(r);
	}
	
	
	static void cleanup(Tile [][]grid, Zone zone, int wall)
	{
		for (int i=0;i<zone.getWidth();i++)
		{
			if (grid[i][0]!=null)
			{
				if (grid[i][0].getDefinition().getBlockVision()==false)
				{
					grid[i][0]=new Tile(i,0,zone.getZoneTileLibrary().getDef(wall),zone,zone.getZoneTileLibrary());
				}
			}
			if (grid[i][zone.getHeight()-1]!=null)
			{
				if (grid[i][zone.getHeight()-1].getDefinition().getBlockVision()==false)
				{
					grid[i][zone.getHeight()-1]=new Tile(i,zone.getHeight()-1,zone.getZoneTileLibrary().getDef(wall),zone,zone.getZoneTileLibrary());
				}
			}
		}
		
		for (int i=0;i<zone.getHeight();i++)
		{
			if (grid[0][i]!=null)
			{
				if (grid[0][i].getDefinition().getBlockVision()==false)
				{
					grid[0][i]=new Tile(0,i,zone.getZoneTileLibrary().getDef(wall),zone,zone.getZoneTileLibrary());
				}
			}
			if (grid[zone.getWidth()-1][i]!=null)
			{
				if (grid[zone.getWidth()-1][i].getDefinition().getBlockVision()==false)
				{
					grid[zone.getWidth()-1][i]=new Tile(zone.getWidth()-1,i,zone.getZoneTileLibrary().getDef(wall),zone,zone.getZoneTileLibrary());
				}
			}		
		}
		
		for (int i=1;i<zone.getWidth()-1;i++)
		{
			for (int j=1;j<zone.getHeight()-1;j++)
			{
				if (grid[i][j]==null)
				{
					if (adjacentTransparent(grid,i,j))
					{
						grid[i][j]=new Tile(i,j,zone.getZoneTileLibrary().getDef(wall),zone,zone.getZoneTileLibrary());
						
					}
				}
			}
		}		
	}
	
	static boolean adjacentTransparent(Tile [][]grid, int x, int y)
	{
		if (grid[x][y+1]!=null)
		{
			if (grid[x][y+1].getDefinition().getBlockVision()==false)
			{
				return true;
			}
		}
		if (grid[x+1][y]!=null)
		{
			if (grid[x+1][y].getDefinition().getBlockVision()==false)
			{
				return true;
			}		
		}
		if (grid[x][y-1]!=null)
		{
			if (grid[x][y-1].getDefinition().getBlockVision()==false)
			{
				return true;
			}
		}	
		
		if (grid[x-1][y]!=null)
		{
			if (grid[x-1][y].getDefinition().getBlockVision()==false)
			{
				return true;
			}
		}
		return false;
	}
	
	public static boolean inGrid(int [][] grid, int x, int y)
	{
		if (x>0 && x<grid.length)
		{
			if (y>0 && y<grid[0].length)
			{
				return true;
			}
			
		}
		return false;
	}
	
	public static boolean checkDR(int [][] grid, int x, int y, int width, int height)
	{
		for (int i=0;i<width;i++)
		{
			for (int j=0;j<height;j++)
			{
				if (!inGrid(grid,x+i,y+j))
				{
					return false;
				}
				if (grid[x+i][y+j]!=0)
				{
					return false;
				}
			}
		}
		return true;
	}
	public static boolean checkUR(int [][] grid, int x, int y, int width, int height)
	{
		for (int i=0;i<width;i++)
		{
			for (int j=0;j<height;j++)
			{
				if (!inGrid(grid,x+i,y-j))
				{
					return false;
				}
				if (grid[x+i][y-j]!=0)
				{
					return false;
				}
			}
		}
		return true;
	}
	public static boolean checkDL(int [][] grid, int x, int y, int width, int height)
	{
		for (int i=0;i<width;i++)
		{
			for (int j=0;j<height;j++)
			{
				if (!inGrid(grid,x-i,y+j))
				{
					return false;
				}
				if (grid[x-i][y+j]!=0)
				{
					return false;
				}
			}
		}
		return true;
	}
	public static boolean checkUL(int [][] grid, int x, int y, int width, int height)
	{
		for (int i=0;i<width;i++)
		{
			for (int j=0;j<height;j++)
			{
				if (!inGrid(grid,x-i,y+j))
				{
					return false;
				}
				if (grid[x-i][y+j]!=0)
				{
					return false;
				}
			}
		}
		return true;
	}
}
