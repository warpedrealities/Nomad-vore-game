package description;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actor.player.Player_LOOK;
import shared.ParserHelper;

public class AppearanceParser {

	public static ArrayList<String> parseAppearance(Player_LOOK look) {
		ArrayList<String> collatedStrings = new ArrayList<String>();

		Document doc = ParserHelper.LoadXML("assets/data/description/description.xml");
		Element root = doc.getDocumentElement();
		Element node = (Element) doc.getFirstChild();
		NodeList children = node.getChildNodes();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < children.getLength(); i++) {

			Node N = children.item(i);
			if (N.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) N;
				// run each step successively
				if (Enode.getTagName().equals("block")) {
					String str = parseBlock(Enode, look);
					if (str != null) {
						builder.append(str);
					}

				}
				if (Enode.getTagName().equals("pbreak")) {
					collatedStrings.add(builder.toString());
					builder = new StringBuilder();
				}
			}
		}
		collatedStrings.add(builder.toString());
		return collatedStrings;
	}

	private static String parseBlock(Element enode, Player_LOOK look) {

		String bodypart = enode.getAttribute("bodypart");
		// check if the body part exists for this block
		if (look.getPart(bodypart) != null) {
			StringBuilder builder = new StringBuilder();
			NodeList children = enode.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node N = children.item(i);
				if (N.getNodeType() == Node.ELEMENT_NODE) {
					Element EN = (Element) N;
					if (EN.getTagName().equals("macro")) {
						String str = MacroLibrary.getInstance().lookupMacro(look, EN.getAttribute("ID"));
						builder.append(str);
					}
					if (EN.getTagName().equals("value")) {
						String str = parseValue(EN, look, bodypart);
						builder.append(str);
					}
				}
				if (N.getNodeType() == Node.TEXT_NODE) {
					builder.append(N.getNodeValue().replace("\n", ""));
				}
			}

			return builder.toString();
		}
		return null;
	}

	private static String parseValue(Element enode, Player_LOOK look, String part) {
		int v = look.getPart(part).getValue(enode.getAttribute("ID"));
		if (enode.getAttribute("rem").length()>0) {
			v = v % Integer.parseInt(enode.getAttribute("rem"));
		}
		if (enode.getAttribute("div").length()>0) {
			v = v / Integer.parseInt(enode.getAttribute("div"));
		}
		return Integer.toString(v);
	}
}
