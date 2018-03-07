package actor;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actorRPG.Actor_RPG;
import actorRPG.RPG_Helper;

public class Modifier {

	List<Modifier_Element> modifiers;
	public Modifier(Element Mnode) {
		modifiers=new ArrayList<Modifier_Element>();
		NodeList children = Mnode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) node;
				String tag=Enode.getTagName();
				float value=Float.parseFloat(Enode.getAttribute("value"));
				buildModifier(tag.toUpperCase(),value);
			}
		}
	}
	private void buildModifier(String tag, float value) {
		// TODO Auto-generated method stub
		int index=-1;
		index=RPG_Helper.AttributefromString(tag);
		if (index!=-1)
		{
			modifiers.add(new Modifier_Element(0,index,value));
		}
		index=RPG_Helper.subAttributeFromString(tag);
		if (index!=-1)
		{
			modifiers.add(new Modifier_Element(1,index,value));		
		}
		if (index==-1)
		{
			System.err.print("error, invalid modifier "+tag);
		}
	}
	public int numModifiers()
	{
		return modifiers.size();
	}
	
	public Modifier_Element getModifier(int i)
	{
		return modifiers.get(i);
	}

}
