package nomad.universe.salvageShip;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import shared.ParserHelper;
import shipsystem.WidgetNavConsole;
import spaceship.Spaceship;
import widgets.WidgetSlot;
import zone.Zone;

public class SalvageShip {

	private boolean shipEligible(Spaceship ship)
	{
		boolean capableShip=ship.getUnusableState()==null;
		boolean hasNavigation=false;
		boolean noEnemies=true;
		boolean noController=ship.getShipController()!=null;
		
		Zone z=ship.getZone(0);
		for (int i=0;i<z.getActors().size();i++)
		{
			if (z.getActors().get(i).getActorFaction().getRelationship("player")<50)
			{
				noEnemies=false;
			}
		}
		
		for (int i=0;i<z.getWidth();i++)
		{
			for (int j=0;j<z.getHeight();j++)
			{
				if (z.getTile(i, j)!=null && z.getTile(i, j).getWidgetObject()!=null 
						&& WidgetSlot.class.isInstance(z.getTile(i, j).getWidgetObject()))
				{
					WidgetSlot ws=(WidgetSlot)z.getTile(i, j).getWidgetObject();
					if (ws.getWidget()!=null && WidgetNavConsole.class.isInstance(ws.getWidget()))
					{
						hasNavigation=true;
						break;
					}
				}
			}
		}
		if (capableShip && noController && noEnemies && hasNavigation)
		{
			return true;
		}
		return false;
	}
	
	public Spaceship salvageShip(String filename) throws IOException
	{
		File file=new File("saves/"+filename+"/"+"verse.sav");
		if (file.exists()==false)
		{
			
				file.createNewFile();
	
		}
		FileInputStream fstream=new FileInputStream(file);
		DataInputStream dstream=new DataInputStream(fstream);
		dstream.readInt();		
		dstream.readLong();
		dstream.readBoolean();
		dstream.readBoolean();
		String s=ParserHelper.LoadString(dstream);
		StubSystem stubSystem=new StubSystem();
		stubSystem.load(filename,s);
		String e=ParserHelper.LoadString(dstream);
		Spaceship ship=stubSystem.getShip(e);
		if (ship!=null)
		{
			if (shipEligible(ship))
			{
				return ship;
			}
		}
		
		return null;
	}
}
