package combat.statusEffects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;

import actorRPG.Actor_RPG;
import shared.ParserHelper;
import view.ViewScene;

public class Status_Stun implements StatusEffect {

	int uid;
	int duration;
	String removeText;

	public Status_Stun() {

	}

	public Status_Stun(Element element) {
		duration = Integer.parseInt(element.getAttribute("duration"));
		uid = Integer.parseInt(element.getAttribute("uid"));
		removeText = element.getTextContent();
	}

	public StatusEffect cloneEffect() {
		Status_Stun status = new Status_Stun();

		status.duration = this.duration;

		status.removeText = this.removeText;

		status.uid = this.uid;
		return status;
	}

	@Override
	public void load(DataInputStream dstream) throws IOException {
		// TODO Auto-generated method stub
		uid = dstream.readInt();
		duration = dstream.readInt();
		removeText = ParserHelper.LoadString(dstream);
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		// TODO Auto-generated method stub
		dstream.writeInt(1);
		dstream.writeInt(uid);
		dstream.writeInt(duration);

		ParserHelper.SaveString(dstream, removeText);
	}

	@Override
	public void apply(Actor_RPG subject) {

		subject.addBusy(duration);
	}

	@Override
	public void update(Actor_RPG subject) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(Actor_RPG subject, boolean suppressMessages) {
		if (removeText!=null && removeText.length()>1) {
			ViewScene.m_interface.DrawText(removeText.replace("TARGET", subject.getName()));
		}
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
	public int getStatusIcon() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 90;
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
		Status_Stun other = (Status_Stun) obj;
		if (uid != other.uid)
			return false;
		return true;
	}

	@Override
	public int getUID() {
		// TODO Auto-generated method stub
		return uid;
	}

}
