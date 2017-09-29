package solarview.spawningSystem;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import nomad.Entity;
import nomad.Universe;
import shared.Geometry;
import shared.ParserHelper;
import shared.Vec2f;
import shared.Vec2i;
import spaceship.Spaceship;

public class SpawningSystem {

	private Element spawnRules;
	private List<Entity> systemEntities;
	public SpawningSystem(String systemName) {
		Document doc = ParserHelper.LoadXML("assets/data/systems/" + systemName + ".xml");
		Element n = (Element) doc.getFirstChild();
		NodeList children = n.getElementsByTagName("spawnRules");
		if (children.getLength()>0)
		{
			spawnRules=(Element)children.item(0);
		}
	}

	private boolean maxedOut(ArrayList<Entity> entities,String ship, int max)
	{
		int count=0;
		for (int i=0;i<entities.size();i++)
		{
			if (Spaceship.class.isInstance(entities.get(i)))
			{
				Spaceship s=(Spaceship)entities.get(i);
				if (s.getshipName().equals(ship))
				{
					count++;
				}
			}
		}
		if (count<max)
		{
			return false;
		}
		return true;
	}

	private void buildEntities(ArrayList<Entity> entitiesInSystem) {

		systemEntities=new ArrayList<Entity>();
		for (int i=0;i<entitiesInSystem.size();i++)
		{
			if (!Spaceship.class.isInstance(entitiesInSystem.get(i)))
			{	
				systemEntities.add(entitiesInSystem.get(i));
			}
		}
	}
	
	private Vec2f getPosition()
	{
		int r=0;
		if (systemEntities.size()>1)
		{
			r=Universe.getInstance().m_random.nextInt(systemEntities.size());
		}
		int d=Universe.getInstance().m_random.nextInt(8);
		return Geometry.getPos(d, systemEntities.get(r).getPosition());
	}
	
	private void spawn(String ship, ArrayList<Entity>entities)
	{
		//find a good spot to spawn it
		Vec2f p=getPosition();
		Spaceship spaceship=new Spaceship(ship,(int)p.x,(int)p.y,Spaceship.ShipState.SPACE);
		entities.add(spaceship);
	}
	
	private void runRule(Element element,ArrayList<Entity> entities)
	{
		int r=Universe.getInstance().m_random.nextInt(100);
		if (r<Integer.parseInt(element.getAttribute("chance")))
		{
			String ship=element.getAttribute("ship");
			int max=Integer.parseInt(element.getAttribute("max"));
			if (maxedOut(entities,ship,max))
			{
				spawn(ship,entities);
			}
		}
	}
	
	public void run(ArrayList<Entity> entitiesInSystem) {
		if (spawnRules!=null)
		{
			buildEntities(entitiesInSystem);
			NodeList children=spawnRules.getElementsByTagName("spawn");
			for (int i=0;i<children.getLength();i++)
			{
				Element e=(Element)children.item(i);
				runRule(e,entitiesInSystem);
			}
		}
	}

}
