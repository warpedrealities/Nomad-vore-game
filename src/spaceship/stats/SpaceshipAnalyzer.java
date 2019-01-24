package spaceship.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import actor.Actor;
import actor.npc.NPC;
import shipsystem.ShipAbility;
import shipsystem.ShipAbility.AbilityType;
import shipsystem.ShipFTL;
import shipsystem.ShipModifier;
import shipsystem.ShipResource;
import shipsystem.ShipShield;
import shipsystem.ShipSimCrew;
import shipsystem.WidgetDamage;
import shipsystem.WidgetSystem;
import shipsystem.WidgetSystem.SystemType;
import shipsystem.conversionSystem.ShipConverter;
import shipsystem.weapon.ShipWeapon;
import spaceship.Spaceship;
import spaceship.SpaceshipResource;
import widgets.WidgetAccomodation;
import widgets.WidgetSlot;
import zone.Tile;

public class SpaceshipAnalyzer {

	public SpaceshipStats generateStats(Spaceship ship) {
		if (ship.getBaseStats()==null)
		{
			ship.Generate();
		}
		List<ShipShield> shields = new ArrayList<ShipShield>();
		List<SpaceshipWeapon> weapons = new ArrayList<SpaceshipWeapon>();
		ArrayList<Integer> uids = new ArrayList<Integer>();

		SpaceshipStats stats = new SpaceshipStats();
		if (ship.getBaseStats().getThrustCost() == 0) {
			ship.generateStats();
		}
		stats.setFuelEfficiency(ship.getBaseStats().getThrustCost());
		stats.setArmour(ship.getBaseStats().getArmour());
		stats.setManouverability(ship.getBaseStats().getManouverability());
		stats.setMoveCost(ship.getBaseStats().getMoveCost());
		stats.addResource("HULL", ship.getBaseStats().getMaxHullPoints(), ship.getBaseStats().getMaxHullPoints(),false);

		int emitterIndex = 0;

		int driveCount=0;
		int ftl=0;
		for (int j = ship.getZone(0).getHeight() - 1; j >= 0; j--) {
			for (int i = 0; i < ship.getZone(0).getWidth(); i++) {

				// check tile
				Tile t = ship.getZone(0).getTile(i, j);
				if (t != null && t.getWidgetObject() != null) {
					if (t.getWidgetObject().getClass().getName().contains("WidgetAccomodation")) {
						WidgetAccomodation wa = (WidgetAccomodation) t.getWidgetObject();
						stats.setCrewCapacity(stats.getCrewCapacity() + wa.getCapacity());
					}
					if (t.getWidgetObject().getClass().getName().contains("WidgetItemPile")) {
						stats.setLooseItems(true);
					}
					if (t.getWidgetObject().getClass().getName().contains("WidgetSlot")) {
						WidgetSlot ws = (WidgetSlot) t.getWidgetObject();

						if (ws.getWidget() != null) {
							if (ws.getWidget().getClass().getName().contains("WidgetAccomodation")) {
								WidgetAccomodation wa = (WidgetAccomodation) ws.getWidget();
								stats.setCrewCapacity(stats.getCrewCapacity() + wa.getCapacity());
							}
							if (ws.getWidget().getClass().getName().contains("WidgetSystem")) {
								WidgetSystem system = (WidgetSystem) ws.getWidget();

								for (int k = 0; k < system.getShipAbilities().size(); k++) {
									ShipAbility.AbilityType type = system.getShipAbilities().get(k).getAbilityType();

									switch (type) {
									case SA_RESOURCE:
										ShipResource res = (ShipResource) system.getShipAbilities().get(k);
										stats.addResource(res.getContainsWhat(), res.getAmountContained(),
												res.getContainedCapacity(),res.isNonCombat());
										break;

									case SA_CONVERTER:
										ShipConverter con = (ShipConverter) system.getShipAbilities().get(k);

										stats.addConverter(con);
										break;
									case SA_MODIFIER:
										ShipModifier mod = (ShipModifier) system.getShipAbilities().get(k);
										useShipMod(stats, mod, uids);
										break;
									case SA_SHIELD:
										shields.add((ShipShield) system.getShipAbilities().get(k));
										break;
									case SA_WEAPON:
										weapons.add(new SpaceshipWeapon((ShipWeapon) system.getShipAbilities().get(k),
												emitterIndex, ws.getFacing()));
										break;
									case SA_FTL:
										ShipFTL drive=(ShipFTL)system.getShipAbilities().get(k);

										int f=drive.getFTL()-driveCount;
										if (f<0)
										{
											f=0;
										}
										ftl+=f;
										driveCount++;
										break;
									case SA_CREW:
										ShipSimCrew sim=(ShipSimCrew)system.getShipAbilities().get(k);
										stats.getCrewStats().addCrewSkill(sim.getCrew());

										break;
									default:
										break;
									}

								}
							}
							if (ws.getType() == SystemType.HARDPOINT) {
								emitterIndex++;
							}
						}
					}
					if (t.getWidgetObject().getClass().getName().contains("WidgetDamage")) {
						WidgetDamage damage = (WidgetDamage) t.getWidgetObject();
						stats.subtractResource("HULL", damage.getDamageValue());
					}
				}

			}
		}

		for (int i = 0; i < ship.getZone(0).getActors().size(); i++) {
			Actor actor = ship.getZone(0).getActors().get(i);
			if (ship.getZone(0).contains((int)actor.getPosition().x, (int)actor.getPosition().y) &&
					NPC.class.isInstance(actor) && actor.getRPGHandler().getActive()) {
				stats.addCrew((NPC) actor);
			}
		}
		if (shields.size() > 0) {
			stats.setShield(new SpaceshipShield(shields));
		}
		stats.setWeapons(weapons);
		if (ftl>0)
		{
			stats.setFTL(ftl);
		}


		return stats;
	}

