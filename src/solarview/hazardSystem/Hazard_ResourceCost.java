package solarview.hazardSystem;

import java.util.Queue;

import org.w3c.dom.Element;

import nomad.universe.Universe;
import spaceship.Spaceship;

public class Hazard_ResourceCost extends Hazard_Base {

	private String resourceName, description;
	private int min, max;
	
	public Hazard_ResourceCost(Element e) {
		description=e.getTextContent();
		resourceName=e.getAttribute("resource");
		min=Integer.parseInt(e.getAttribute("min"));
		max=Integer.parseInt(e.getAttribute("max"));
	}

	private int calcDamage(Spaceship ship)
	{
		float minF=((float)min)/100;
		float maxF=((float)max)/100;
		float minP=ship.getShipStats().getResource(resourceName).getResourceAmount()*minF;
		float maxP=ship.getShipStats().getResource(resourceName).getResourceAmount()*maxF;
		float variance=Universe.m_random.nextFloat()*(maxP-minP);
		return (int) (variance+minP);	
	}
	
	@Override
	public void runHazard(Spaceship ship, Queue<String> queue) {
		if (ship.getShipStats().getResource(resourceName)==null)
		{
			return;
		}
		int damage=calcDamage(ship);

		ship.getShipStats().getResource(resourceName).subtractResourceAmount(damage);
		String dmg=Integer.toString(damage);
		queue.add(description.replace("VALUE", dmg));
	}

}
