package dialogue.evaluation;

import javax.xml.soap.Node;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import actor.player.Player_LOOK;
import shared.ParserHelper;

public class AppearanceEvaluator {

	private Player_LOOK look;
	
	public AppearanceEvaluator(Player_LOOK look) {
		this.look=look;
	}

	private boolean checkPart(Element e)
	{
		String bodypart=e.getAttribute("part");
		if (look.getPart(bodypart)!=null)
		{
			return true;
		}
		return false;
	}
	
	private boolean checkValue(Element e)
	{
		String bodypart=e.getAttribute("part");
		if (look.getPart(bodypart)!=null)
		{
			int v=look.getPart(bodypart).getValue(e.getAttribute("value"));
			if (e.getAttribute("equals").length()>0)
			{
				if (v==Integer.parseInt(e.getAttribute("equals")))
				{
					return true;
				}
			}
			if (e.getAttribute("greaterthan").length()>0)
			{
				if (v>=Integer.parseInt(e.getAttribute("greaterthan")))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean check(String file) {
		Document doc = ParserHelper.LoadXML("assets/data/conversations/likeness/" + file + ".xml");
		Element root = doc.getDocumentElement();
		Element n = (Element) doc.getFirstChild();
		NodeList nodes=n.getChildNodes();
		
		for (int i=0;i<nodes.getLength();i++)
		{
			if (nodes.item(i).getNodeType()==Node.ELEMENT_NODE)
			{
				Element e=(Element)nodes.item(i);
				if (e.getTagName().equals("hasPart"))
				{
					if (!checkPart(e))
					{
						return false;
					}
				}
				if (e.getTagName().equals("hasValues"))
				{
					if (!checkValue(e))
					{
						return false;
					}
				}
			}
		}
		return true;
	}

}
