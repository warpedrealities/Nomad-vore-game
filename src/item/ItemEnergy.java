package item;

import org.w3c.dom.Element;

public class ItemEnergy {
	int m_maxenergy;
	String m_refill;
	float m_refillrate;

	public ItemEnergy(Element element) {
		if (element.getAttribute("refill") != "") {
			m_refillrate = Float.parseFloat(element.getAttribute("efficiency"));
			m_refill = element.getAttribute("refill");
		}
		m_maxenergy = Integer.parseInt(element.getAttribute("capacity"));
	}

	public int getMaxEnergy() {
		return m_maxenergy;
	}

	public String getRefill() {
		return m_refill;
	}

	public float getrefillrate() {
		return m_refillrate;
	}
}
