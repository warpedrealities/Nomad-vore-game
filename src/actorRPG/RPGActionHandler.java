package actorRPG;

import combat.CombatMove;

import shared.Vec2f;
import view.ModelController_Int;
import vmo.GameManager;
import nomad.Universe;
import actor.Actor;
import actor.Attackable;

public class RPGActionHandler {

	Actor_RPG actorRPG;
	Actor hostActor;
	
	public RPGActionHandler(Actor_RPG actorRPG, Actor hostActor)
	{
		this.actorRPG=actorRPG;
		this.hostActor=hostActor;
	}
	
	public CombatMove getAttack()
	{
		return actorRPG.getCombatMove(0);
	}
	
	public int getMeleeDefence() {

		return 10+actorRPG.getAttribute(Actor_RPG.DODGE)+actorRPG.getAttribute(Actor_RPG.PARRY);
	}
	
	public int getRangedDefence() {

		return 15+actorRPG.getAttribute(Actor_RPG.DODGE);
	}

	public boolean getAttackable()
	{
		if (actorRPG.getStat(Actor_RPG.HEALTH)>0 && actorRPG.getStat(Actor_RPG.RESOLVE)>0)
		{
			return true;
		}
		
		return false;
	}

	public boolean getActive() {
		if (actorRPG.getStat(Actor_RPG.HEALTH)>0 && actorRPG.getStat(Actor_RPG.RESOLVE)>0)
		{
			return true;
		}
		
		return false;
	}

	public int getResolveDefence() {
		
		return 10+actorRPG.getAttribute(Actor_RPG.WILLPOWER);
	}

}
