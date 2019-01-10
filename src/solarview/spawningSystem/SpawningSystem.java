package solarview.spawningSystem;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import entities.Entity;
import shared.ParserHelper;
import spaceship.Spaceship;

public class SpawningSystem {

	private Element spawnRules;
	private List<SpawnScript> scripts;
	private List<Entity> systemEntities;
	public SpawningSystem(String systemName) {
		Document doc = ParserHelper.LoadXML("assets/data/systems/" + systemName + ".xml");
		scripts=new ArrayList<SpawnScript>();
		Element n = (Element) doc.getFirstChild();
		NodeList children = n.getElementsByTagName("spawnRules");
		if (children.getLength()>0)
		{	
			spawnRules=(Element)children.item(0);
			children=spawnRules.getElementsByTagName("spawn");
			for (int i=0;i<children.getLength();i++)
			{
				Element e=(Element)children.item(i);
				scripts.add(new SpawnScript(e));
			}			
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

	public static List<Entity> buildEntities(ArrayList<Entity> entitiesInSystem) {

		List <Entity> entities=new ArrayList<Entity>();
		for (int i=0;i<entitiesInSystem.size();i++)
		{
			if (!Spaceship.class.isInstance(entitiesInSystem.get(i)))
			{	
				entities.add(entitiesInSystem.get(i));
			}
		}
		return entities;
	}
	
	public void run(ArrayList<Entity> entitiesInSystem) {
		systemEntities=SpawningSystem.buildEntities(entitiesInSystem);
		for (int i=0;i<scripts.size();i++)
		{
			scripts.get(i).run(entitiesInSystem,systemEntities);
		}
	}

}
