package solarview.hazardSystem;

import java.util.Queue;

import javax.xml.soap.Node;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import spaceship.Spaceship;
import spaceship.boarding.BoardingHelper;
import spaceship.stats.SpaceshipAnalyzer;
import view.ViewScene;
import vmo.Game;

public class Hazard_Invasion extends Hazard_Base {
	
	private String description, npcFilenames[];
		
	public Hazard_Invasion(Element e) {
		NodeList n=e.getChildNodes();
		
		int count=Integer.parseInt(e.getAttribute("count"));
		int index=0;
		npcFilenames=new String[count];
		
		for (int i=0;i<n.getLength();i++)
		{
			if (n.item(i).getNodeType()==Node.ELEMENT_NODE)
			{
				Element element=(Element)n.item(i);
				if (element.getTagName().equals("npc"))
				{
					npcFilenames[index]=element.getAttribute("file");
					index++;
				}
				if (element.getTagName().equals("description"))
				{
					description=element.getTextContent();
				}
			}
		}
	}

	@Override
	public void runHazard(Spaceship ship, Queue<String> queue) {
		
		new BoardingHelper(ship).addNPCs(npcFilenames);

		if (ship.getShipStats()!=null)
		{
			new SpaceshipAnalyzer().decomposeResources(ship.getShipStats(), ship);
			ship.setShipStats(null);
			ship.setShipController(null);			
		}
		Game.sceneManager.SwapScene(new ViewScene());
		ViewScene.m_interface.DrawText(description);
	}

}
