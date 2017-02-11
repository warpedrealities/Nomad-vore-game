package research;

import java.util.Map;

import org.w3c.dom.Element;

public class Entry_Requirement {

	private String dataName;
	private int dataCount;
	
	public Entry_Requirement(Element element)
	{
		dataName=element.getAttribute("name");
		dataCount=Integer.parseInt(element.getAttribute("count"));
	}

	public String getDataName() {
		return dataName;
	}
	public int getDataCount() {
		return dataCount;
	}

	public boolean met(Map<String, Data> dataMap) {
		Data d=dataMap.get(dataName);
		if (d!=null)
		{
			if (d.getCount()>=dataCount)
			{
				return true;
			}
		}
		return false;
	}
	
	
	
	
	
}
