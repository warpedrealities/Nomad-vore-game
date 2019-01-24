package actorRPG.player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SkillSelection {
	int skillImprovements[];

	public SkillSelection() {
		skillImprovements = new int[15];
		for (int i = 0; i < 15; i++) {
			skillImprovements[i] = 0;
		}
	}

	public SkillSelection(DataInputStream dstream) throws IOException {
		skillImprovements = new int[15];
		for (int i = 0; i < 15; i++) {
			skillImprovements[i] = dstream.readInt();
		}
	}

	public void save(DataOutputStream dstream) throws IOException {
		for (int i = 0; i < 15; i++) {
			dstream.writeInt(skillImprovements[i]);
		}
	}

	public int getSkill(int index) {
		return skillImprovements[index - 6];
	}

	public void setSkill(int index, int value) {
		skillImprovements[index - 6] = value;
	}

	public int[] applyImprovements(int playerAttributes[]) {
		for (int i = 0; i < 15; i++) {
			playerAttributes[i + 6] += skillImprovements[i];
		}
		return playerAttributes;
	}

	public void improveSkill(int index) {
		skillImprovements[index] += 1;
	}
}
