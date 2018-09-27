package attack;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actorRPG.RPG_Helper;

public class Attack {

	int m_modifier;
	float m_modstrength;
	boolean m_ranged;
	int m_bonus;
	String[] m_description;
	Damage[] m_damage;

	public Attack(Element node) {
		String numdesc = node.getAttribute("numdescriptions");
		m_description = new String[Integer.parseInt(numdesc)];
		m_damage = new Damage[Integer.parseInt(node.getAttribute("numdamagetypes"))];
		int desc = 0;
		int dmg = 0;
		NodeList children = node.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			Node N = children.item(i);
			if (N.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) N;
				if (Enode.getTagName() == "damage") {
					m_damage[dmg] = new Damage(Enode);
					dmg++;
				}
				if (Enode.getTagName() == "targeting") {
					m_bonus = Integer.parseInt(Enode.getAttribute("bonus"));
					if (Integer.parseInt(Enode.getAttribute("ranged")) > 0) {
						m_ranged = true;
					}
				}
				if (Enode.getTagName() == "bonus") {
					m_modifier = RPG_Helper.abilityFromString(Enode.getAttribute("modifier"));
					m_modstrength = Float.parseFloat(Enode.getAttribute("modstrength"));
				}
				if (Enode.getTagName() == "description") {
					m_description[desc] = Enode.getTextContent();
					desc++;
				}

			}
		}
	}

	public Attack(Damage damage, int modifier, float modstrength, boolean ranged) {
		m_damage = new Damage[1];
		m_damage[0] = damage;
		m_modifier = modifier;
		m_modstrength = modstrength;
		m_ranged = ranged;
		m_description = new String[1];
		m_description[0] = "You punch the TARGET for DAMAGE damage";
		m_bonus = 0;

	}

	public boolean getRanged() {
		return m_ranged;
	}

	public int getModifier() {
		return m_modifier;
	}

	public float getModStrength() {
		return m_modstrength;
	}

	public String getDescription(int desc) {
		return m_description[desc];
	}

	public int getNumDescription() {
		return m_description.length;
	}

	public int getBonus() {
		return m_bonus;
	}

	public int getNumDamage() {
		return m_damage.length;
	}

	public Damage getDamage(int i) {
		return m_damage[i];
	}
}
