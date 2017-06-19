package description;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actor.player.Player_LOOK;

public class Macro_Value extends Macro {

	Map<Integer,String> valueToString;
	
	public Macro_Value(Element node, String name) {
		valueToString=new HashMap<Integer,String>();
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
				if (Enode.getTagName()=="translate")
				{
					int v=Integer.parseInt(Enode.getAttribute("value"));
					String str=Enode.getAttribute("string");
					valueToString.put(v, str);
				}
			}
		}
	}

	@Override
	public String readMacro(Player_LOOK look) {
		BodyPart part=look.getPart(partName);
		if (part!=null)
		{
			int v=part.getValue(variableName);
			return valueToString.get(v);
		}
		return null;
	}

}
