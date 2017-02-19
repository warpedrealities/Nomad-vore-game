package item;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ItemWidget extends Item {

	private String widgetName;

	
	public String getWidgetName()
	{
		return widgetName;
	}
	
	public ItemWidget(Element Inode, int UID) {
		super(UID);
		NodeList children=Inode.getChildNodes();
		m_use=ItemUse.NONE;
		
		m_name=Inode.getAttribute("name");
		m_weight=Float.parseFloat(Inode.getAttribute("weight"));
		itemValue=Float.parseFloat(Inode.getAttribute("value"));
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
				if (Enode.getTagName()=="placeditem")
				{
					widgetName=Enode.getAttribute("widget");
				}
			}
		}
	}

}
