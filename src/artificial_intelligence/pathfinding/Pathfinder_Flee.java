package artificial_intelligence.pathfinding;

import shared.Vec2f;
import view.ZoneInteractionHandler;
import zone.Zone_int;


public class Pathfinder_Flee extends Pathfinder {

	public Pathfinder_Flee(Zone_int zone) {
		super(zone);
		// TODO Auto-generated constructor stub
	}

	private float evaluateNode(PathNode node)
	{
		if (m_zone.getTile((int)node.m_position.x,(int)node.m_position.y).getVisible()==false)
		{
			return node.m_h+2;
		}
		
		return node.m_h;
	}
	@Override
	protected PathNode UpdateClosedList()
	{
		float comp=0;
		PathNode p=null;
		for (int i=0;i<m_openlist.size();i++)
		{
			float hPlus=evaluateNode(m_openlist.get(i));		
			if (hPlus>comp)
			{
				comp=hPlus;
				p=m_openlist.get(i);
				
			}
		}	
		return p;
	}
	@Override
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
	
}
