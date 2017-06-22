package view;

import nomad.Universe;
import shared.Vec2f;
import vmo.GameManager;
import zone.TileDef;
import zone.Zone;
import actor.Actor;
import actor.player.Inventory;
import actorRPG.Actor_RPG;
import actorRPG.Player_RPG;
import artificial_intelligence.Sense;
import item.Item;
import item.ItemWeapon;

public class SceneController implements Sense {

	private Zone activeZone;
	private Universe gameUniverse;
	private ZoneInteractionHandler interactionHandler;
	private QuickslotHandler quickslotHandler = new QuickslotHandler();

	public void initializeHandler(ModelController_Int view) {
		interactionHandler = new ZoneInteractionHandler(null, view);
	}

	public ZoneInteractionHandler getHandler() {
		return interactionHandler;
	}

	public Zone getActiveZone() {
		return activeZone;
	}

	public void setActiveZone(Zone m_zone) {
		this.activeZone = m_zone;
		interactionHandler.setZone(m_zone);
	}

	public Universe getUniverse() {
		return gameUniverse;
	}

	public void setUniverse(Universe m_world) {
		this.gameUniverse = m_world;
	}

	@Override
	public boolean CanWalk(int x, int y) {
		if (activeZone.getTiles()[x][y] != null) {
			if (activeZone.getTiles()[x][y].getDefinition().getMovement() == TileDef.TileMovement.WALK) {
				return true;
			}

		}
		return false;
	}

	@Override
	public int getViolationLevel() {

		if (interactionHandler.getFactionListener() != null) {
			return interactionHandler.getFactionListener().getViolationLevel();
		}
		return 0;
	}

	@Override
	public Vec2f getViolationLocation() {

		if (interactionHandler.getFactionListener() != null) {
			return interactionHandler.getFactionListener().getViolationLocation();
		}

		return null;
	}

	public void shutdown() {
		interactionHandler.shutdown();
	}

	public void update() {
		for (int i = 0; i < activeZone.getActors().size(); i++) {
			if (activeZone.getActors().get(i).getPosition().x >= 0) {
				activeZone.getActors().get(i).Update();
			}

		}
		interactionHandler.update();
	}

	@Override
	public Actor getHostile(Actor origin, int maxRange, boolean visibleOnly) {
		// TODO Auto-generated method stub
		Actor hostile = null;
		float distance = 99;
		for (int i = 0; i < activeZone.getActors().size(); i++) {
			if (activeZone.getActors().get(i).getAttackable()
					&& activeZone.getActors().get(i).isHostile(origin.getActorFaction().getFilename())) {
				Actor target = activeZone.getActors().get(i);
				float d = target.getPosition().getDistance(origin.getPosition());
				if (d < maxRange) {
					if (visibleOnly && activeZone.getActors().get(i).getRPG().getStealthState() == -1) {
						if (GameManager.m_los.existsLineOfSight(activeZone, (int) origin.getPosition().x,
								(int) origin.getPosition().y, (int) target.getPosition().x,
								(int) target.getPosition().y, true)) {
							if (distance > d) {
								distance = d;
								hostile = target;
							}
						}
					} else {
						if (distance > d) {
							distance = d;
							hostile = target;
						}
					}

				}

			}
		}
		return hostile;
	}

	@Override
	public Actor getPlayer(Actor origin, boolean visibleOnly) {
		if (visibleOnly == true) {
			if (GameManager.m_los.existsLineOfSight(activeZone, (int) origin.getPosition().x,
					(int) origin.getPosition().y, (int) gameUniverse.getPlayer().getPosition().x,
					(int) gameUniverse.getPlayer().getPosition().y, true)) {
				return gameUniverse.getPlayer();
			}
		} else {
			return gameUniverse.getPlayer();
		}

		return null;
	}

	@Override
	public boolean getPreference(String preference) {

		return Universe.getInstance().getPrefs().ForbiddenPref(preference);

	}

	@Override
	public void drawText(String text) {

		ViewScene.m_interface.DrawText(text);

	}

	public void useQuickslot() {
		quickslotHandler.handle();

	}

}
