package pathfinder;

import shared.Vec2f;

public class Path {
	public float m_h;
	public Vec2f m_position;
	public int m_direction, m_c;
	public Path m_parent;

	public Path(Vec2f p, int direction, int c, float h, Path parent) {
		m_parent = parent;
		m_h = h;
		m_c = c;
		m_direction = direction;
		m_position = p;
	}
}
