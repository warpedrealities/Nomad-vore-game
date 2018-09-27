package dialogue.random;

import javax.xml.soap.Node;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import nomad.universe.Universe;

public class Randomizer {

	String items[];

	public Randomizer(Element node) {
		NodeList children = node.getChildNodes();
		items = new String[Integer.parseInt(node.getAttribute("count"))];
		int index = 0;
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) children.item(i);
				items[index] = e.getTextContent();
				index++;
			}
		}
	}

	public String getRandom() {
		return items[Universe.m_random.nextInt(items.length - 1)];
	}
}
