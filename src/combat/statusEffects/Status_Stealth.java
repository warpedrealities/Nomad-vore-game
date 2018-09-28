package combat.statusEffects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;

import actorRPG.Actor_RPG;
import nomad.universe.Universe;
import view.ViewScene;

public class Status_Stealth implements StatusEffect {

	private int icon, uid, strength, interval, duration, clock;

	public Status_Stealth() {

	}

	public Status_Stealth(Element e) {
		uid = Integer.parseInt(e.getAttribute("uid"));
		icon = Integer.parseInt(e.getAttribute("icon"));
		if (e.getAttribute("duration").length() > 0) {
			duration = Integer.parseInt(e.getAttribute("duration"));
		}
		strength = Integer.parseInt(e.getAttribute("strength"));
		interval = Integer.parseInt(e.getAttribute("interval"));
	}

	@Override
	public void load(DataInputStream dstream) throws IOException {
		// TODO Auto-generated method stub
		icon = dstream.readInt();
		uid = dstream.readInt();
		strength = dstream.readInt();
		interval = dstream.readInt();
		duration = dstream.readInt();
		clock = dstream.readInt();
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {

		dstream.writeInt(4);
		dstream.writeInt(icon);
		dstream.writeInt(uid);
		dstream.writeInt(strength);
		dstream.writeInt(interval);
		dstream.writeInt(duration);
		dstream.writeInt(clock);
	}

	@Override
	public void apply(Actor_RPG subject) {

	}

	@Override
	public void update(Actor_RPG subject) {
		if (clock > 0) {
			clock--;
		}
	}

	@Override
	public void remove(Actor_RPG subject, boolean suppressMessages) {

		if (subject.getActor().getVisible() && !suppressMessages) {
			ViewScene.m_interface.DrawText(subject.getName() + " is no longer stealthy");
		}

	}

	@Override
	public boolean maintain() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public StatusEffect cloneEffect() {

		Status_Stealth status = new Status_Stealth();
		status.clock = clock;
		status.duration = duration;
		status.interval = interval;
		status.strength = strength;
		status.uid = uid;
		return status;
	}

	@Override
	public int getStatusIcon() {

		return icon;
	}

	@Override
	public int getUID() {

		return uid;
	}

	public int getClock() {
		return clock;
	}

	public boolean spotCheck(int spot) {
		if (clock > 0) {
			return false;
		}
		int roll = Universe.getInstance().m_random.nextInt(20) + spot;
		if (roll > strength) {
			return true;
		}
		clock = interval;
		return false;
	}

}
