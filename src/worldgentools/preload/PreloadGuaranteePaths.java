package worldgentools.preload;

import java.util.ArrayList;

import shared.Vec2i;
import worldgentools.auditing.CarvingPathfinder;
import worldgentools.islandgenerator.ClosestIslandTileFinder;
import worldgentools.islandgenerator.Island;
import worldgentools.islandgenerator.IslandGenerator;
import zone.Zone;

public class PreloadGuaranteePaths {
	private Zone m_zone;
	
	public PreloadGuaranteePaths(Zone zone)
	{
		m_zone=zone;
	}
	
	public void run(int edge,int tile,int replace, ArrayList<Integer> list)
	{
		switch (edge)
		{
		case 0:
		runNorth(tile,replace, list);
		break;
		
		case 1:
		runEast(tile,replace, list);
		break;
		
		case 2:
		runSouth(tile,replace,list);
		break;
		
		case 3:
		runWest(tile,replace,list);
		break;
		
		}
	}
	
	private void runNorth(int tile,int replace, ArrayList<Integer> list)
	{
		//first, have 64 attempts to pick 4 tiles of the edge
		Vec2i p[]=new Vec2i[2];
		p[0]=new Vec2i(GuaranteePathsFindPosition.fromTheBottom(list),m_zone.getHeight()-1);
		p[1]=new Vec2i(GuaranteePathsFindPosition.fromTheTop(list),m_zone.getHeight()-1);
		
		//generate floodfill islands of navigable floor space
		IslandGenerator isg=new IslandGenerator(m_zone);
		isg.run();
		//pick the largest island
		Island island=isg.getLargestIsland();
		//find nearest point of that island to each of our picked tiles
		CarvingPathfinder carver=new CarvingPathfinder(m_zone,replace);
		for (int i=0;i<2;i++)
		{
			Vec2i nearest=ClosestIslandTileFinder.getClosestNorth(island, m_zone.getWidth(), m_zone.getHeight(), p[0], 16);
			if (nearest!=null)
			{
				//use carving pathfinder
				carver.carve(nearest, p[i], tile);
			}
		}
		//pathfind from there to the edges.
		
		
	}
	
	private void runEast(int tile,int replace, ArrayList<Integer> list)
	{
		//first, have 64 attempts to pick 4 tiles of the edge
		Vec2i p[]=new Vec2i[2];
		p[0]=new Vec2i(m_zone.getWidth()-1,GuaranteePathsFindPosition.fromTheBottom(list));
		p[1]=new Vec2i(m_zone.getWidth()-1,GuaranteePathsFindPosition.fromTheTop(list));	
		//generate floodfill islands of navigable floor space
		IslandGenerator isg=new IslandGenerator(m_zone);
		isg.run();
		//pick the largest island
		Island island=isg.getLargestIsland();
		//find nearest point of that island to each of our picked tiles
		CarvingPathfinder carver=new CarvingPathfinder(m_zone,replace);
		for (int i=0;i<2;i++)
		{
			Vec2i nearest=ClosestIslandTileFinder.getClosestEast(island, m_zone.getWidth(), m_zone.getHeight(), p[0], 16);
			if (nearest!=null)
			{
				//use carving pathfinder
				carver.carve(nearest, p[i], tile);
			}
		}	
		//pathfind from there to the edges.
	}
	
	private void runSouth(int tile,int replace, ArrayList<Integer> list)
	{
		//first, have 64 attempts to pick 4 tiles of the edge
		Vec2i p[]=new Vec2i[2];
		p[0]=new Vec2i(GuaranteePathsFindPosition.fromTheBottom(list),0);
		p[1]=new Vec2i(GuaranteePathsFindPosition.fromTheTop(list),0);
		
		//generate floodfill islands of navigable floor space
		IslandGenerator isg=new IslandGenerator(m_zone);
		isg.run();
		//pick the largest island
		Island island=isg.getLargestIsland();
		//find nearest point of that island to each of our picked tiles
		CarvingPathfinder carver=new CarvingPathfinder(m_zone,replace);
		for (int i=0;i<2;i++)
		{
			Vec2i nearest=ClosestIslandTileFinder.getClosestSouth(island, m_zone.getWidth(), m_zone.getHeight(), p[0], 16);
			if (nearest!=null)
			{
				//use carving pathfinder
				carver.carve(nearest, p[i], tile);
			}
		}
		//pathfind from there to the edges.
	}
	
	private void runWest(int tile,int replace, ArrayList<Integer> list)
	{
		//first, have 64 attempts to pick 4 tiles of the edge
		Vec2i p[]=new Vec2i[2];
		p[0]=new Vec2i(0,GuaranteePathsFindPosition.fromTheBottom(list));
		p[1]=new Vec2i(0,GuaranteePathsFindPosition.fromTheTop(list));		
		//generate floodfill islands of navigable floor space
		IslandGenerator isg=new IslandGenerator(m_zone);
		isg.run();
		//pick the largest island
		Island island=isg.getLargestIsland();
		//find nearest point of that island to each of our picked tiles
		CarvingPathfinder carver=new CarvingPathfinder(m_zone,replace);
		for (int i=0;i<2;i++)
		{
			Vec2i nearest=ClosestIslandTileFinder.getClosestWest(island, m_zone.getWidth(), m_zone.getHeight(), p[0], 16);
			if (nearest!=null)
			{
				//use carving pathfinder
				carver.carve(nearest, p[i], tile);
			}
		}
		//pathfind from there to the edges.
	}
	
}
