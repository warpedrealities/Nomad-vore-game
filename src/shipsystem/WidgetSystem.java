package shipsystem;

import interactionscreens.ContainerScreen;
import interactionscreens.SystemScreen;
import item.Item;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import nomad.Universe;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actor.player.Player;
import shared.ParserHelper;
import shipsystem.ShipAbility.AbilityType;
import shipsystem.weapon.ShipWeapon;
import view.ViewScene;
import widgets.WidgetBreakable;

public class WidgetSystem extends WidgetBreakable {

	ArrayList<ShipAbility> systemAbilities;
	
	public WidgetSystem(Element node)
	{
		super(node);
		systemAbilities=new ArrayList<ShipAbility>();
		NodeList children=node.getChildNodes();
		
		for (int i=0;i<children.getLength();i++)
		{
			Node N=children.item(i);
			if (N.getNodeType()==Node.ELEMENT_NODE)
			{
				Element Enode=(Element)N;
				//run each step successively
				if (Enode.getTagName()=="shipResource")
				{
					systemAbilities.add(new ShipResource(Enode));
				}
				if (Enode.getTagName()=="shipModifier")
				{
					systemAbilities.add(new ShipModifier(Enode));
				}
				if (Enode.getTagName()=="shipConverter")
				{
					systemAbilities.add(new ShipConverter(Enode,m_name));
				}
				if (Enode.getTagName()=="shipDispenser")
				{
					systemAbilities.add(new ShipDispenser(Enode,m_name));
				}
				if (Enode.getTagName()=="shipWeapon")
				{
					systemAbilities.add(new ShipWeapon(Enode,m_name));
				}
				if (Enode.getTagName()=="shipShield")
				{
					systemAbilities.add(new ShipShield(Enode,m_name));
				}
			}
		}
	}
	
	@Override
	public boolean Interact(Player player)
	{
		
		ViewScene.m_interface.setScreen(new SystemScreen(this));
		
		return true;
	}
	
	@Override
	public
	void save(DataOutputStream dstream) throws IOException {
		dstream.write(8);
		commonSave(dstream);
		super.saveBreakable(dstream);
		
		dstream.writeInt(systemAbilities.size());
		for (int i=0;i<systemAbilities.size();i++)
		{
			int v=systemAbilities.get(i).getAbilityType().ordinal();
			dstream.writeInt(v);
			systemAbilities.get(i).save(dstream);
		}
		
	}
	
	public WidgetSystem(DataInputStream dstream) throws IOException {

		commonLoad(dstream);
		load(dstream);
		
		systemAbilities=new ArrayList<ShipAbility>();
		int count=dstream.readInt();
		for (int i=0;i<count;i++)
		{
			int v=dstream.readInt();
			AbilityType a=AbilityType.values()[v];
			switch (a)
			{
				case SA_RESOURCE:
				systemAbilities.add(new ShipResource(dstream));
				break;
				case SA_MODIFIER:
				systemAbilities.add(new ShipModifier(dstream));
				break;
				case SA_CONVERTER:
				systemAbilities.add(new ShipConverter(dstream,m_name));
				break;
				case SA_DISPENSER:
				systemAbilities.add(new ShipDispenser(dstream,m_name));
				break;
				case SA_WEAPON:
				systemAbilities.add(new ShipWeapon(dstream,m_name));
				break;
				case SA_SHIELD:
				systemAbilities.add(new ShipShield(dstream,m_name));
				break;
			}
			
		}
	}
	
	@Override
	public boolean safeOnly()
	{
		return true;
	}
	
	
	public ArrayList<ShipAbility> getShipAbilities()
	{
		return systemAbilities;
	}
	
	public void setResource(String name, float value)
	{
		for (int i=0;i<systemAbilities.size();i++)
		{
			if (systemAbilities.get(i).getAbilityType()==AbilityType.SA_RESOURCE)
			{
				ShipResource res=(ShipResource) systemAbilities.get(i);
				if (res.getContainsWhat().equals(name))
				{
					res.setAmountContained(value);
					break;
				}
				
				
			}
		}	
	}
}
