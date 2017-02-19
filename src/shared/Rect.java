package shared;

public class Rect {

	public Vec2f m_corner;
	public Vec2f m_size;
	
	public Rect(Vec2f corner, Vec2f size)
	{
		m_size=size;
		m_corner=corner;
	}
	
	public boolean Contains(Vec2f p)
	{
		if (p.x>m_corner.x && p.y>m_corner.y)
		{
			if (p.x<m_corner.x+m_size.x &&
				p.y<m_corner.y+m_size.y)
			{
				return true;
			}	
		}
		return false;
	}
	
	public boolean Intersects(Rect rect)
	{
		if (Contains(rect.m_corner))
		{
			return true;
		}
		
		if (rect.m_corner.y<m_corner.y+m_size.y && rect.m_corner.y+rect.m_size.y>m_corner.y)
		{
			if (rect.m_corner.x<m_corner.x+m_size.x && rect.m_corner.x+rect.m_size.x>m_corner.x)
			{		
				return true;
			
			}
		}
		
		return false;
	}
	
}
