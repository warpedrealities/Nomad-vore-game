package actor;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actorRPG.Actor_RPG;

public class Modifier {

	int m_modifiers[];

	public Modifier(Element Mnode) {
		m_modifiers = new int[20];
		NodeList children = Mnode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) node;

				if (Enode.getTagName() == "kinetic") {
					m_modifiers[Actor_RPG.KINETIC] = Integer.parseInt(Enode.getAttribute("value"));
				}

				if (Enode.getTagName() == "thermal") {
					m_modifiers[Actor_RPG.THERMAL] = Integer.parseInt(Enode.getAttribute("value"));
				}

				if (Enode.getTagName() == "pheremone") {
					m_modifiers[Actor_RPG.PHEREMONE] = Integer.parseInt(Enode.getAttribute("value"));
				}
				if (Enode.getTagName() == "shock") {
					m_modifiers[Actor_RPG.SHOCK] = Integer.parseInt(Enode.getAttribute("value"));
				}

				if (Enode.getTagName() == "seduction") {
					m_modifiers[Actor_RPG.SEDUCTION] = Integer.parseInt(Enode.getAttribute("value"));
				}
				if (Enode.getTagName() == "parry") {
					m_modifiers[Actor_RPG.PARRY] = Integer.parseInt(Enode.getAttribute("value"));
				}
				if (Enode.getTagName() == "melee") {
					m_modifiers[Actor_RPG.MELEE] = Integer.parseInt(Enode.getAttribute("value"));
				}
				if (Enode.getTagName() == "ranged") {
					m_modifiers[Actor_RPG.RANGED] = Integer.parseInt(Enode.getAttribute("value"));
				}
				if (Enode.getTagName() == "tease") {
					m_modifiers[Actor_RPG.TEASE] = Integer.parseInt(Enode.getAttribute("value"));
				}
			}
		}
	}

	public int getModifier(int i) {
		return m_modifiers[i];
	}

	public int getNumModifiers() {

		return m_modifiers.length;
	}
}
