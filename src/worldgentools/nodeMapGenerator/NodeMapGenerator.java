package worldgentools.nodeMapGenerator;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import nomad.Universe;
import shared.Geometry;
import shared.Vec2i;
import zone.Zone;

public class NodeMapGenerator {

	private int verticalLink,horizontalLink,nodeSize,nodeCount,startX,startY;
	private Vec2i [][]nodeCores;
	private boolean [][] takenCores;
	private boolean [][]oldGrid;
	private boolean [][]newGrid;
	private List<IslandNode> nodes;
	public NodeMapGenerator(Element element)
	{
		verticalLink=Integer.parseInt(element.getAttribute("verticalLink"));	
		horizontalLink=Integer.parseInt(element.getAttribute("horizontalLink"));
		nodeSize=Integer.parseInt(element.getAttribute("nodeSize"));
		nodeCount=Integer.parseInt(element.getAttribute("nodeCount"));		
		startX=Integer.parseInt(element.getAttribute("startX"));
		startY=Integer.parseInt(element.getAttribute("startY"));
	}
	
	private void  GenOverlay(Zone zone) {
		boolean[][] grid = new boolean[zone.getWidth()][];
		for (int i = 0; i < zone.getWidth(); i++) {
			grid[i] = new boolean[zone.getHeight()];
			for (int j = 0; j < zone.getHeight(); j++) {
				grid[i][j] = false;
			}
		}
		newGrid=grid;
	}
	
	public NodeMapGenerator run(Zone zone, boolean[][] grid)
	{
		nodes=new ArrayList<IslandNode>();
		oldGrid=grid;
		GenOverlay(zone);
		//build offset node cores
		genCoreGrid(zone.getWidth(),zone.getHeight());
		//build islands
		
		//build initial island
		buildFirstNode();
		//build subsequent nodes
		while (nodeCount>nodes.size())
		{
			buildNode();
		}	
		//finalize the grid
		for (int i=0;i<nodes.size();i++)
		{
			nodes.get(i).paintIsland(newGrid);
		}
		return this;
	}

	private void buildFirstNode()
	{
		takenCores[startX][startY]=true;
		IslandNode node=new IslandNode(nodeCores[startX][startY].x,nodeCores[startX][startY].y,startX,startY);
		node.generate(nodeSize);	
		nodes.add(node);		
	}

	private void buildNode() {
		//find an open adjacent slot
		int r=Universe.m_random.nextInt(nodes.size());
		Vec2i p=findOpen(nodes.get(r).getGridPos());
		if (p!=null)
		{
			IslandNode node=new IslandNode(nodeCores[p.x][p.y].x,nodeCores[p.x][p.y].y,p.x,p.y);
			node.generate(nodeSize);	
			nodes.add(node);	
			takenCores[p.x][p.y]=true;
		}	
	}

	private Vec2i findOpen(Vec2i pos) {
		int offset=Universe.m_random.nextInt(4);
		for (int i=0;i<4;i++)
		{
			int c=offset+i;
			if (c>=4)
			{
				c=c-4;
			}
			Vec2i p=Geometry.getPos(c*2, pos.x, pos.y);
			if (p.x>=0 && p.x<nodeCores.length)
			{
				if (p.y>=0 && p.y<nodeCores[0].length)
				{
					//check it isnt a filled slot
					if (!takenCores[p.x][p.y])
					{
						return p;
					}
				}			
			}

		}
		
		return null;
	}

	private void genCoreGrid(int width, int height)
	{
		nodeCores=new Vec2i[width/8][];
		takenCores=new boolean[width/8][];
		for (int i=0;i<width/8;i++)
		{
			takenCores[i]=new boolean[height/8];
			nodeCores[i]=new Vec2i[height/8];
			for (int j=0;j<height/8;j++)
			{
				nodeCores[i][j]=genCore(i,j);
				takenCores[i][j]=false;
			}
		}		
	}
	
	private Vec2i genCore(int x, int y) {
		int x0=(x*8)+Universe.m_random.nextInt(4)+4;
		int y0=(y*8)+Universe.m_random.nextInt(4)+4;
		return new Vec2i(x0,y0);
	}
	
	public boolean [][]getGrid()
	{
		return newGrid;
	}
	
}
