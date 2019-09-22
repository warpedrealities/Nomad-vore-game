package actorRPG.player.reactive;

import java.util.ArrayList;
import java.util.List;

import actorRPG.player.Player_RPG;
import perks.PerkInstance;
import perks.PerkReactive;

public class ReactiveAbilities {

	private List<StatReactiveAbilities> reactions;

	public ReactiveAbilities() {
		reactions = new ArrayList<>(4);
		for (int i = 0; i < 4; i++) {
			reactions.add(new StatReactiveAbilities());
		}
	}

	public void clean() {
		for (int i = 0; i < 4; i++) {
			reactions.get(i).clean();
		}
	}

	public void add(PerkReactive reactive, int rank) {
		if (reactive.isNegativeTrigger()) {
			reactions.get(reactive.getStatTrigger().getValue()).addNegative(reactive);
		} else {
			reactions.get(reactive.getStatTrigger().getValue()).addPositive(reactive);
		}
	}


	public void processPerks(List<PerkInstance> perks) {
		clean();
		for (int i = 0; i < perks.size(); i++) {
			for (int j = 0; j < perks.get(i).getPerk().getNumElements(); j++) {
				if (PerkReactive.class.isInstance(perks.get(i).getPerk().getElement(j))) {
					PerkReactive p = (PerkReactive) perks.get(i).getPerk().getElement(j);
					add(p, perks.get(i).getPerkRank());
				}
			}
		}
	}

	public float increase(int target, float value, Player_RPG player_RPG) {
		return reactions.get(target).handleIncrease(value, player_RPG);
	}

	public float reduce(int target, float value, Player_RPG player_RPG) {
		return reactions.get(target).handleDecrease(value, player_RPG);
	}
}
