package artificial_intelligence;

import shared.Vec2f;

public class PathNode {
	public float m_h;
	public Vec2f m_position;
	public int m_direction, m_c;
	public PathNode m_parent;

	public PathNode(Vec2f p, int direction, int c, float h, PathNode parent) {
		m_parent = parent;
		m_h = h;
		m_c = c;
		m_direction = direction;
		m_position = p;
	}
}
