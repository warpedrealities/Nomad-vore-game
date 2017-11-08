package worldgentools.nodeMapGenerator;

import nomad.Universe;
import shared.Vec2i;
import worldgentools.nodeMapGenerator.BridgingTool.BridgingTool;

public class IslandNode {

	private Vec2i pos;
	private Vec2i gridPos;
	private boolean [][] extent;
	private boolean surrounded;
	
	public IslandNode(int x, int y, int gridX,int gridY)
	{
		surrounded=false;
		pos=new Vec2i(x,y);
		gridPos=new Vec2i(gridX,gridY);
	}
	
	private boolean [][] genGrid()
	{
		boolean [][] grid=new boolean[8][];
		for (int i=0;i<8;i++)
		{
			grid[i]=new boolean[8];
			for (int j=0;j<8;j++)
			{
				grid[i][j]=false;
			}
		}
		return grid;
	}
	
	public void genSeed(int size)
	{
		for (int i=0;i<8;i++)
		{
			for (int j=0;j<8;j++)
			{
				int chance=size;//-(int)(new Vec2i(i,j).getDistance(4,4));
				int r=Universe.m_random.nextInt(6);
				if (r<chance)
				{
					extent[i][j]=true;
				}
			}
		}
	}
	
	public void generate(int size)
	{
		extent=genGrid();		
		genSeed(size);
		for (int i=0;i<3;i++)
		{
			extent=CellularAutomata.run(extent);			
		}
		extent=CellularAutomata.clean(extent);		
		extent=new BridgingTool().run(extent);
		extent=new BridgingTool().run(extent);	
	}

	public int getX()
	{
		return pos.x;
	}
	
	public int getY()
	{
		return pos.y;
	}

	public Vec2i getPos() {
		return pos;
	}

	public Vec2i getGridPos() {
		return gridPos;
	}

	public boolean test(int x, int y) {
		if (pos.x==x && pos.y==y)
		{
			return true;
		}
		return false;
	}

	public void paintIsland(boolean[][] grid) {
		for (int i=0;i<8;i++)
		{
			for (int j=0;j<8;j++)
			{
				int x=i-4+pos.x;
				int y=j-4+pos.y;
				if (x>=0 && x<grid.length)
				{
					if (y>=0 && y<grid[0].length)
					{
						if (extent[i][j])
						{
							grid[x][y]=true;					
						}
					}
				}
			}
		}
	}

	public boolean isSurrounded() {
		return surrounded;
	}

	public void setSurrounded(boolean surrounded) {
		this.surrounded = surrounded;
	}
	
	
	
}
