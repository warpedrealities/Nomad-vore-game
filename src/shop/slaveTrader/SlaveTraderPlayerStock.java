package shop.slaveTrader;

import java.util.List;

import widgets.Widget;
import widgets.WidgetCapture;
import widgets.WidgetSlot;
import zone.Zone;

public class SlaveTraderPlayerStock {

	private Zone shipZone;
	private List<SlaveStockItem> stock;
	
	public SlaveTraderPlayerStock(List<SlaveStockItem> list, Zone zone)
	{
		this.shipZone=zone;
		this.stock=list;
	}

	public Zone getShipZoneReference() {
		return shipZone;
	}

	public List<SlaveStockItem> getStock() {
		return stock;
	}

	public void removeCaptive(SlaveStockItem item) {
		for (int i=0;i<shipZone.getWidth();i++)
		{
			for (int j=0;j<shipZone.getHeight();j++)
			{
				if (shipZone.getTile(i, j)!=null && shipZone.getTile(i, j).getWidgetObject()!=null)
				{
					Widget w=shipZone.getTile(i, j).getWidgetObject();
					if (WidgetSlot.class.isInstance(w))
					{
						WidgetSlot ws=(WidgetSlot)w;
						if (ws.getWidget()!=null && 
								WidgetCapture.class.isInstance(ws.getWidget()))
						{
							WidgetCapture wc=(WidgetCapture)ws.getWidget();
							if (removeCaptive(item,wc))
							{
								break;
							}
						}
					}
					if (WidgetCapture.class.isInstance(w))
					{
						WidgetCapture wc=(WidgetCapture)w;		
						if (removeCaptive(item,wc))
						{
							break;
						}
						
					}
				}
			}
		}
		
		
		stock.remove(item);
	}

	private boolean removeCaptive(SlaveStockItem item, WidgetCapture wc) {
		for (int i=0;i<wc.getCapacity();i++)
		{
			if (wc.getNPC(i)!=null && wc.getNPC(i).equals(item.getNPC()))
			{
				wc.setNPC(null, i);
				return true;
			}
		}
		return false;
	}
	
}
