package worldgentools;

import nomad.Universe;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import actor.npc.NPC;
import actor.npc.ScriptPackage;
import shared.ParserHelper;
import shared.Vec2f;
import shared.Vec2i;
import zone.Tile;
import zone.Zone;
import zone.TileDef.TileMovement;

public class NPCPlacer {

	Zone zone;
	PointsOfInterest pointsOfInterest;

	public NPCPlacer(Zone zone, PointsOfInterest pointsOfInterest) {
		this.zone = zone;
		this.pointsOfInterest = pointsOfInterest;
	}

	public void PlaceNPC(Element Enode, boolean[][] grid, int xoffset, int yoffset) {
		int x = Integer.parseInt(Enode.getAttribute("x")) + xoffset;
		int y = Integer.parseInt(Enode.getAttribute("y")) + yoffset;
		Tile tile = zone.getTile(x, y);

		ScriptPackage script = null;
		NodeList scripts = Enode.getElementsByTagName("script");
		if (scripts.getLength() > 0) {
			Element scriptElement = (Element) scripts.item(0);
			String death = null, spawn = null;
			if (scriptElement.getAttribute("death").length() > 0) {
				death = scriptElement.getAttribute("death");
			}
			if (scriptElement.getAttribute("spawn").length() > 0) {
				spawn = scriptElement.getAttribute("spawn");
			}
			script = new ScriptPackage(spawn, death);
		}

		if (zone.getTile(x, y) != null && zone.getTile(x, y).getDefinition().getMovement() == TileMovement.WALK) {
			// npc name
			String name = Enode.getAttribute("name");

			Document doc = ParserHelper.LoadXML("assets/data/npcs/" + name + ".xml");
			Element n = (Element) doc.getFirstChild();
			NPC npc = new NPC(n, new Vec2f(x, y), name);
			npc.setScripts(script);
			npc.setCollisioninterface(zone);
			zone.getTile(x, y).setActorInTile(npc);
			zone.getActors().add(npc);
			if (script != null) {
				npc.Respawn(Universe.getClock());
			}
		}

	}

	public void SeedNPCs(Element Enode, boolean[][] grid) {

		// max population
		int min = Integer.parseInt(Enode.getAttribute("min"));
		// min population
		int max = Integer.parseInt(Enode.getAttribute("max"));
		// mindistance
		int mindistance = 0;
		if (Enode.getAttribute("minDistance").length() > 0) {
			mindistance = Integer.parseInt(Enode.getAttribute("minDistance"));
		}
		int number = 0;

		int count = min;
		if (min < max) {
			count += (Universe.m_random.nextInt(max - min));
		}

		Vec2f[] points = new Vec2f[count];
		// npc name
		String name = Enode.getAttribute("name");

		Document doc = ParserHelper.LoadXML("assets/data/npcs/" + name + ".xml");
		Element root = doc.getDocumentElement();
		Element n = (Element) doc.getFirstChild();
		NPC template = new NPC(n, new Vec2f(0, 0), name);

		ScriptPackage script = null;
		NodeList scripts = Enode.getElementsByTagName("script");
		if (scripts.getLength() > 0) {
			Element scriptElement = (Element) scripts.item(0);
			String death = null, spawn = null;
			if (scriptElement.getAttribute("death").length() > 0) {
				death = scriptElement.getAttribute("death");
			}
			if (scriptElement.getAttribute("spawn").length() > 0) {
				spawn = scriptElement.getAttribute("spawn");
			}
			script = new ScriptPackage(spawn, death);
		}

		while (number < count) {
			int x = Universe.m_random.nextInt(zone.getWidth());
			int y = Universe.m_random.nextInt(zone.getHeight());
			if (zone.getTiles()[x][y] != null && grid[x][y] == true) {

				if (zone.getTiles()[x][y].getDefinition().getMovement() == TileMovement.WALK
						&& zone.getTiles()[x][y].getWidgetObject() == null) {
					Vec2f p = new Vec2f(x, y);
					if (mindistance > 0 && crowdingCheck(points, p, mindistance)) {
						points[number] = p;
						NPC npc = new NPC(template, points[number]);
						npc.setScripts(script);
						zone.getActors().add(npc);

						npc.setCollisioninterface(zone);
						number++;
					} else {
						points[number] = p;
						NPC npc = new NPC(template, points[number]);
						npc.setScripts(script);
						zone.getActors().add(npc);

						npc.setCollisioninterface(zone);
						number++;
					}

				}
			}

		}

	}

	private boolean crowdingCheck(Vec2f[] points, Vec2f compare, int minDistance) {
		for (int i = 0; i < points.length; i++) {
			if (points[i] == null) {
				return true;
			}
			if (points[i].getDistance(compare) < minDistance) {
				return false;
			}
		}
		return true;
	}

	public void placeNPCPOI(Element enode, boolean[][] grid) {

		Vec2i p = pointsOfInterest.getNextPOI();
		String name = enode.getAttribute("name");

		Document doc = ParserHelper.LoadXML("assets/data/npcs/" + name + ".xml");
		Element root = doc.getDocumentElement();
		Element n = (Element) doc.getFirstChild();
		NPC npc = new NPC(n, new Vec2f(p.x, p.y), name);
		zone.getActors().add(npc);
		npc.setCollisioninterface(zone);
		zone.getTile(p.x, p.y).setActorInTile(npc);
	}
}
