package shipsystem.resourceConversion;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import shared.ParserHelper;

public class ResourceConverter {

	private Map<String,Integer> conversionMap;
	
	public ResourceConverter(String resourceName) {
		conversionMap=new HashMap<String,Integer>();
		Document doc = ParserHelper.LoadXML("assets/data/resourceConversion/" + resourceName+".xml");
		Element root = doc.getDocumentElement();
		Element n = (Element) doc.getFirstChild();
		NodeList children=n.getChildNodes();
		for (int i=0;i<children.getLength();i++)
		{
			if (children.item(i).getNodeType()==Node.ELEMENT_NODE)
			{
				Element e=(Element)children.item(i);
				conversionMap.put(e.getAttribute("item"), Integer.parseInt(e.getAttribute("value")));
			}
		}
	}

	public boolean canConvert(String itemName) {
		if (conversionMap.get(itemName)!=null)
		{
			return true;
		}
		return false;
	}

	public int conversionValue(String itemName) {
		return conversionMap.get(itemName);
	}

}
