package actorRPG.player.reactive;

import perks.PerkReactive.CharacterStat;

public class ReactiveAbility {
	private float triggerMod, effectMod;
	private CharacterStat trigger, effect;

	public ReactiveAbility(float triggerMod, float effectMod, CharacterStat trigger, CharacterStat effect) {
		super();
		this.triggerMod = triggerMod;
		this.effectMod = effectMod;
		this.trigger = trigger;
		this.effect = effect;
	}

	public float getTriggerMod() {
		return triggerMod;
	}

	public float getEffectMod() {
		return effectMod;
	}

	public CharacterStat getTrigger() {
		return trigger;
	}

	public CharacterStat getEffect() {
		return effect;
	}

}
