package solarview.hazardSystem;

import java.util.Queue;

import javax.xml.soap.Node;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import actorRPG.Actor_RPG;
import nomad.GameOver;
import nomad.universe.Universe;
import spaceship.Spaceship;
import vmo.Game;

public class Hazard_Injury extends Hazard_Base {

	private String description, gameover;
	private int min, max;
	public Hazard_Injury(Element e) {
		min=Integer.parseInt(e.getAttribute("min"));
		max=Integer.parseInt(e.getAttribute("max"));
		NodeList n=e.getChildNodes();
		for (int i=0;i<n.getLength();i++)
		{
			if (n.item(i).getNodeType()==Node.ELEMENT_NODE)
			{
				Element element=(Element)n.item(i);
				if (element.getTagName().equals("description"))
				{
					description=element.getTextContent();
				}
				if (element.getTagName().equals("gameover"))
				{
					gameover=element.getTextContent();
				}
			}
		}
	}
	@Override
	public void runHazard(Spaceship ship, Queue<String> queue) {
		int damage=min+Universe.m_random.nextInt(max-min);
		
		Universe.getInstance().getPlayer().getRPG().ReduceStat(Actor_RPG.HEALTH, damage);
		if (Universe.getInstance().getPlayer().getRPG().getStat(Actor_RPG.HEALTH)<=0)
		{
			Game.sceneManager.SwapScene(new GameOver(gameover));
		}
		else
		{
			String dmg=Integer.toString(damage);
			queue.add(description.replace("VALUE", dmg));
		}
		
		
	}

}
