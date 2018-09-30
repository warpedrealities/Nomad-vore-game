package perks.moveModifier;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import combat.effect.Effect;
import combat.effect.Effect_Damage;
import combat.effect.Effect_Status;

public class Effect_Change {

	public enum modifierType {
		ERROR, ADD, MOD
	};

	modifierType type;

	boolean proportionate;

	Effect effect;

	public Effect_Change(Element node) {
		type = strToType(node.getAttribute("type"));
		if (node.getAttribute("proportionate").equals("true")) {
			proportionate = true;
		}
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element enode = (Element) children.item(i);
				if (enode.getTagName().equals("effectDamage")) {
					effect = new Effect_Damage(enode);
				}
				if (enode.getTagName().equals("effectStatus")) {
					effect = new Effect_Status(enode);
				}
			}
		}
	}

	public static modifierType strToType(String type) {
		if (type.equals("ADD")) {
			return modifierType.ADD;
		}
		if (type.equals("MOD")) {
			return modifierType.MOD;
		}
		return modifierType.ERROR;
	}

	public modifierType getType() {
		return type;
	}

	public Effect getEffect() {
		return effect;
	}

	public boolean isProportionate() {
		return proportionate;
	}

}
