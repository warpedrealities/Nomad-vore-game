package combat.statusEffects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;

import actorRPG.Actor_RPG;
import faction.Faction;
import faction.FactionLibrary;
import shared.ParserHelper;

public class StatusFaction implements StatusEffect {

	private int uid;
	private int duration;
	private Faction faction;
	
	public StatusFaction()
	{
		
	}
	
	public StatusFaction(Element e) {
		uid = Integer.parseInt(e.getAttribute("uid"));
		if (e.getAttribute("duration").length() > 0) {
			duration = Integer.parseInt(e.getAttribute("duration"));
		}
		faction=FactionLibrary.getInstance().getFaction(e.getTextContent());
	}

	@Override
	public void load(DataInputStream dstream) throws IOException {
		uid=dstream.readInt();
		duration=dstream.readInt();
		faction=FactionLibrary.getInstance().getFaction(ParserHelper.LoadString(dstream));
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		dstream.writeInt(7);
		dstream.writeInt(uid);
		dstream.writeInt(duration);
		ParserHelper.SaveString(dstream, faction.getFilename());
	}

	@Override
	public void apply(Actor_RPG subject) {
		
	}

	@Override
	public void update(Actor_RPG subject) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(Actor_RPG subject) {
		
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
		StatusFaction status = new StatusFaction();
		status.duration = this.duration;

		status.uid = this.uid;
		status.faction = faction;

		return status;
	}

	@Override
	public int getStatusIcon() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getUID() {
		return uid;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StatusFaction other = (StatusFaction) obj;
		if (uid != other.uid)
			return false;
		return true;
	}

	public Faction getFaction() {
		return faction;
	}
}
