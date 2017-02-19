package item;

import item.Item.ItemUse;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ItemKey extends Item {

	public ItemKey(Element enode, int id) {
		super(id);
		m_use=ItemUse.NONE;
		m_name=enode.getAttribute("name");
		m_weight=Float.parseFloat(enode.getAttribute("weight"));
		itemValue=Float.parseFloat(enode.getAttribute("value"));	
		NodeList children=enode.getChildNodes();
		for (int i=0;i<children.getLength();i++)
		{
			Node node=children.item(i);
			if (node.getNodeType()==Node.ELEMENT_NODE)
			{
				Element Enode=(Element)node;
				if (Enode.getTagName()=="description")
				{
					m_description=Enode.getTextContent().replace("\n", "");
				}
			}
		}
	}

}
