package worldgentools;

import org.w3c.dom.Element;

import nomad.Universe;
import worldgentools.nodeMapGenerator.NodeMapGenerator;
import zone.Zone;

public class CircleTool {

	Zone zone;
	int minCount,maxCount;
	int minSize,maxSize;
	
	boolean [][] grid;
	
	public CircleTool(Element enode) {
		minCount=Integer.parseInt(enode.getAttribute("minCircles"));
		maxCount=Integer.parseInt(enode.getAttribute("maxCircles"));
		minSize=Integer.parseInt(enode.getAttribute("min"));
		maxSize=Integer.parseInt(enode.getAttribute("max"));
	}
	
	private void  GenOverlay(Zone zone) {
		boolean[][] grid = new boolean[zone.getWidth()][];
		for (int i = 0; i < zone.getWidth(); i++) {
			grid[i] = new boolean[zone.getHeight()];
			for (int j = 0; j < zone.getHeight(); j++) {
				grid[i][j] = false;
			}
		}
		this.grid=grid;
	}
	
	public CircleTool run(Zone m_zone, boolean[][] grid) {
		this.zone=m_zone;
		GenOverlay(m_zone);
		int count=minCount;
		if (maxCount>minCount)
		{
			count+=Universe.m_random.nextInt(maxCount-minCount);
		}
		for (int i=0;i<count;)
		{
			int x=Universe.m_random.nextInt(zone.getWidth());
			int y=Universe.m_random.nextInt(zone.getHeight());
			
			if (grid[x][y] && m_zone.getTile(x, y)==null){
				buildTree(x,y);
				i++;
			}
		}
		return this;
	}
	
	public void addToGrid(int x, int y)
	{
		if (x>=0 && x<zone.getWidth())
		{
			if (y>=0 && y<zone.getHeight())
			{
				this.grid[x][y]=true;
			}
		}
	}
	
	public void buildTree(int x, int y)
	{
		int size=minSize+Universe.m_random.nextInt(maxSize-minSize);
		this.grid[x][y]=true;
		if (size>=2)
		{
			addToGrid(x-1,y);
			addToGrid(x+1,y);
			addToGrid(x,y-1);
			addToGrid(x,y+1);	
		}
		if (size>=3)
		{
			addToGrid(x-1,y-1);
			addToGrid(x-1,y+1);
			addToGrid(x+1,y-1);
			addToGrid(x+1,y+1);	
		}
		if (size>=4)
		{
			addToGrid(x-2,y);
			addToGrid(x+2,y);
			addToGrid(x,y-2);
			addToGrid(x,y+2);	
		}		
	}

	public boolean[][] getGrid() {
		return grid;
	}

}
