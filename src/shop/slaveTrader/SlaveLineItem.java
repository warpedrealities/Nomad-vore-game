package shop.slaveTrader;

import org.w3c.dom.Element;

public class SlaveLineItem {

	protected String slaveName;
	protected int value;
	
	public SlaveLineItem()
	{
		
	}
	
	public SlaveLineItem(Element enode) {
		slaveName=enode.getAttribute("name");
		value=Integer.parseInt(enode.getAttribute("value"));
	}

	public String getSlaveName() {
		return slaveName;
	}

	public int getValue() {
		return value;
	}
	
	

}
