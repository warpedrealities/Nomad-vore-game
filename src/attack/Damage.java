package attack;

import org.w3c.dom.Element;

import actorRPG.RPG_Helper;

public class Damage {
	int m_type;
	int m_max;
	int m_min;

	public Damage(Element node) {
		m_type = RPG_Helper.AttributefromString(node.getAttribute("type"));
		m_max = Integer.parseInt(node.getAttribute("maximum"));
		m_min = Integer.parseInt(node.getAttribute("minimum"));
	}

	public Damage(int type, int max, int min) {
		m_type = type;
		m_max = max;
		m_min = min;
	}

	public int getType() {
		return m_type;
	}

	public int getMax() {
		return m_max;
	}

	public int getMin() {
		return m_min;
	}
}
