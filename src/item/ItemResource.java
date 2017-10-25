package item;

import item.Item.ItemUse;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import combat.effect.Effect_Recover;

public class ItemResource extends Item {

	public ItemResource(Element Inode, int uid) {
		super(uid);
		NodeList children = Inode.getChildNodes();

		m_use = ItemUse.NONE;
		m_name = Inode.getAttribute("name");
		m_weight = Float.parseFloat(Inode.getAttribute("weight"));
		String str = Inode.getAttribute("value");
		itemValue = Float.parseFloat(str);

		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) node;
				if (Enode.getTagName() == "description") {
					m_description = Enode.getTextContent().replace("\n", "");
				}

			}
		}

	}
	
	@Override
	public boolean canStack() {
		return true;
	}	
}
