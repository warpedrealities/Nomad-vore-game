package zone.Environment;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.soap.Node;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import actor.player.Player;
import actorRPG.player.Player_RPG;
import nomad.universe.Universe;

public class EnvironmentalConditions {

	private EnvironmentModifiers modifiers;
	private List<EnvironmentalCondition> conditions;
	private int interval;
	private boolean dangerous;
	
	public EnvironmentalConditions()
	{
		interval=0;
		modifiers=new EnvironmentModifiers();
		conditions=new ArrayList<EnvironmentalCondition>();
	}
	
	public EnvironmentalConditions(Element element)
	{
		interval=0;
		modifiers=new EnvironmentModifiers();
		conditions=new ArrayList<EnvironmentalCondition>();
		
		NodeList children=element.getChildNodes();
		for (int i=0;i<children.getLength();i++)
		{
			if (children.item(i).getNodeType()==Node.ELEMENT_NODE)
			{
				Element e=(Element)children.item(i);
				if (e.getTagName().equals("damagingCondition"))
				{
					conditions.add(new DamagingCondition(e));
				}
				if (e.getTagName().equals("modifierCondition"))
				{
					conditions.add(new ModifierCondition(e));
				}
			}

		}
		
	}
	
	public void update(Player player)
	{
		dangerous=false;
		modifiers.reset();
		Player_RPG rpg=(Player_RPG)player.getRPG();
		for (int i=0;i<conditions.size();i++)
		{
			if (rpg.isConditionImmune(conditions.get(i).getIdentity()))
			{
				conditions.get(i).setActive(false);
			}
			else
			{
				conditions.get(i).setActive(true);
				conditions.get(i).modEnvironment(modifiers);
				if (conditions.get(i).getDangerous())
				{
					dangerous=true;
				}
			}
		}
	}
	
	public void run(Player player)
	{
		if (interval==10)
		{
			interval=0;
			for (int i=0;i<conditions.size();i++)
			{
				if (conditions.get(i).getActive())
				{
					conditions.get(i).run(player);
				}
			}			
		}
		else
		{
			interval++;
		}
	}
	
	public EnvironmentalConditions(DataInputStream dstream) throws IOException {
		conditions=new ArrayList<EnvironmentalCondition>();
		modifiers=new EnvironmentModifiers();
		int count=dstream.readInt();
		EnvironmentalConditionLoader loader=new EnvironmentalConditionLoader();
		for (int i=0;i<count;i++)
		{
			conditions.add(loader.loadCondition(dstream));
		}
	}

	public void save(DataOutputStream dstream) throws IOException {

		dstream.writeInt(conditions.size());
		for (int i=0;i<conditions.size();i++)
		{
			conditions.get(i).save(dstream);
		}
	}

	public float getMovementMultiplier() {
		return modifiers.movementModifier;
	}

	public float getVisionMultiplier()
	{
		return modifiers.visionModifier;
	}

	public boolean getDanger() {
		return dangerous;
	}

	public void removeCondition(String condition) {
		for (int i=0;i<conditions.size();i++)
		{
			if (conditions.get(i).getIdentity().equals(condition))
			{
				conditions.remove(i);
				break;
			}
		}
		update(Universe.getInstance().getPlayer());
	}
}
