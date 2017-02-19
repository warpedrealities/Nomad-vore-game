package artificial_intelligence;

import shared.Vec2f;

public class Path {

	public Path m_next;
	//position
	Vec2f m_position;
	int m_index;
	//direction
	int m_direction;
	public Path(int direction, Vec2f position)
	{
		m_index=1;
		m_position=position;
		m_direction=direction;
	}
	
	public Path getNext()
	{
		return m_next;
	}
	
	public int getDirection()
	{
		return m_direction;
	}
	
	public void AddLink(Path path)
	{
		//add a link onto the end
		Path p=this;
		while (p.m_next!=null)
		{
			p.m_index++;
			p=p.m_next;
		}
		p.m_next=path;
	}
	
}
