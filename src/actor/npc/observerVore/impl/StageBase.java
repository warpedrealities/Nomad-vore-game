package actor.npc.observerVore.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.soap.Node;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import view.ViewScene;

public abstract class StageBase {
	protected int index;
	protected int size;
	
	protected List <String> strings;
	
	protected void build(Element node)
	{
		strings=new ArrayList<String>(8);
		NodeList nodes=node.getChildNodes();
		for (int i=0;i<nodes.getLength();i++)
		{	
			if (nodes.item(i).getNodeType()==Node.ELEMENT_NODE)
			{
				strings.add(nodes.item(i).getTextContent());
			}
		}
		index=0;
		size=strings.size();
	}
	
	protected void progress(boolean visible)
	{
		if (visible)
		{
			ViewScene.m_interface.DrawText(strings.get(index));
		}
		index++;
	}
}
