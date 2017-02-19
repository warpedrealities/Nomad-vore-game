package worldgentools;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PerlinFramework {

	
	public static void useFramework(boolean [][] grid, Element element, ZoneBuildTools buildTools)
	{
		PerlinTool tool=new PerlinTool(buildTools.m_width,buildTools.m_height);
		
		NodeList children=element.getChildNodes();
		for (int i=0;i<children.getLength();i++)
		{
			if (children.item(i).getNodeType()==Node.ELEMENT_NODE)
			{
				Element enode=(Element)children.item(i);
				if (enode.getTagName().equals("band"))
				{
					boolean [][] ngrid=tool.getGrid(grid, 
							Float.parseFloat(enode.getAttribute("low")), 
							Float.parseFloat(enode.getAttribute("high")));
					buildTools.NextPhase(enode, ngrid);
					
				}
			}
		}
		
		
	}
	
	
}
