package spaceship.stats;

import java.util.List;

import nomad.universe.Universe;
import shipsystem.WidgetDamage;
import spaceship.Spaceship;
import zone.Tile;
import zone.TileDef.TileMovement;

public class SpaceshipDamageAnalyzer {

	private List <WidgetDamage> damages;
	private Spaceship ship;
	private int hullDamage, systemDamage, requiredDamage, maxHull;
	public SpaceshipDamageAnalyzer(List <WidgetDamage> damages, Spaceship ship, int hullDamage, int systemDamage, int requiredDamage, int maxHull)
	{
		this.damages=damages;
		this.ship=ship;
		this.hullDamage=hullDamage;
		this.systemDamage=systemDamage;
		this.requiredDamage=requiredDamage;
		this.maxHull=maxHull;
	}
	
	public void run()
	{
		if (systemDamage+hullDamage < requiredDamage) {
			// create another damage impact holder
			moreDamage();
		}
		if (systemDamage+hullDamage >requiredDamage)
		{
			lessDamage();
		}	
	}

	private void lessDamage() {
		int reductionAmount=requiredDamage-(systemDamage+hullDamage);
		while (reductionAmount>0) {
			if (systemDamage>0)
			{
				reductionAmount=removeSystemDamage(reductionAmount);
			}
			else
			{
				reductionAmount=removeHullDamage(reductionAmount);
			}
		}
		
	}

	private int removeHullDamage(int reductionAmount) {
		WidgetDamage damage=findDamage(true);
		if (reductionAmount<damage.getDamageValue())
		{
			damage.setDamageValue(damage.getDamageValue()-reductionAmount);
			hullDamage-=reductionAmount;
			reductionAmount=0;
		}
		else
		{
			reductionAmount-=damage.getDamageValue();
			hullDamage-=damage.getDamageValue();
			removeDamageWidget(damage);
		}
		return reductionAmount;
	}

	private int removeSystemDamage(int reductionAmount) {
		WidgetDamage damage=findDamage(false);
		if (reductionAmount<damage.getDamageValue())
		{
			damage.setDamageValue(damage.getDamageValue()-reductionAmount);
			systemDamage-=reductionAmount;
			reductionAmount=0;
		}
		else
		{
			reductionAmount-=damage.getDamageValue();
			systemDamage-=damage.getDamageValue();
			removeDamageWidget(damage);
		}
		return reductionAmount;
	}
	
	private void removeDamageWidget(WidgetDamage damage) {
		for (int i = 0; i < ship.getZone(0).getWidth(); i++) 
		{
			for (int j = 0; j < ship.getZone(0).getHeight(); j++) 
			{
				// check tile
				Tile t = ship.getZone(0).getTile(i, j);
				if (t != null && t.getWidgetObject() != null) 
				{
					if (t.getWidgetObject()==damage) 
					{					
						t.setWidget(null);
						damages.remove(damage);
						break;
					}
				}
			}
		}
	}

	private WidgetDamage findDamage(boolean exterior)
	{
		for (int i=0;i<damages.size();i++)
		{
			if (damages.get(i).getDamageValue()>0 && damages.get(i).isExterior()==exterior)
			{
				return damages.get(i);
			}
		}
		return null;
	}

	private int calcExteriorDamageRequired()
	{
		int total_req=0;
		if (requiredDamage<=maxHull/2) {
			total_req=requiredDamage;
		} else
		{
			total_req=maxHull/2;
		}
		
		if (total_req>hullDamage)
		{
			return total_req-hullDamage;
		}
		
		return 0;
	}
	
	private void moreDamage() {
		int externalDamage= calcExteriorDamageRequired();
		int systemDamage= calcInternalDamageRequired(externalDamage);
		if (externalDamage>0)
		{
			createDamage(externalDamage, true);
		}
		if (systemDamage>0)
		{
			createDamage(externalDamage, false);		
		}
		
	}

	private void createDamage(int damage, boolean exterior) {
		while (true) {
			int x = Universe.m_random.nextInt((int) ship.getSize().x);
			int y = Universe.m_random.nextInt((int) ship.getSize().y);
			if (ship.getZone(0).getTile(x, y) != null
					&& ship.getZone(0).getTile(x, y).getDefinition().getMovement() == TileMovement.WALK) {
				if (ship.getZone(0).getTile(x, y).getWidgetObject() != null) {
					if (WidgetDamage.class.isInstance(ship.getZone(0).getTile(x, y).getWidgetObject())) {
						WidgetDamage d = (WidgetDamage) ship.getZone(0).getTile(x, y).getWidgetObject();
						d.setDamageValue(damage + d.getDamageValue());
						return;
					}
				} else {
					ship.getZone(0).getTile(x, y).setWidget(new WidgetDamage(damage,exterior));
					return;
				}
			}
		}
	}

	private int calcInternalDamageRequired(int externalDamage) {
		int value=requiredDamage-externalDamage;
		if (value>systemDamage)
		{
			return value-systemDamage;
		}
		return 0;
	}
	
	
}
