package combat;

import actor.Actor;
import nomad.Universe;
import shared.Vec2f;
import view.ViewScene;
import zone.Tile;
import zone.TileDef.TileMovement;

public class CombatMovementHandler {

	public static void MoveAway(Actor actor, Actor from, int distance) {
		Vec2f direction = new Vec2f(actor.getPosition().x - from.getPosition().x,
				actor.getPosition().y - from.getPosition().y);
		direction.normalize();
		Vec2f p = new Vec2f(actor.getPosition().x, actor.getPosition().y);
		Vec2f last = p;
		for (int i = 0; i < distance; i++) {

			p.add(direction);
			int x = (int) p.x;
			int y = (int) p.y;
			Tile t = Universe.getInstance().getCurrentZone().getTile(x, y);
			if (t == null) {
				break;
			}
			if (t.getDefinition().getBlockVision()) {
				break;
			}
			if (t.getDefinition().getMovement() == TileMovement.WALK && t.getActorInTile() == null) {
				last = new Vec2f(x, y);
			}
		}
		if (Universe.getInstance().getCurrentZone().contains((int) last.x, (int) last.y)) {
			if (actor.getRPG().isThreatening(actor.getActorFaction()))
			{
				Universe.getInstance().getCurrentZone().removeThreat
				((int)actor.getPosition().x, (int)actor.getPosition().y, actor);
			}
			Universe.getInstance().getCurrentZone().getTile((int) actor.getPosition().x, (int) actor.getPosition().y)
					.setActorInTile(null);
			actor.setPosition(last);
			Universe.getInstance().getCurrentZone().getTile((int) actor.getPosition().x, (int) actor.getPosition().y)
					.setActorInTile(actor);
			ViewScene.m_interface.redraw();
		}

	}

	public static void MoveTowards(Actor actor, Actor to, int distance) {
		Vec2f direction = new Vec2f(to.getPosition().x - actor.getPosition().x,
				to.getPosition().y - actor.getPosition().y);
		direction.normalize();
		Vec2f p = actor.getPosition().replicate();
		Vec2f last = p;
		float d = to.getPosition().getDistance(actor.getPosition());
		if (d < distance) {
			distance = (int) d;
		}
		for (int i = 0; i < distance; i++) {

			p.add(direction);
			int x = (int) p.x;
			int y = (int) p.y;
			Tile t = Universe.getInstance().getCurrentZone().getTile(x, y);
			if (t == null) {
				break;
			}
			if (t.getDefinition().getBlockVision()) {
				break;
			}
			if (t.getDefinition().getMovement() == TileMovement.WALK && t.getActorInTile() == null) {
				last = new Vec2f(x, y);
			}
		}
		if (actor.getRPG().isThreatening(actor.getActorFaction()))
		{
			Universe.getInstance().getCurrentZone().removeThreat
			((int)actor.getPosition().x, (int)actor.getPosition().y, actor);
		}
		Universe.getInstance().getCurrentZone().getTile((int) actor.getPosition().x, (int) actor.getPosition().y)
				.setActorInTile(null);
		actor.setPosition(last);
		Universe.getInstance().getCurrentZone().getTile((int) actor.getPosition().x, (int) actor.getPosition().y)
				.setActorInTile(actor);
		ViewScene.m_interface.redraw();
	}
}
