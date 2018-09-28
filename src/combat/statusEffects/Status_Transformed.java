package combat.statusEffects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actorRPG.Actor_RPG;
import shared.ParserHelper;

public class Status_Transformed implements StatusEffect {

	private int duration;
	private int spriteIcon;
	private int uid;
	private String removeText;
	
	public Status_Transformed()
	{
		
	}
	
	public Status_Transformed(Element e)
	{
		uid = Integer.parseInt(e.getAttribute("uid"));
		spriteIcon = Integer.parseInt(e.getAttribute("icon"));
		if (e.getAttribute("duration").length() > 0) {
			duration = Integer.parseInt(e.getAttribute("duration"));
		}
		NodeList children = e.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) node;
				if (Enode.getTagName() == "removeText") {
					removeText = Enode.getTextContent();
				}
			}
		}		
	}
	
	@Override
	public void load(DataInputStream dstream) throws IOException {
		uid = dstream.readInt();
		spriteIcon = dstream.readInt();
		duration = dstream.readInt();
		removeText = ParserHelper.LoadString(dstream);
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		dstream.writeInt(8);
		dstream.writeInt(uid);
		dstream.writeInt(spriteIcon);
		dstream.writeInt(duration);
		ParserHelper.SaveString(dstream, removeText);
	}

	@Override
	public void apply(Actor_RPG subject) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Actor_RPG subject) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(Actor_RPG subject, boolean messages) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean maintain() {
		duration--;
		if (duration < 1) {
			return true;
		}
		return false;
	}

	@Override
	public StatusEffect cloneEffect() {
		Status_Transformed status = new Status_Transformed();
		status.duration = this.duration;
		status.removeText = this.removeText;
		status.spriteIcon = this.spriteIcon;
		status.uid = this.uid;
		return status;
	}

	@Override
	public int getStatusIcon() {
		return spriteIcon;
	}

	@Override
	public int getUID() {
		return uid;
	}

}
