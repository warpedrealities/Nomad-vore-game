package worldgentools.auditing;

import java.util.ArrayList;

import artificial_intelligence.pathfinding.PathNode;
import artificial_intelligence.pathfinding.Pathfinder;
import nomad.universe.Universe;
import shared.Vec2f;
import shared.Vec2i;
import zone.Tile;
import zone.Zone;
import zone.Zone_int;
import zone.TileDef.TileMovement;

public class VoidPathfinder {

	Zone zone;
	int carvethrough;

	public VoidPathfinder(Zone zone, int carvethrough) {
		this.carvethrough = carvethrough;
		this.zone = zone;
	}

	public void run(ArrayList<Vec2i> reachable, ArrayList<Vec2i> unreachable, int replace, boolean random) {
		for (int i = 0; i < unreachable.size(); i++) {
			if (random)
			{
				random(reachable,unreachable,replace,i);
			}
			else
			{
				nonRandom(reachable,unreachable,replace,i);
			}

		}
	}

	private void random(ArrayList<Vec2i> reachable, ArrayList<Vec2i> unreachable, int replace,int index)
	{
		int r=Universe.m_random.nextInt(reachable.size());

		System.out.println("carving");
		carve(unreachable.get(index), reachable.get(r), replace);		
	}
	
	private void nonRandom(ArrayList<Vec2i> reachable, ArrayList<Vec2i> unreachable, int replace,int index)
	{
		float d = 999;
		Vec2i p = null;
		for (int j = 0; j < reachable.size(); j++) {
			float distance = reachable.get(j).getDistance(unreachable.get(index));
			if (distance < d) {
				d = distance;
				p = reachable.get(j);
			}
		}		
		System.out.println("carving");
		carve(unreachable.get(index), p, replace);
	}
		
	public void carve(Vec2i source, Vec2i destination, int replace) {
		PathNode[] path = new Pathfinder(zone).genPath(source, destination, 32, false);
		if (path != null) {
			return;
		}

		path = new Pathfinder_void(zone, carvethrough).genPath(new Vec2f(source.x, source.y),
				new Vec2f(destination.x, destination.y), 64, false);
		if (path != null) {
			
			for (int i = 0; i < path.length; i++) {
				Tile t = zone.getTile((int) path[i].m_position.x, (int) path[i].m_position.y);
				if (t != null) {
					System.out.println("carving through tile "+path[i].m_position.x+" "+path[i].m_position.y);
					
					if (t.getDefinition().getID() == carvethrough) {
						zone.getTiles()[(int) path[i].m_position.x][(int) path[i].m_position.y] = new Tile(
								(int) path[i].m_position.x, (int) path[i].m_position.y,
								zone.getZoneTileLibrary().getDef(replace), zone, zone.getZoneTileLibrary());
					}
				}
				else
				{
					System.out.println("placing tile "+path[i].m_position.x+" "+path[i].m_position.y);
					zone.getTiles()[(int) path[i].m_position.x][(int) path[i].m_position.y] = new Tile(
							(int) path[i].m_position.x, (int) path[i].m_position.y,
							zone.getZoneTileLibrary().getDef(replace), zone, zone.getZoneTileLibrary());					
				}
			}

		}
		else
		{
			System.out.println("no path found");
		}
	}
}
