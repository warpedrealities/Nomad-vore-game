package perks;

import javax.xml.soap.Node;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import actorRPG.RPG_Helper;

public class PerkReactive extends PerkElement {

	public enum CharacterStat {
		S_NONE(-1), S_HEALTH(0), S_RESOLVE(1), S_ACTION(3), S_SATIATION(2);

		private int value;

		CharacterStat(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	};

	private CharacterStat statTrigger;
	private boolean negativeTrigger;
	private CharacterStat statEffect;
	private float triggerMod;
	private float effectMod;

	public PerkReactive(Element enode) {
		NodeList children = enode.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) children.item(i);

				if (e.getTagName().equals("trigger")) {
					statTrigger = RPG_Helper.getCharacterStatFromString(e.getAttribute("stat"));
					if (e.getAttribute("negative").equals("true")) {
						negativeTrigger = true;
					}
					triggerMod = Float.parseFloat(e.getAttribute("modifier"));
				}
				if (e.getTagName().equals("effect")) {
					statEffect = RPG_Helper.getCharacterStatFromString(e.getAttribute("stat"));
					effectMod = Float.parseFloat(e.getAttribute("modifier"));
				}
			}
		}
	}

	public CharacterStat getStatTrigger() {
		return statTrigger;
	}

	public boolean isNegativeTrigger() {
		return negativeTrigger;
	}

	public CharacterStat getStatEffect() {
		return statEffect;
	}

	public float getTriggerMod() {
		return triggerMod;
	}

	public float getEffectMod() {
		return effectMod;
	}

}
