package actor.npc.observerVore;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actor.Actor;
import actor.npc.NPC;
import actor.npc.observerVore.impl.VoreScript_Impl;
import nomad.FlagField;
import nomad.universe.Universe;
import shared.Geometry;
import shared.Vec2f;
import view.ViewScene;

public class ObserverVoreHelper {

	public static List<ObserverVoreTarget> buildTargets(Element enode) {
		List<ObserverVoreTarget> list = new ArrayList<>();
		NodeList children = enode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node N = children.item(i);
			if (N.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) N;
				String target = e.getAttribute("target");
				String scene = e.getAttribute("scene");
				String pref = e.getAttribute("pref");
				ObserverVoreTarget t = new ObserverVoreTarget(target, scene, pref);
				if (e.getChildNodes().getLength() > 0) {
					addRequirements(t, e);
				}
				list.add(t);
			}
		}

		return list;
	}

	private static void addRequirements(ObserverVoreTarget target, Element element) {
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node N = children.item(i);
			if (N.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) N;
				String flag = e.getAttribute("flag");
				String evaluate = e.getAttribute("evaluate");
				int value = Integer.parseInt(e.getAttribute("value"));
				boolean global = e.getAttribute("global").equals("true") ? true : false;
				ObserverVoreReq req = new ObserverVoreReq(flag, evaluate, value, global);
				target.AddRequirement(req);
			}
		}
	}

	public static VoreScript checkForTargets(Vec2f position, List<ObserverVoreTarget> list, NPC origin) {
		if (list == null) {
			return null;
		}
		for (int i = 0; i < 8; i++) {
			Vec2f p = Geometry.getPos(i, position);
			Actor npc = ViewScene.m_interface.getSceneController().getActorInTile((int) p.x, (int) p.y, false);
			if (npc != null && !npc.getRPGHandler().getActive()) {
				ObserverVoreTarget target = getTarget(npc.getName(), list, origin);
				if (target != null) {
					return new VoreScript_Impl(target.getScene(), (NPC) npc, null);
				}

			}
		}

		return null;
	}

	private static ObserverVoreTarget getTarget(String name, List<ObserverVoreTarget> list, NPC npc) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getTarget().equals(name) && reqCheck(npc, list.get(i)) && prefCheck(list.get(i))) {
				return list.get(i);
			}
		}
		return null;
	}

	private static boolean reqCheck(NPC npc, ObserverVoreTarget observerVoreTarget) {
		if (observerVoreTarget.getRequirements().size() == 0) {
			return true;
		}
		FlagField local = npc.getFlags();
		FlagField global = Universe.getInstance().getPlayer().getFlags();

		for (int i = 0; i < observerVoreTarget.getRequirements().size(); i++) {
			ObserverVoreReq req = observerVoreTarget.getRequirements().get(i);
			int value = req.isGlobal() ? global.readFlag(req.getFlag()) : local.readFlag(req.getFlag());
			int comp = req.getValue();
			switch (req.getReq()) {
			case EQUALS:
				return value == comp;
			case GREATERTHAN:
				return value > comp;
			case LESSTHAN:
				return value < comp;
			}
		}
		return false;
	}

	private static boolean prefCheck(ObserverVoreTarget observerVoreTarget) {
		return !Universe.getInstance().getPrefs().ForbiddenPref(observerVoreTarget.getPreference());
	}

}
