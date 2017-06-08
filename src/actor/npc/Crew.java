package actor.npc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Crew {
	
	public enum Skill{NAVIGATION,GUNNERY,TECHNICIAN,ENGINEER};
	
	private Skill skill;
	private int rank;
	
	public Crew(Skill skill, int rank)
	{
		this.skill=skill;
		this.rank=rank;
	}

	public Crew(DataInputStream dstream) throws IOException {
		// TODO Auto-generated constructor stub
	}

	public Skill getSkill() {
		return skill;
	}

	public int getRank() {
		return rank;
	}

	public void save(DataOutputStream dstream) throws IOException {
		// TODO Auto-generated method stub
		
	}
	
	
}
