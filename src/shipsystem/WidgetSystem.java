package shipsystem;

import interactionscreens.ContainerScreen;
import interactionscreens.systemScreen.SystemScreen;
import item.Item;
import nomad.universe.Universe;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actor.player.Player;
import shared.ParserHelper;
import shipsystem.ShipAbility.AbilityType;
import shipsystem.conversionSystem.ShipConverter;
import shipsystem.droneSpawning.ShipDroneSystem;
import shipsystem.weapon.ShipWeapon;
import view.ViewScene;
import widgets.WidgetBreakable;
import widgets.WidgetItemPile;

public class WidgetSystem extends WidgetBreakable {

	private ArrayList<ShipAbility> systemAbilities;
	private boolean hardpoint;
	
	public WidgetSystem(Element node) {
		super(node);
		if ("true".equals(node.getAttribute("hardpoint")))
		{
			hardpoint=true;
		}
		systemAbilities = new ArrayList<ShipAbility>();
		NodeList children = node.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			Node N = children.item(i);
			if (N.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) N;
				// run each step successively
				if (Enode.getTagName() == "shipResource") {
					systemAbilities.add(new ShipResource(Enode));
				}
				if (Enode.getTagName() == "shipModifier") {
					systemAbilities.add(new ShipModifier(Enode));
				}
				if (Enode.getTagName() == "shipConverter") {
					systemAbilities.add(new ShipConverter(Enode, m_name));
				}
				if (Enode.getTagName() == "shipDispenser") {
					systemAbilities.add(new ShipDispenser(Enode, m_name));
				}
				if (Enode.getTagName() == "shipWeapon") {
					systemAbilities.add(new ShipWeapon(Enode, m_name));
				}
				if (Enode.getTagName() == "shipShield") {
					systemAbilities.add(new ShipShield(Enode, m_name));
				}
				if (Enode.getTagName() == "shipFTL") {
					systemAbilities.add(new ShipFTL(Enode, m_name));
				}
				if (Enode.getTagName() == "shipSimCrew") {
					systemAbilities.add(new ShipSimCrew(Enode, m_name));
				}
				if (Enode.getTagName() == "shipDroneSystem") {
					systemAbilities.add(new ShipDroneSystem(Enode, m_name));
				}
			}
		}
	}

	@Override
	public boolean Interact(Player player) {

		ViewScene.m_interface.setScreen(new SystemScreen(this));

		return true;
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		dstream.write(8);
		commonSave(dstream);
		super.saveBreakable(dstream);
		dstream.writeBoolean(hardpoint);
		dstream.writeInt(systemAbilities.size());
		for (int i = 0; i < systemAbilities.size(); i++) {
			int v = systemAbilities.get(i).getAbilityType().ordinal();
			dstream.writeInt(v);
			systemAbilities.get(i).save(dstream);
		}

	}

	public WidgetSystem(DataInputStream dstream) throws IOException {

		commonLoad(dstream);
		load(dstream);
		hardpoint=dstream.readBoolean();
		systemAbilities = new ArrayList<ShipAbility>();
		int count = dstream.readInt();
		for (int i = 0; i < count; i++) {
			int v = dstream.readInt();
			AbilityType a = AbilityType.values()[v];
			switch (a) {
			case SA_RESOURCE:
				systemAbilities.add(new ShipResource(dstream));
				break;
			case SA_MODIFIER:
				systemAbilities.add(new ShipModifier(dstream));
				break;
			case SA_CONVERTER:
				systemAbilities.add(new ShipConverter(dstream, m_name));
				break;
			case SA_DISPENSER:
				systemAbilities.add(new ShipDispenser(dstream, m_name));
				break;
			case SA_WEAPON:
				systemAbilities.add(new ShipWeapon(dstream, m_name));
				break;
			case SA_SHIELD:
				systemAbilities.add(new ShipShield(dstream, m_name));
				break;
			case SA_FTL:
				systemAbilities.add(new ShipFTL(dstream, m_name));		
				break;
			case SA_CREW:
				systemAbilities.add(new ShipSimCrew(dstream, m_name));			
				break;
			case SA_SPAWNER:
				systemAbilities.add(new ShipDroneSystem(dstream, m_name));
				break;
			}

		}
	}

	@Override
	public boolean safeOnly() {
		return true;
	}

	public ArrayList<ShipAbility> getShipAbilities() {
		return systemAbilities;
	}

	public void setResource(String name, float value) {
		for (int i = 0; i < systemAbilities.size(); i++) {
			if (systemAbilities.get(i).getAbilityType() == AbilityType.SA_RESOURCE) {
				ShipResource res = (ShipResource) systemAbilities.get(i);
				if (res.getContainsWhat().equals(name)) {
					res.setAmountContained(value);
					break;
				}

			}
		}
	}

	public boolean isHardPoint() {
		return hardpoint;
	}
	
	@Override
	protected void destroy()
	{
		if (slottedWidget == false) {
			if (m_contains != null) {
				WidgetItemPile Pile = new WidgetItemPile(2, "a pile of items containing ", m_contains[0]);
				if (m_contains.length > 1) {
					for (int j = 1; j < m_contains.length; j++) {
						Pile.AddItem(m_contains[j]);
					}					
				}
				for (int i=0;i<systemAbilities.size();i++)
				{
					if (ShipResource.class.isInstance(systemAbilities.get(i)))
					{
						ShipResource sr=(ShipResource)systemAbilities.get(i);
						sr.extractResources(Pile,false);
					}
				}	
				ViewScene.m_interface.ReplaceWidget(this, Pile);
			} else {
				ViewScene.m_interface.RemoveWidget(this);
			}
		}	
		
		
	}
	
	public void handleDismantle(WidgetItemPile pile) {
		for (int i=0;i<systemAbilities.size();i++)
		{
			if (ShipResource.class.isInstance(systemAbilities.get(i)))
			{
				ShipResource sr=(ShipResource)systemAbilities.get(i);
				sr.extractResources(pile,true);		
			}
		}
	}	
}
