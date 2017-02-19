package dialogue;

import nomad.Universe;
import view.ViewScene;
import widgets.Widget;
import widgets.WidgetDoor;
import zone.Tile;
import zone.Zone;

public class DialogueHelper {

	static public void openDoor(String lock)
	{
		Zone zone=Universe.getInstance().getCurrentZone();
		int width=zone.getWidth();
		int height=zone.getHeight();
		
		for (int i=0;i<width;i++)
		{
			for (int j=0;j<height;j++)
			{
				Tile t=zone.getTile(i, j);
				if (t!=null)
				{
					Widget widget=t.getWidgetObject();
					if (widget!=null && WidgetDoor.class.isInstance(widget))
					{
						WidgetDoor wd=(WidgetDoor)widget;
						if (wd.getLockKey().equals(lock))
						{
							ViewScene.m_interface.RemoveWidget(wd);
							return;
						}
					}
				}
			}
		}
		
		
	}
	
	
	
	
}
