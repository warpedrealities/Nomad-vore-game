package actor.player;

import java.util.ArrayList;

import actor.Actor;
import actor.npc.NPC;
import actorRPG.Actor_RPG;
import nomad.universe.Universe;
import shared.Vec2f;
import shared.Vec2i;
import spaceship.Spaceship;
import view.ZoneInteractionHandler;
import widgets.WidgetPortal;
import zone.Zone;

public class CompanionTool {

	static public void loadCompanions(Player player, Zone zone) {
		if (player.companionSlots != null) {
			for (int i = 0; i < player.companionSlots.length; i++) {
				if (player.companionSlots[i] != null) {
					player.companionSlots[i].setCollisioninterface(zone);
					ArrayList<Actor> actors = zone.getActors();
					boolean b = true;
					for (int j = 0; j < actors.size(); j++) {
						if (player.companionSlots[i] == actors.get(j)) {
							b = false;
						}
					}
					if (b == true) {
						actors.add(player.companionSlots[i]);
					}

				}
			}
		}
	}

	static private void addActor(NPC actor, Zone zone) {
		ArrayList<Actor> actors = zone.getActors();
		boolean b = true;
		for (int j = 0; j < actors.size(); j++) {
			if (actor == actors.get(j)) {
				b = false;
			}
		}
		if (b == true) {
			actors.add(actor);
		}
	}

	static public void moveCompanions(Player player, Zone zone) {
		if (player.companionSlots != null) {
			for (int i = 0; i < player.companionSlots.length; i++) {
				if (player.companionSlots[i] != null) {

					addActor(player.companionSlots[i], zone);
					placeCompanion(player, zone, player.companionSlots[i]);
				}
			}
		}
	}

	static private void placeCompanion(Player player, Zone zone, Actor companion) {
		companion.getRPG().IncreaseStat(Actor_RPG.RESOLVE, 5);
		companion.getRPG().IncreaseStat(Actor_RPG.HEALTH, 5);
		companion.setActorVisibility(true);
		companion.getSpriteInterface().setVisible(true);
		companion.setCollisioninterface(zone);

		int r = Universe.m_random.nextInt(8);
		for (int i = 0; i < 8; i++) {
			int z = r + i;
			if (z >= 8) {
				z = z - 8;
			}
			Vec2f p = ZoneInteractionHandler.getPos(z, player.getPosition());
			if (zone.isObstacle((int) p.x, (int) p.y) == false && zone.getActor((int) p.x, (int) p.y) == null) {

				companion.setPosition(new Vec2f(p.x, p.y));
				return;
			}

		}
	}

	public static void addCompanion(NPC m_npc, Player m_player) {
		m_npc.setCompanion(true);
		m_npc.setActorFaction(m_player.getActorFaction());
		NPC[] list = m_player.companionSlots;
		for (int i = 0; i < list.length; i++) {
			if (list[i] == null) {
				list[i] = m_npc;
				break;
			}
		}
	}

	public static void removeCompanion(NPC m_npc, Player m_player) {

		m_npc.setCompanion(false);
		NPC[] list = m_player.companionSlots;
		for (int i = 0; i < list.length; i++) {
			if (list[i] == m_npc) {
				list[i] = null;
				break;
			}
		}
	}

	private static boolean placePassenger(Zone zone, NPC npc, Vec2f position) {
		int r = Universe.m_random.nextInt(8);
		for (int i = 0; i < 8; i++) {
			int z = r + i;
			if (z >= 8) {
				z = z - 8;
			}
			Vec2f p = ZoneInteractionHandler.getPos(z, position);
			if (zone.isObstacle((int) p.x, (int) p.y) == false && zone.getActor((int) p.x, (int) p.y) == null) {

				npc.setPosition(new Vec2f(p.x, p.y));
				return true;
			}

		}
		return false;
	}

	public static void removePassenger(NPC npc, Spaceship ship) {
		ship.getZone(0).getTile((int)npc.getPosition().x,(int)npc.getPosition().y).setActorInTile(null);
		WidgetPortal portal = ship.getZone(0).getPortalWidget(-101);
		if (portal.getTarget() != null) {
			Zone zone = Universe.getInstance().getCurrentEntity().getZone(portal.getTarget());
			if (zone != null) {
				npc.setCollisioninterface(zone);
				Vec2f p = portal.getTargetXY();
				if (p == null) {
					p = zone.getPortal(-101);
				}
				if (placePassenger(zone, npc, p)) {
					addActor(npc, zone);
					ship.getZone(0).getActors().remove(npc);
					if (npc.isCompanion()) {
						removeCompanion(npc, Universe.getInstance().getPlayer());
					}

				}
			}
		}
	}

	public void removeAllCompanions(Player m_player) {
		NPC[] list = m_player.companionSlots;
		for (int i = 0; i < list.length; i++) {
				list[i] = null;
		}
	}
}