	private void useShipMod(SpaceshipStats stats, ShipModifier mod, ArrayList<Integer> uids) {
		if (mod.getUid() != 0) {
			for (int i = 0; i < uids.size(); i++) {
				if (uids.get(i).equals(mod.getUid())) {
					return;
				}
			}

		}
		switch (mod.getModifies()) {
		case ShipModifier.FUEL_EFFICIENCY:
			stats.setFuelEfficiency(stats.getFuelEfficiency() * mod.getModification());
			break;
		case ShipModifier.MANOUVERABILITY:
			stats.setManouverability(stats.getManouverability() + mod.getModification());
			break;
		case ShipModifier.ARMOUR:
			stats.setArmour(stats.getArmour() + mod.getModification());
			break;
		case ShipModifier.SPEED:
			stats.setMoveCost(stats.getMoveCost() * mod.getModification());
			break;
		}
	}

	public void decomposeResources(SpaceshipStats stats, Spaceship ship) {
		// get all resources
		ArrayList<SpaceshipResource> resources = new ArrayList<SpaceshipResource>();
		Iterator<SpaceshipResource> it = stats.getIterator();
		while (it.hasNext()) {
			resources.add(it.next());
		}
		// associate ship resources
		Map<String, ArrayList<ShipResource>> shipResources = new HashMap<String, ArrayList<ShipResource>>();

		for (int i = 0; i < resources.size(); i++) {
			if (!resources.get(i).getResourceName().equals("HULL")) {
				shipResources.put(resources.get(i).getResourceName(), new ArrayList<ShipResource>());
			}
		}
		// get all hull damage
		ArrayList<WidgetDamage> damage = new ArrayList<WidgetDamage>();
		int hullDamage = 0; int systemDamage= 0;
		int statDamage = (int) ((int) stats.getResource("HULL").getResourceCap()
				- stats.getResource("HULL").getResourceAmount());
		for (int i = 0; i < ship.getZone(0).getWidth(); i++) {
			for (int j = 0; j < ship.getZone(0).getHeight(); j++) {
				// check tile
				Tile t = ship.getZone(0).getTile(i, j);
				if (t != null && t.getWidgetObject() != null) {
					if (t.getWidgetObject().getClass().getName().contains("WidgetItemPile")) {
						stats.setLooseItems(true);
					}
					if (t.getWidgetObject().getClass().getName().contains("WidgetSystem")) {
						WidgetSystem system = (WidgetSystem) t.getWidgetObject();

						for (int k = 0; k < system.getShipAbilities().size(); k++) {
							ShipAbility.AbilityType type = system.getShipAbilities().get(k).getAbilityType();
							if (type == AbilityType.SA_RESOURCE) {
								ShipResource res = (ShipResource) system.getShipAbilities().get(k);
								shipResources.get(res.getContainsWhat()).add(res);
							}
						}
					}
					if (t.getWidgetObject().getClass().getName().contains("WidgetSlot")) {
						WidgetSlot ws = (WidgetSlot) t.getWidgetObject();
						if (ws.getWidget() != null) {
							if (ws.getWidget().getClass().getName().contains("WidgetSystem")) {
								WidgetSystem system = (WidgetSystem) ws.getWidget();

								for (int k = 0; k < system.getShipAbilities().size(); k++) {
									ShipAbility.AbilityType type = system.getShipAbilities().get(k).getAbilityType();
									if (type == AbilityType.SA_RESOURCE) {
										ShipResource res = (ShipResource) system.getShipAbilities().get(k);
										shipResources.get(res.getContainsWhat()).add(res);
									}
								}
							}
						}
					}
					if (t.getWidgetObject().getClass().getName().contains("WidgetDamage")) {
						WidgetDamage d=(WidgetDamage) t.getWidgetObject();
						damage.add(d);
						if (d.isExterior()) {
							hullDamage += d.getDamageValue();
						}
						else
						{
							systemDamage += d.getDamageValue();
						}
						//	accounteddamage += ((WidgetDamage) t.getWidgetObject()).getDamageValue();
					}
				}

			}
		}
		new SpaceshipDamageAnalyzer(damage,ship,hullDamage,systemDamage,statDamage,(int)stats.getResource("HULL").getResourceCap()).run();

		// convey changes one to the other
		for (int i = 0; i < resources.size(); i++) {
			if (!resources.get(i).getResourceName().equals("HULL")) {
				float v = resources.get(i).getResourceAmount();
				ArrayList<ShipResource> shipres = shipResources.get(resources.get(i).getResourceName());

				for (int j = 0; j < shipres.size(); j++) {
					if (shipres.get(j).getContainedCapacity() <= v) {
						v -= shipres.get(j).getContainedCapacity();
						shipres.get(j).setAmountContained(shipres.get(j).getContainedCapacity());
					} else {
						shipres.get(j).setAmountContained(v);
						v=0;
					}
				}
			}
		}
	}


	public int getNumSystems(Spaceship ship) {
		int count = 0;
		for (int i = 0; i < ship.getZone(0).getWidth(); i++) {
			for (int j = 0; j < ship.getZone(0).getHeight(); j++) {
				// check tile
				Tile t = ship.getZone(0).getTile(i, j);
				if (t != null && t.getWidgetObject() != null) {
					if (!t.getWidgetObject().getClass().getName().contains("WidgetItemPile")) {
						count++;
					}
				}

			}
		}

		return count;
	}
}
