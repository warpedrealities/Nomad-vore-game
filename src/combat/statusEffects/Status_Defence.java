package combat.statusEffects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actorRPG.Actor_RPG;
import actorRPG.RPG_Helper;
import shared.ParserHelper;
import view.ViewScene;

public class Status_Defence implements StatusEffect {

	int uid;
	int spriteIcon;
	int duration;
	int strength;

	String removeText;
	AttribMod[] modifiers;

	public Status_Defence() {

	}

	public Status_Defence(Element e) {
		// TODO Auto-generated constructor stub
		uid = Integer.parseInt(e.getAttribute("uid"));
		spriteIcon = Integer.parseInt(e.getAttribute("icon"));
		if (e.getAttribute("duration").length() > 0) {
			duration = Integer.parseInt(e.getAttribute("duration"));
		}
		int count = Integer.parseInt(e.getAttribute("numModifiers"));
		modifiers = new AttribMod[count];
		NodeList children = e.getChildNodes();
		int index = 0;
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) node;
				if (Enode.getTagName() == "removeText") {
					removeText = Enode.getTextContent();
				}
				if (Enode.getTagName() == "effect") {
					modifiers[index] = new AttribMod();
					modifiers[index].attribute = RPG_Helper.AttributefromString(Enode.getAttribute("attribute"));
					modifiers[index].modifier = Integer.parseInt(Enode.getAttribute("modifier"));
					index++;
				}
				if (Enode.getTagName() == "strength") {
					strength = Integer.parseInt(Enode.getAttribute("value"));
				}
			}
		}
	}

	public StatusEffect cloneEffect() {
		Status_Defence status = new Status_Defence();
		status.duration = this.duration;
		status.removeText = this.removeText;
		status.spriteIcon = this.spriteIcon;
		status.uid = this.uid;
		status.strength = strength;
		status.modifiers = new AttribMod[modifiers.length];
		for (int i = 0; i < modifiers.length; i++) {
			status.modifiers[i] = modifiers[i];
		}
		return status;

	}

	@Override
	public void load(DataInputStream dstream) throws IOException {
		uid = dstream.readInt();
		spriteIcon = dstream.readInt();
		duration = dstream.readInt();
		strength = dstream.readInt();
		int c = dstream.readInt();
		modifiers = new AttribMod[c];
		for (int i = 0; i < c; i++) {
			modifiers[i] = new AttribMod();
			modifiers[i].load(dstream);
		}
		removeText = ParserHelper.LoadString(dstream);
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		dstream.writeInt(5);
		dstream.writeInt(uid);
		dstream.writeInt(spriteIcon);
		dstream.writeInt(duration);
		dstream.writeInt(strength);
		dstream.writeInt(modifiers.length);
		for (int i = 0; i < modifiers.length; i++) {
			modifiers[i].save(dstream);
		}
		ParserHelper.SaveString(dstream, removeText);

	}

	@Override
	public void apply(Actor_RPG subject) {

	}

	@Override
	public void update(Actor_RPG subject) {

	}

	@Override
	public void remove(Actor_RPG subject,boolean suppressMessages) {

		if (ViewScene.m_interface != null && !suppressMessages) {
			ViewScene.m_interface.DrawText(removeText.replace("TARGET", subject.getName()));
		}

	}

	@Override
	public boolean maintain() {
		duration--;
		if (duration < 1 || strength < 0) {
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 3;
		result = prime * result + uid;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Status_Defence other = (Status_Defence) obj;
		if (uid != other.uid)
			return false;
		return true;
	}

	@Override
	public int getStatusIcon() {

		return spriteIcon;
	}

	@Override
	public int getUID() {
		return uid;
	}

	public int runDefence(int damage, int damageType) {
		for (int i = 0; i < modifiers.length; i++) {
			if (modifiers[i].attribute == damageType) {
				int s = 0;
				if (damage >= modifiers[i].modifier) {
					s = modifiers[i].modifier;
				} else {
					s = damage;
				}
				damage -= modifiers[i].modifier;
				strength -= s;
				return damage;
			}
		}
		return damage;
	}

}
