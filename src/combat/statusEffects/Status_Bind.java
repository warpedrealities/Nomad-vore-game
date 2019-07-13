package combat.statusEffects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actor.Actor;
import actorRPG.Actor_RPG;
import nomad.universe.Universe;
import shared.ParserHelper;
import view.ViewScene;

public class Status_Bind extends Status_AttribMod {

	Actor origin;
	boolean originDependent;
	int strength;
	String[] struggleTexts;
	int accumulation;

	public Status_Bind(Element e) {
		super(e);

		NodeList children = e.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) node;
				if (Enode.getTagName() == "bind") {
					if (Enode.getAttribute("originDependent").length() > 0) {
						if (Enode.getAttribute("originDependent").equals("true")) {
							originDependent = true;
						}
					}
					strength = Integer.parseInt(Enode.getAttribute("strength"));
				}
				if (Enode.getTagName() == "struggle") {
					genStruggles(Enode);
				}
			}
		}

	}

	private void genStruggles(Element enode) {
		struggleTexts = new String[Integer.parseInt(enode.getAttribute("count"))];
		int index = 0;
		NodeList children = enode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) node;
				if (Enode.getTagName() == "text") {
					struggleTexts[index] = Enode.getTextContent();
					index++;
				}
			}
		}
	}

	public Actor getOrigin() {
		return origin;
	}

	@Override
	public void setOrigin(Actor origin) {
		this.origin = origin;
	}

	public boolean isOriginDependent() {
		return originDependent;
	}

	public void setOriginDependent(boolean originDependent) {
		this.originDependent = originDependent;
	}

	public Status_Bind() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public StatusEffect cloneEffect() {
		Status_Bind status = new Status_Bind();
		status.duration = this.duration;
		status.removeText = this.removeText;
		status.spriteIcon = this.spriteIcon;
		status.uid = this.uid;
		status.modifiers = new AttribMod[modifiers.length];
		for (int i = 0; i < modifiers.length; i++) {
			status.modifiers[i] = modifiers[i];
		}
		status.strength = this.strength;
		status.originDependent = this.originDependent;
		status.struggleTexts = this.struggleTexts;

		return status;

	}

	@Override
	public void load(DataInputStream dstream) throws IOException {
		uid = dstream.readInt();
		spriteIcon = dstream.readInt();
		duration = dstream.readInt();
		int c = dstream.readInt();
		modifiers = new AttribMod[c];
		for (int i = 0; i < c; i++) {
			modifiers[i] = new AttribMod();
			modifiers[i].load(dstream);
		}
		removeText = ParserHelper.LoadString(dstream);
		originDependent = dstream.readBoolean();
		strength = dstream.readInt();
		c = dstream.readInt();
		struggleTexts = new String[c];
		for (int i = 0; i < c; i++) {
			struggleTexts[i] = ParserHelper.LoadString(dstream);
		}
		boolean b = dstream.readBoolean();
		if (b == true) {
			String originName = ParserHelper.LoadString(dstream);
			int x = dstream.readInt();
			int y = dstream.readInt();
			Actor actor = Universe.getInstance().getCurrentZone().getActor(x, y);
			if (actor != null && actor.getName().equals(originName)) {
				origin = actor;
			}
		}
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		dstream.writeInt(2);
		dstream.writeInt(uid);
		dstream.writeInt(spriteIcon);
		dstream.writeInt(duration);
		dstream.writeInt(modifiers.length);
		for (int i = 0; i < modifiers.length; i++) {
			modifiers[i].save(dstream);
		}
		ParserHelper.SaveString(dstream, removeText);
		dstream.writeBoolean(originDependent);
		dstream.writeInt(strength);

		dstream.writeInt(struggleTexts.length);
		for (int i = 0; i < struggleTexts.length; i++) {
			ParserHelper.SaveString(dstream, struggleTexts[i]);
		}
		if (origin != null) {
			dstream.writeBoolean(true);
			ParserHelper.SaveString(dstream, origin.getName());
			int x = (int) origin.getPosition().x;
			int y = (int) origin.getPosition().y;
			dstream.writeInt(x);
			dstream.writeInt(y);
		} else {
			dstream.writeBoolean(false);
		}
	}

	@Override
	public void apply(Actor_RPG subject) {

		for (int i = 0; i < modifiers.length; i++) {
			subject.modAttribute(modifiers[i].attribute, modifiers[i].modifier);
		}
	}

	@Override
	public void update(Actor_RPG subject) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(Actor_RPG subject,boolean suppressMessage) {
		for (int i = 0; i < modifiers.length; i++) {
			subject.modAttribute(modifiers[i].attribute, modifiers[i].modifier * -1);
		}
		if (origin!=null)
		{
			origin.addBusy(2);
		}
		if (ViewScene.m_interface!=null && !suppressMessage)
		{
			ViewScene.m_interface.DrawText(removeText.replace("TARGET", subject.getName()));
		}

		subject.setBindState(-1);
	}

	@Override
	public boolean maintain() {
		if (originDependent) {
			if (origin != null) {
				if (origin.getRPG().getStat(Actor_RPG.HEALTH) <= 0 || origin.getRPG().getStat(Actor_RPG.RESOLVE) <= 0) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean struggle(int roll, String name) {

		if (originDependent) {
			if (origin != null) {
				if (origin.getRPG().getStat(Actor_RPG.HEALTH) <= 0 || origin.getRPG().getStat(Actor_RPG.RESOLVE) <= 0) {
					return true;
				}
			} else {
				return true;
			}
		}
		if (roll + accumulation > strength) {
			return true;
		} else {
			accumulation++;
			int r = 0;
			if (struggleTexts.length > 1) {
				r = Universe.m_random.nextInt(struggleTexts.length);
			}
			ViewScene.m_interface.DrawText(struggleTexts[r].replace("TARGET", name));
		}
		return false;
	}

	@Override
	public void linkActors(ArrayList<Actor> actors) {

	}
}
