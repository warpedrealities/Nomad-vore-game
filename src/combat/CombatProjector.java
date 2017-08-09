package combat;

import actor.Actor;
import actor.Attackable;
import actor.player.Player;
import actorRPG.Actor_RPG;
import nomad.Universe;
import rlforj.los.ILosBoard;
import shared.Vec2f;
import view.ViewScene;
import vmo.Game;
import vmo.GameManager;
import zone.Tile;
import zone.Zone;

public class CombatProjector implements ILosBoard {

	Zone zone;
	Actor origin;
	CombatMove move;

	public CombatProjector(Actor origin, Zone zone, CombatMove move) {
		this.origin = origin;
		this.zone = zone;
		this.move = move;
	}

	@Override
	public boolean contains(int x, int y) {
		if (x < 0 || y < 0) {
			return false;
		}
		if (x >= zone.getWidth() || y >= zone.getHeight()) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isObstacle(int x, int y) {
		return zone.isObstacle(x, y);
	}

	@Override
	public void visit(int x, int y) {

		if (zone.getTile(x, y) != null && zone.getTile(x, y).getActorInTile() != null) {
			if (zone.getTile(x, y).getActorInTile() != origin) {
				attack(origin, move, zone.getTile(x, y).getActorInTile());
			}

		}
	}

	private boolean getVisible(Vec2f p) {
		Tile t = Universe.getInstance().getCurrentZone().getTile((int) p.x, (int) p.y);

		if (t != null && t.getVisible()) {
			return true;
		}
		return false;
	}

	private void attack(Actor origin, CombatMove move, Attackable target) {
		int defAttribute = Actor_RPG.DODGE;
		if (move.isNonViolent()) {
			defAttribute = Actor_RPG.WILLPOWER;
		}
		// get defence
		int def = CombatLookup.getBaseDefence(0, defAttribute) + target.getAttribute(defAttribute);
		// get attack bonus
		int bonus = origin.getRPG().getAttribute(move.getBonusAttribute());
		boolean visible = getVisible(target.getPosition());
		int r = GameManager.m_random.nextInt(20) + bonus;
		if (def <= r) {
			if (move.isNonViolent()) {
				ViewScene.m_interface.Flash(target.getPosition(), 1);
			} else {
				ViewScene.m_interface.Flash(target.getPosition(), 0);
			}
			if (Game.sceneManager.getConfig().isVerboseCombat() && r < 100 && origin.getVisible()) {
				ViewScene.m_interface.DrawText(origin.getName() + " attacks " + target.getName() + " " + (r - bonus)
						+ "+" + bonus + " DC:" + def + "=hit");
			}
			int value = 0;
			// apply effect to target
			boolean critical = false;

			if (r >= def + 10 && r < 100 && Player.class.isInstance(origin) && Actor.class.isInstance(target)) {
				critical = true;
			}
			for (int i = 0; i < move.getEffects().size(); i++) {
				value += target.applyEffect(move.getEffects().get(i), origin, critical);
			}
			// show hit text

			if (target.getAttackable() && visible) {
				String text = move.getHitText()[GameManager.m_random.nextInt(move.getHitText().length)];
				if (text.length() > 0) {
					String str = text.replace("TARGET", target.getName()).replace("VALUE", Integer.toString(value));
					if (critical) {
						ViewScene.m_interface.DrawText(str + " (CRITICAL!)");
					} else {
						ViewScene.m_interface.DrawText(str);
					}
				}
			}
		} else {
			if (visible) {
				if (Game.sceneManager.getConfig().isVerboseCombat() && r < 100 && origin.getVisible()) {
					ViewScene.m_interface.DrawText(origin.getName() + " attacks " + target.getName() + " " + (r - bonus)
							+ "+" + bonus + " DC:" + def + "=miss");
				}
				// show miss text
				String text = move.getMissText()[GameManager.m_random.nextInt(move.getMissText().length)];
				String str = text.replace("TARGET", target.getName());
				ViewScene.m_interface.DrawText(str);

			}
		}

	}

}
