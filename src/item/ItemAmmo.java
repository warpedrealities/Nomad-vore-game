package item;

import item.Item.ItemUse;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actor.Inventory;
import actor.Modifier;

public class ItemAmmo extends Item implements ItemHasEnergy {

	ItemEnergy m_energy;

	public ItemAmmo(Element Inode, int uid)
	{
		super(uid);
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
				if (Enode.getTagName()=="energy")
				{	
					m_energy=new ItemEnergy(Enode);
				}
			}
		}	
	
	}	
	
	@Override
	public ItemEnergy getEnergy() {
		// TODO Auto-generated method stub
		return m_energy;
	}
	
}
