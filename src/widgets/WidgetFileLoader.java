package widgets;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import shared.ParserHelper;
import shipsystem.WidgetNavConsole;
import shipsystem.WidgetSystem;
import worldgentools.LootTable;

public class WidgetFileLoader {

	static public Widget loadWidget(String filename)
	{
		Document doc=ParserHelper.LoadXML("assets/data/widgets/"+filename+".xml");
		Element root=doc.getDocumentElement();
	    Element n=(Element)doc.getFirstChild();	
		Widget widget=null;
		if (root.getTagName().contains("harvestable"))
		{
			widget=new WidgetHarvestable(n);	
		}
		if (root.getTagName().contains("breakable"))
		{
			widget=new WidgetBreakable(n);
		}
		if (root.getTagName().contains("craftingtable"))
		{
			widget=new WidgetCraftingTable(n);
		}
		if (root.getTagName().contains("container"))
		{
			widget=new WidgetContainer(n);
		}
		if (root.getTagName().contains("shipsystem"))
		{
			widget=new WidgetSystem(n);
		}
		if (root.getTagName().contains("navconsole"))
		{
			widget=new WidgetNavConsole(n);
		}
		if (root.getTagName().contains("accomodation"))
		{
			widget=new WidgetAccomodation(n);
		}
		return widget;
		
	}
	
	
}
