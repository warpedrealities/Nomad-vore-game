package faction.violation;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import faction.Faction;
import faction.FactionLibrary;

public class Super_Rule extends FactionRule {

	String factionName;
	int dispositionModifier;
	
	public Super_Rule(Element node) {
		super(node);
		// TODO Auto-generated constructor stub
		NodeList children=node.getChildNodes();
		for (int i=0;i<children.getLength();i++)
		{
			if (children.item(i).getNodeType()==Node.ELEMENT_NODE)
			{
				Element child=(Element)children.item(i);
				if (child.getTagName().equals("factionRelationship"))
				{
					factionName=child.getAttribute("faction");
					dispositionModifier=Integer.parseInt(child.getAttribute("mod"));
				}
				
				
			}
			
		}
	}
	
	public void violationAction()
	{
		if (factionName!=null)
		{
			Faction faction=FactionLibrary.getInstance().getFaction(factionName);
			faction.modDisposition("player",dispositionModifier);
		}
	}

}
