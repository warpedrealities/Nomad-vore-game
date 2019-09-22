package actorRPG.player.reactive;

import java.util.ArrayList;
import java.util.List;

import actorRPG.player.Player_RPG;
import perks.PerkReactive;
import perks.PerkReactive.CharacterStat;

public class StatReactiveAbilities {

	private List<ReactiveAbility> positives;
	private List<ReactiveAbility> negatives;

	public StatReactiveAbilities() {
		positives = new ArrayList<ReactiveAbility>();
		negatives = new ArrayList<ReactiveAbility>();
	}

	public void clean() {
		positives.clear();
		negatives.clear();
	}

	public void addNegative(PerkReactive reactive) {
		negatives.add(new ReactiveAbility(reactive.getTriggerMod(), reactive.getEffectMod(), reactive.getStatTrigger(),
				reactive.getStatEffect()));
	}

	public void addPositive(PerkReactive reactive) {
		positives.add(new ReactiveAbility(reactive.getTriggerMod(), reactive.getEffectMod(), reactive.getStatTrigger(),
				reactive.getStatEffect()));
	}

	public float handleIncrease(float value, Player_RPG rpg) {
		for (int i = 0; i < positives.size(); i++) {
			float m = value * positives.get(i).getEffectMod();
			if (positives.get(i).getEffect() != CharacterStat.S_NONE) {
				if (m > 0) {
					rpg.IncreaseStat(positives.get(i).getEffect().getValue(), m, false);
				} else {
					rpg.ReduceStat(positives.get(i).getEffect().getValue(), m, false);
				}
			}
			value = value * positives.get(i).getTriggerMod();
		}
		return value;
	}

	public float handleDecrease(float value, Player_RPG rpg) {
		for (int i = 0; i < negatives.size(); i++) {
			float m = value * negatives.get(i).getEffectMod();
			if (negatives.get(i).getEffect() != CharacterStat.S_NONE) {
				if (m > 0) {
					rpg.IncreaseStat(positives.get(i).getEffect().getValue(), m, false);
				} else {
					rpg.ReduceStat(positives.get(i).getEffect().getValue(), m, false);
				}
			}
			value = value * positives.get(i).getTriggerMod();
		}
		return value;
	}

}
