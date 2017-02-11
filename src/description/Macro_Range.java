package description;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actor.Player_LOOK;

public class Macro_Range extends Macro {

	List<Range_Bounds> listOfRanges;
	
	
	public Macro_Range(Element node,String name) {
		// TODO Auto-generated constructor stub
		listOfRanges=new ArrayList<Range_Bounds>();
		NodeList children=node.getChildNodes();
		macroName=name;
		partName=node.getAttribute("part");
		variableName=node.getAttribute("variable");
		for (int i=0;i<children.getLength();i++)
		{
		
			Node N=children.item(i);
			if (N.getNodeType()==Node.ELEMENT_NODE)
			{
				Element Enode=(Element)N;
				//run each step successively
				if (Enode.getTagName()=="range")
				{
					listOfRanges.add(new Range_Bounds(Enode));
				}
			}
		}
	}

	@Override
	public String readMacro(Player_LOOK look) {

		int v=0;
		if (look.getPart(partName)!=null)
		{
			v=look.getPart(partName).getValue(variableName);
		}
		for (int i=0;i<listOfRanges.size();i++)
		{
			if (listOfRanges.get(i).inBounds(v))
			{
				return listOfRanges.get(i).getString();
			}
			
		}
		return null;
	}

}
