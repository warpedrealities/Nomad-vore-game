package pathfinder;

import shared.Vec2f;

public class Path2 {
	public float m_h;
	public Vec2f m_position;
	public int m_direction, m_c;
	public Path2 m_parent;
	public Path2(Vec2f p, int direction, int c, float h, Path2 path)
	{
		m_h=h;
		m_c=c;
		m_direction=direction;
		m_position=p;
		m_parent=path;
	}
}
