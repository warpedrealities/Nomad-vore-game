package artificial_intelligence;

import pathfinder.Path;
import shared.Vec2f;
import view.ViewScene;
import view.ZoneInteractionHandler;
import zone.Tile;
import zone.Zone_int;
import zone.TileDef.TileMovement;
import actor.Actor;
import actor.npc.NPC;
import artificial_intelligence.pathfinding.Pathfinder_Flee;
import nomad.universe.Universe;

public class SpecialCommandHandler {

	private static SpecialCommandHandler instance;

	public static SpecialCommandHandler getInstance() {
		if (instance == null) {
			instance = new SpecialCommandHandler();
		}

		return instance;
	}

	public boolean performSpecial(String command, NPC npc, Zone_int zone) {
		if (command.equals("hide")) {
			return performHide(npc, zone);
		}
		if (command.equals("flee")) {
			return performFlee(npc, zone);
		}
		if (command.equals("retreat")) {
			return performRetreat(npc, zone);
		}
		return false;
	}

	private boolean performRetreat(NPC npc, Zone_int zone) {

		Actor hostile=ViewScene.m_interface.getSceneController().getHostile(npc, 10, true);
		if (hostile!=null)
		{
			Vec2f p = hostile.getPosition();
			Pathfinder_Flee pathfinder = new Pathfinder_Flee(zone);
			artificial_intelligence.pathfinding.Path path = pathfinder.findPath(npc.getPosition(), p, 8, npc.getFlying());
			if (path != null) {
				npc.setActorPath(path);
				npc.FollowPath();
				return true;
			}			
		}

		return false;
	}

	private boolean performFlee(NPC npc, Zone_int zone) {

		Vec2f p = Universe.getInstance().getPlayer().getPosition();
		Pathfinder_Flee pathfinder = new Pathfinder_Flee(zone);
		artificial_intelligence.pathfinding.Path path = pathfinder.findPath(npc.getPosition(), p, 8, npc.getFlying());
		if (path != null) {
			npc.setActorPath(path);
			npc.FollowPath();
			return true;
		}
		return false;
	}

	private boolean performHide(NPC npc, Zone_int zone) {

		Vec2f player = Universe.getInstance().getPlayer().getPosition();

		Vec2f[] dest = new Vec2f[8];
		Tile[] tiles = new Tile[8];
		for (int i = 0; i < 8; i++) {
			dest[i] = ZoneInteractionHandler.getPos(i, new Vec2f(0, 0));
			dest[i].x = dest[i].x * 4;
			dest[i].y = dest[i].y * 4;
			dest[i].x += npc.getPosition().x;
			dest[i].y += npc.getPosition().y;
			tiles[i] = zone.getTile((int) dest[i].x, (int) dest[i].y);
		}

		int r = Universe.m_random.nextInt(8);
		for (int i = 0; i < 8; i++) {
			int z = r + i;
			if (z > 7) {
				z = z - 8;
			}
			if (tiles[z] != null) {
				if (tiles[z].getVisible() == false && tiles[z].getDefinition().getMovement() == TileMovement.WALK) {
					npc.Pathto((int) dest[z].x, (int) dest[z].y, 4);
					return true;
				}
			}
		}
		float distance = 0;
		int index = -1;
		for (int i = 0; i < 8; i++) {
			int z = r + i;
			if (z > 7) {
				z = z - 8;
			}
			if (tiles[z] != null) {
				if (tiles[z].getDefinition().getMovement() == TileMovement.WALK) {
					float d = dest[z].getDistance(player);
					if (d > distance) {
						index = z;
						distance = d;
					}
				}
			}
		}
		if (index > -1) {
			npc.Pathto((int) dest[index].x, (int) dest[index].y, 4);
			return true;
		}

		int rx = Universe.m_random.nextInt(zone.getWidth());
		int ry = Universe.m_random.nextInt(zone.getHeight());
		Tile t = zone.getTile(rx, ry);
		if (t != null && t.getDefinition().getMovement() == TileMovement.WALK) {
			npc.Pathto(rx, ry, 4);
			return true;
		}

		return false;
	}
}
