package combat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CooldownHandler {

	ArrayList<MoveCooldown> cooldowns;
	Map<String, MoveCooldown> cooldownMap;

	public CooldownHandler() {
		cooldowns = new ArrayList<MoveCooldown>();
		cooldownMap = new HashMap<String, MoveCooldown>();
	}

	private MoveCooldown[] getRetainedCooldowns(MoveCooldown[] newMoves) {
		cooldowns.remove(newMoves);
		MoveCooldown[] list = new MoveCooldown[cooldowns.size()];
		int index = 0;
		for (int i = 0; i < list.length; i++) {
			if (cooldowns.get(i).getCooldown() > 0) {
				list[index] = cooldowns.get(i);
				list[index].setVisible(false);
				index++;
			}
		}

		return list;
	}

	public void updateList(CombatMove[] moves) {
		MoveCooldown[] newlist = new MoveCooldown[moves.length];
		// check for duplicates
		int index = 0;
		for (int i = 0; i < moves.length; i++) {
			for (int j = 0; j < cooldowns.size(); j++) {
				if (moves[i].getMoveName().equals(cooldowns.get(j).getMoveName())) {
					newlist[index] = cooldowns.get(j);
					newlist[index].setVisible(true);
					index++;
					moves[i] = null;

					break;
				}
			}
		}
		if (index < moves.length) {
			for (int i = 0; i < moves.length; i++) {
				if (moves[i] != null) {
					newlist[index] = new MoveCooldown(moves[i].getIcon(), moves[i].getMoveName(),
							moves[i].getMoveCooldown());
					index++;
				}
			}
		}

		MoveCooldown[] retain = getRetainedCooldowns(newlist);
		cooldownMap.clear();
		cooldowns.clear();
		for (int i = 0; i < newlist.length; i++) {
			cooldowns.add(newlist[i]);
			cooldownMap.put(newlist[i].getMoveName(), newlist[i]);
		}
		for (int i = 0; i < retain.length; i++) {
			if (retain[i] != null) {
				cooldowns.add(retain[i]);
			}
		}
	}

	public void save(DataOutputStream dstream) throws IOException {
		dstream.writeInt(cooldowns.size());
		for (int i = 0; i < cooldowns.size(); i++) {
			cooldowns.get(i).save(dstream);
		}
	}

	public void load(DataInputStream dstream) throws IOException {
		int count = dstream.readInt();
		for (int i = 0; i < count; i++) {
			MoveCooldown cooldown = new MoveCooldown();
			cooldown.load(dstream);
			cooldowns.add(cooldown);
		}
	}

	public void update(int time) {
		for (int i = 0; i < cooldowns.size(); i++) {
			if (cooldowns.get(i) != null) {
				cooldowns.get(i).update(time);
			}
		}
	}

	public void useMove(String name) {
		cooldownMap.get(name).use();
	}

	public MoveCooldown getCooldown(int index) {
		return cooldowns.get(index);
	}

	public int getCount() {
		return cooldowns.size();
	}

	public boolean moveIsUnusable(String moveName) {
		MoveCooldown cooldown = cooldownMap.get(moveName);
		if (cooldown != null) {
			if (cooldown.getCooldown() > 0) {
				return true;
			}
		}
		return false;
	}
}
