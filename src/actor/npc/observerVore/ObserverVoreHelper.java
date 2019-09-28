package actor.npc.observerVore;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actor.Actor;
import actor.npc.NPC;
import actor.npc.observerVore.impl.VoreScript_Impl;
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
				list.add(new ObserverVoreTarget(target, scene, pref));
			}
		}

		return list;
	}

	public static VoreScript checkForTargets(Vec2f position, List<ObserverVoreTarget> list) {
		if (list == null) {
			return null;
		}
		for (int i = 0; i < 8; i++) {
			Vec2f p = Geometry.getPos(i, position);
			Actor npc = ViewScene.m_interface.getSceneController().getActorInTile((int) p.x, (int) p.y, false);
			if (npc != null && !npc.getRPGHandler().getActive()) {
				ObserverVoreTarget target = getTarget(npc.getName(), list);
				if (target != null) {
					return new VoreScript_Impl(target.getScene(), (NPC) npc, null);
				}

			}
		}

		return null;
	}

	private static ObserverVoreTarget getTarget(String name, List<ObserverVoreTarget> list) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getTarget().equals(name) && prefCheck(list.get(i))) {
				return list.get(i);
			}
		}
		return null;
	}

	private static boolean prefCheck(ObserverVoreTarget observerVoreTarget) {
		return !Universe.getInstance().getPrefs().ForbiddenPref(observerVoreTarget.getPreference());
	}

}
