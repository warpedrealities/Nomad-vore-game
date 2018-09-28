package actor.npc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;

import shared.ParserHelper;

public class Crew {

	public enum Skill {
		NAVIGATION, GUNNERY, TECHNICIAN, ENGINEER
	};

	private Skill skill;
	private int rank;

	public Crew(Skill skill, int rank) {
		this.skill = skill;
		this.rank = rank;
	}

	public Crew(DataInputStream dstream) throws IOException {
		skill=Skill.valueOf(ParserHelper.LoadString(dstream));
		rank=dstream.readInt();
	}

	public Crew(Element enode) {
		skill=Skill.valueOf(enode.getAttribute("skill"));
		rank=Integer.parseInt(enode.getAttribute("value"));
	}

	public Skill getSkill() {
		return skill;
	}

	public int getRank() {
		return rank;
	}

	public void save(DataOutputStream dstream) throws IOException {
		ParserHelper.SaveString(dstream, skill.toString());
		dstream.writeInt(rank);
	}

}
