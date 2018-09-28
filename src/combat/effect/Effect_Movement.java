package combat.effect;

import org.w3c.dom.Element;

import actor.Actor;
import actorRPG.RPG_Helper;
import combat.CombatMovementHandler;

public class Effect_Movement extends Effect {

	enum MovementType {
		self_away, self_towards, target_towards, target_away
	};

	private MovementType movementType;
	private int distance;
	private int abilityMod;

	public static MovementType strToMType(String str) {
		if (str.equals("SELF_AWAY")) {
			return MovementType.self_away;
		}
		if (str.equals("SELF_TOWARDS")) {
			return MovementType.self_towards;
		}
		if (str.equals("TARGET_TOWARDS")) {
			return MovementType.target_towards;
		}
		if (str.equals("TARGET_AWAY")) {
			return MovementType.target_away;
		}
		return null;
	}

	public Effect_Movement(Element node) {
		movementType = strToMType(node.getAttribute("moveType"));
		distance = Integer.parseInt(node.getAttribute("distance"));
		abilityMod = RPG_Helper.abilityFromString(node.getAttribute("abilityMod"));
	}

	@Override
	public int applyEffect(Actor origin, Actor target, boolean critical) {
		if (movementType == MovementType.self_away || movementType == MovementType.self_towards) {
			return applySelf(origin, target);
		} else {
			return applyTarget(origin, target);
		}
	}

	private int applySelf(Actor origin, Actor target) {
		int localDistance = origin.getRPG().getAbilityMod(abilityMod) + distance;
		if (movementType == MovementType.self_away) {
			CombatMovementHandler.MoveAway(origin, target, localDistance);
		}
		if (movementType == MovementType.self_towards) {
			CombatMovementHandler.MoveTowards(origin, target, localDistance);
		}
		return 0;
	}

	private int applyTarget(Actor origin, Actor target) {
		int localDistance = origin.getRPG().getAbility(abilityMod) - origin.getRPG().getAbilityMod(abilityMod)
				+ distance;
		if (movementType == MovementType.target_towards) {
			CombatMovementHandler.MoveTowards(target, origin, localDistance);
		}
		if (movementType == MovementType.target_away) {
			CombatMovementHandler.MoveAway(target, origin, localDistance);
		}
		return 0;
	}

	@Override
	public Effect clone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void applyChange(Effect effect,int rank) {
		// TODO Auto-generated method stub

	}

}
