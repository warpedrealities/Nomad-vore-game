package item;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class ItemContainer extends Item {

	private int capacity;
	private float weightRatio;
	private int containedTag;
	
	public ItemContainer(Element node, int uid) {
		super(uid);
		m_use=ItemUse.OPEN;
		NodeList children = node.getChildNodes();
		int l = children.getLength();
		m_name = node.getAttribute("name");
		m_weight = Float.parseFloat(node.getAttribute("weight"));
		itemValue = Float.parseFloat(node.getAttribute("value"));
		for (int i = 0; i < children.getLength(); i++) {
			Node n = children.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) n;
				String str = Enode.getTagName();
				if (Enode.getTagName() == "description") {
					m_description = Enode.getTextContent().replace("\n", "");
				}
				if (Enode.getTagName().equals("container"))
				{
					weightRatio=Float.parseFloat(Enode.getAttribute("ratio"));
					capacity=Integer.parseInt(Enode.getAttribute("capacity"));
					containedTag=Integer.parseInt(Enode.getAttribute("tag"));
				}
			}
		}
	}

	public int getCapacity() {
		return capacity;
	}

	public float getWeightRatio() {
		return weightRatio;
	}

	public int getContainedTag() {
		return containedTag;
	}
	
	@Override
	public boolean canStack() {
		return false;
	}

}
