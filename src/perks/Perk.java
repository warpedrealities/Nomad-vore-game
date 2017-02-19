package perks;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actorRPG.RPG_Helper;

public class Perk {

	
	private String name;

	private String [] requires;
	private int maxrank;
	private String description;
	private ArrayList <PerkElement> elements;
	
	public Perk(Element enode) {
		elements=new ArrayList<PerkElement>();
		name=enode.getAttribute("name");
		
		maxrank=1;
		NodeList children=enode.getChildNodes();
		for (int i=0;i<children.getLength();i++)
		{
			Node node=children.item(i);
			if (node.getNodeType()==Node.ELEMENT_NODE)
			{
				Element childnode=(Element)node;
				if (childnode.getTagName()=="description")
				{
					description=childnode.getTextContent().replace("\n", "");
				}
				if (childnode.getTagName()=="requirement")
				{
					requires=genRequirement(childnode);
				}
				if (childnode.getTagName()=="move")
				{
					elements.add(
							new PerkMove(childnode)		
									);
				}
				if (childnode.getTagName()=="moveModifier")
				{
					elements.add(
							new PerkMoveModifier(childnode)		
									);
				}
				if (childnode.getTagName()=="weaponMove")
				{
					elements.add(
							new PerkWeaponMove(childnode)		
									);
				}
				
				if (childnode.getTagName()=="modifier")
				{
					elements.add(
							new PerkModifier(
									childnode.getAttribute("affects"),
									Integer.parseInt(childnode.getAttribute("value"))
									,
									PerkModifier.PerkModType.valueOf(childnode.getAttribute("type"))));
				}
				if (childnode.getTagName()=="maxrank")
				{
					maxrank=Integer.parseInt(childnode.getAttribute("value"));
				}
			}
		}
	}
	
	private String [] genRequirement(Element node)
	{
		String str[]=new String[Integer.parseInt(node.getAttribute("count"))];
		NodeList children=node.getElementsByTagName("req");
		for (int i=0;i<children.getLength();i++)
		{
			str[i]=children.item(i).getTextContent();
		}
		return str;
	}

	public String getName() {
		return name;
	}

	public String [] getRequires() {
		return requires;
	}
	public int getMaxrank() {
		return maxrank;
	}
	public String getDescription() {
		return description;
	}
	
	public int getNumElements()
	{
		return elements.size();
	}
	
	public PerkElement getElement(int index)
	{
		return elements.get(index);
	}
}
