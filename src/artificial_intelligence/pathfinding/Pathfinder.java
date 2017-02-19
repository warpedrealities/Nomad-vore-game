package artificial_intelligence.pathfinding;

import java.util.Vector;


import shared.Vec2f;
import shared.Vec2i;

import view.ZoneInteractionHandler;
import vmo.Tile_Int;
import zone.Tile;
import zone.Zone_int;


public class Pathfinder {

	protected Vector <PathNode> m_closedlist;
	protected Vector <PathNode> m_openlist;
	
	protected Zone_int m_zone;
	
	public Pathfinder(Zone_int zone)
	{
		m_zone=zone;
		m_closedlist=new Vector<PathNode>();
		m_openlist=new Vector<PathNode>();
	}
	
	protected void UpdateOpenList(PathNode p, Vec2f d, int c, boolean fly)
	{
		for (int i=0;i<8;i++)
		{
			Vec2f pos=ZoneInteractionHandler.getPos(i, p.m_position);
			if (pos.x>=0 && pos.x<m_zone.getWidth() &&
				pos.y>=0 && pos.y<m_zone.getHeight())
			{
				if (CheckTile(pos,fly))
				{
					m_openlist.add(new PathNode(pos,i, c+1, pos.getDistance(d),p));
				}		
				
				
			}
		}
	}
	
	protected boolean CheckTile(Vec2f p, boolean fly)
	{
		if (m_zone.getActor((int)p.x, (int)p.y)==null && m_zone.passable((int)p.x, (int)p.y, fly))
		{
			for (int i=0;i<m_closedlist.size();i++)
			{
				if (m_closedlist.get(i).m_position.x==p.x && m_closedlist.get(i).m_position.y==p.y)
				{
					return false;
				}
			}
			for (int i=0;i<m_openlist.size();i++)
			{
				if (m_openlist.get(i).m_position.x==p.x && m_openlist.get(i).m_position.y==p.y)
				{
					return false;
				}
			}

			
			return true;			
		}

		return false;
	}
	
	protected PathNode UpdateClosedList()
	{
		float comp=64;
		PathNode p=null;
		for (int i=0;i<m_openlist.size();i++)
		{
			if (m_openlist.get(i).m_h<comp)
			{
				comp=m_openlist.get(i).m_h;
				p=m_openlist.get(i);
				
			}
		}	
		return p;
	}
	
	public PathNode[] genPath(Vec2f o, Vec2f d, int steps, boolean fly)
	{
		m_closedlist.clear();
		m_openlist.clear();
		
		//enter the origin as the closed list
		m_closedlist.add(new PathNode(o, 0, 0, o.getDistance(d),null));
		UpdateOpenList(m_closedlist.get(0),d,0,fly);
		PathNode incr=null;
		for (int i=0;i<steps;i++)
		{
			incr=UpdateClosedList();
			if (incr!=null)
			{
				m_openlist.remove(incr);
				m_closedlist.add(incr);
				if (incr.m_h<2)
				{
					break;
				}

				UpdateOpenList(incr,d,incr.m_c,fly);
			}
			else
			{
				return null;
			}
			
		}

		PathNode nodes[]=new PathNode[incr.m_c];
		for (int i=0;i<nodes.length;i++)
		{
			nodes[i]=incr;
			incr=incr.m_parent;
		}		
		return nodes;
	
	}
	public Path findPath(Vec2f o, Vec2f d, int steps, boolean fly)
	{
		PathNode[] nodes=genPath(o,d,steps,fly);
		if (nodes!=null)
		{
			Path directions=new Path(nodes[nodes.length-1].m_direction,o);
			for (int i=nodes.length-1;i>=0;i--)
			{
				directions.AddLink(new Path(nodes[i].m_direction,nodes[i].m_position));	
			}
			return directions;
		}
		return null;
	}

	public PathNode[] genPath(Vec2i source, Vec2i destination, int steps, boolean fly) {
		
		return genPath(new Vec2f(source.x,source.y),new Vec2f(destination.x,destination.y),steps,fly);

	}
}
