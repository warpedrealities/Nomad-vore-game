package combat;

import actor.Actor;
import actor.Attackable;
import actor.player.Player;
import combat.CombatMove.AttackPattern;
import combat.CombatMove.MoveType;
import shared.Vec2f;
import view.ViewScene;
import vmo.Game;
import vmo.GameManager;

public class multiAttackHandler {

	public static boolean handle(CombatMove combatMove, Actor origin, Attackable target, int def, int bonus, float distance, boolean visible) {
		
		int multiAttack=combatMove.getMultiAttack();
		boolean hit=false, critical=false;
		int value=0;
		
		for (int j=0;j<multiAttack;j++)
		{
			int r=GameManager.m_random.nextInt(20) + bonus;
			if (r>=def)
			{
				hit=true;
				if (Game.sceneManager.getConfig().isVerboseCombat() && r < 100 && origin.getVisible()) {
					ViewScene.m_interface.DrawText(origin.getName() + " attacks " + target.getName() + " " + (r - bonus)
							+ "+" + bonus + " DC:" + def + "=hit");
				}	
				boolean crit = false;
				
				if (r > def + 10 && r < 100 && Player.class.isInstance(origin) && Actor.class.isInstance(target)) {
					crit = true;
					critical=true;
				}
				for (int i = 0; i < combatMove.getEffects().size(); i++) {
					value += target.applyEffect(combatMove.getEffects().get(i), origin, crit);
				}		
			}
			else
			{
				if (Game.sceneManager.getConfig().isVerboseCombat() && r < 100 && origin.getVisible()) {
					ViewScene.m_interface.DrawText(origin.getName() + " attacks " + target.getName() + " " + (r - bonus)
							+ "+" + bonus + " DC:" + def + "=miss");
				}			
			}
	
		}
		
		if (hit)
		{
			origin.getThreat().addThreat(value);
			if (target.getAttackable() && visible) {
				String text = combatMove.getHitText()[GameManager.m_random.nextInt(combatMove.getHitText().length)];
				if (text.length() > 0) {
					String str = text.replace("TARGET", target.getName()).replace("VALUE", Integer.toString(value));
					if (critical) {
						ViewScene.m_interface.DrawText(str + " (CRITICAL!)");
					} else {
						if (ViewScene.m_interface!=null)
						{
							ViewScene.m_interface.DrawText(str);
						}
			
					}
				}
			}			
			if (visible) {

				if (combatMove.getAttackPattern() == AttackPattern.P_MELEE || 
						combatMove.getAttackPattern() == AttackPattern.P_RANGED
						|| combatMove.getAttackPattern() == AttackPattern.P_SHORT||
								combatMove.getAttackPattern() == AttackPattern.P_REACH) {
					if (combatMove.isNonViolent()) {
						ViewScene.m_interface.Flash(target.getPosition(), 1);
					} else {
						if (ViewScene.m_interface!=null)
						{
							if (distance >= 2 && combatMove.getAttackPattern() != AttackPattern.P_CONE && combatMove.getMoveType()!=MoveType.MOVEMENT) {
								ViewScene.m_interface.projectile(new Vec2f(target.getPosition().x, target.getPosition().y),
										new Vec2f(origin.getPosition().x, origin.getPosition().y), 0);
							}
							else
							{
								ViewScene.m_interface.Flash(target.getPosition(), 0);					
							}
						}
			
					}

				}
			}		
		}
		else
		{

			// show miss text
			String text = combatMove.getMissText()[GameManager.m_random.nextInt(combatMove.getMissText().length)];
			String str = text.replace("TARGET", target.getName());
			ViewScene.m_interface.DrawText(str);
			if (visible && distance >= 2 && combatMove.getAttackPattern() != 
					AttackPattern.P_CONE && combatMove.getMoveType()!=MoveType.MOVEMENT) {
				ViewScene.m_interface.projectile(new Vec2f(target.getPosition().x, target.getPosition().y),
						new Vec2f(origin.getPosition().x, origin.getPosition().y), 2);
			}
		}
		return true;
	}

}
