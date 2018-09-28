package description;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actor.player.Player;

public class BodyLoader {

	public static void loadBody(Element node, Player player) {
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {

			Node N = children.item(i);
			if (N.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) N;
				// run each step successively
				if (Enode.getTagName() == "bodypart") {
					BodyPart p = new BodyPart(Enode.getAttribute("partname"));
					player.getLook().addPart(p);
					loadPart(Enode, p);
				}
			}
		}
	}

	public static void loadPart(Element node, BodyPart part) {
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {

			Node N = children.item(i);
			if (N.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) N;
				// run each step successively
				if (Enode.getTagName() == "partvalue") {
					String str = Enode.getAttribute("variablename");
					int v = Integer.parseInt(Enode.getAttribute("value"));
					part.setValue(str, v);
				}
			}
		}
	}

}
