package solarview.hazardSystem;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import shared.ParserHelper;

public class HazardArrayBuilder {
	private NodeList nodeList;
	public HazardArrayBuilder(int i) {
		Document doc = ParserHelper.LoadXML("assets/data/systems/hazardLists/hazardList" + i + ".xml");
		Element root = doc.getDocumentElement();
		Element n = (Element) doc.getFirstChild();
		nodeList = n.getChildNodes();
	}

	public List<Hazard_Base> build() {
		List <Hazard_Base> list=new ArrayList<Hazard_Base>();
		for (int i=0;i<nodeList.getLength();i++)
		{
			if (nodeList.item(i).getNodeType()==Node.ELEMENT_NODE)
			{
				Element e=(Element)nodeList.item(i);
				if (e.getTagName().equals("resourceCost"))
				{
					list.add(new Hazard_ResourceCost(e));
				}
				if (e.getTagName().equals("injury"))
				{
					list.add(new Hazard_Injury(e));
				}
				if (e.getTagName().equals("invasion"))
				{
					list.add(new Hazard_Invasion(e));
				}
			}
		}
		return list;
	}

}
