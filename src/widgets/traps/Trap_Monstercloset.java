package widgets.traps;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import actor.Actor;
import nomad.Universe;
import shared.ParserHelper;
import view.ViewScene;
import widgets.WidgetDoor;
import zone.Zone;

public class Trap_Monstercloset implements Trap_Effect {

	String lockID;
	int radius;
	
	public Trap_Monstercloset(Element enode) {
		NodeList n=enode.getElementsByTagName("doors");
		Element e=(Element)n.item(0);
		lockID=e.getAttribute("lock");
		radius=Integer.parseInt(e.getAttribute("radius"));
	}

	public Trap_Monstercloset(DataInputStream dstream) throws IOException {

		lockID=ParserHelper.LoadString(dstream);
		radius=dstream.readInt();
	}

	@Override
	public void trigger(Actor target) {
		int x=(int) target.getPosition().x;
		int y=(int) target.getPosition().y;
		openDoors(x,y);
		ViewScene.m_interface.DrawText("you've triggered a trap");
	}
	
	private void openDoors(int x, int y)
	{
		int minx=x-radius;
		int miny=y-radius;
		Zone zone=Universe.getInstance().getCurrentZone();
		for (int i=minx;i<minx+(radius*2);i++)
		{
			for (int j=miny;j<miny+(radius*2);j++)
			{
				if (i>0 && j>0 && i<zone.getWidth() && j<zone.getHeight())
				{
					if (zone.getTile(i, j)!=null)
					{
						if (zone.getTile(i, j).getWidgetObject()!=null)
						{
							if (WidgetDoor.class.isInstance(zone.getTile(i, j).getWidgetObject()))
							{
								WidgetDoor door=(WidgetDoor)zone.getTile(i, j).getWidgetObject();
								if (door.getLockKey().equals(lockID))
								{
									ViewScene.m_interface.RemoveWidget(door);
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {

		dstream.writeInt(1);
		ParserHelper.SaveString(dstream,lockID);
		dstream.writeInt(radius);
	}

}
