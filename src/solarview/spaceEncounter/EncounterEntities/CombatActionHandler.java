package solarview.spaceEncounter.EncounterEntities;

import java.util.ArrayList;
import java.util.List;

import shipsystem.ShipResource;
import shipsystem.weapon.WeaponCost;
import solarview.spaceEncounter.effectHandling.EffectHandler;
import spaceship.SpaceshipResource;

public class CombatActionHandler {

	private List<CombatAction> actions;
	private EncounterShip ship;
	private float clock;
	
	public CombatActionHandler(EncounterShip ship)
	{
		clock=0;
		this.ship=ship;
		actions=new ArrayList<CombatAction>();
		
	}
	
	public List<CombatAction> getList()
	{
		return actions;
	}
	
	private boolean checkResources(CombatAction action)
	{
		for (int i=0;i<action.getWeapon().getWeapon().getWeapon().getWeaponCosts().size();i++)
		{
			WeaponCost wp=action.getWeapon().getWeapon().getWeapon().getWeaponCosts().get(i);
			if (ship.getShip().getShipStats().getResource(wp.getResourceName())==null)
			{
				return false;
			}
			float r=ship.getShip().getShipStats().getResource(wp.getResourceName()).getResourceAmount();
			if (wp.getCost()>r)
			{
				return false;
			}
		}		
		return true;
	}
	
	private void subtractResources(CombatAction action)
	{
		for (int i=0;i<action.getWeapon().getWeapon().getWeapon().getWeaponCosts().size();i++)
		{
			WeaponCost wp=action.getWeapon().getWeapon().getWeapon().getWeaponCosts().get(i);
			SpaceshipResource r=ship.getShip().getShipStats().getResource(wp.getResourceName());
			r.setResourceAmount(r.getResourceAmount()-wp.getCost());
		}
	}
	
	private void startEffect(CombatAction action, EffectHandler effectHandler)
	{
		//check resources
		if (!checkResources(action))
		{
			return;
		}
		
		//subtract resources
		subtractResources(action);
		
		//create effect
		effectHandler.addEffect(action);
		action.getWeapon().useWeapon();
	}
	
	public void update(float dt, EffectHandler effectHandler)
	{
		if (actions.size()>0)
		{
			clock-=dt;
			if (clock<=0)
			{
				startEffect(actions.get(0),effectHandler);
				actions.remove(0);
				clock=0.1F;
			}
		}
	}
}
