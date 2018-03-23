package spaceship.stats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import actor.npc.NPC;
import actorRPG.Actor_RPG;
import nomad.universe.Universe;
import shipsystem.ShipConverter;
import spaceship.SpaceshipResource;

public class SpaceshipStats {

	ArrayList<NPC> crewList;
	int crewCapacity;
	private CrewStats crewStats;
	Map<String, SpaceshipResource> resources;
	ArrayList<ShipConverter> converters;
	float fuelEfficiency;
	float moveCost;
	float manouverability;
	float armour;
	float solar;
	private int FTL;	
	boolean looseItems;
	SpaceshipShield shield;
	List<SpaceshipWeapon> weapons;

	
	public SpaceshipStats() {
		resources = new HashMap<String, SpaceshipResource>();
		converters = new ArrayList<ShipConverter>();
		crewList = new ArrayList<NPC>();
		crewCapacity = 0;
		crewStats=new CrewStats();
		setInitialCrewStats();
	}
	
	private void setInitialCrewStats()
	{
		int piloting=Universe.getInstance().getPlayer().getRPG().getAttribute(Actor_RPG.NAVIGATION);
		int gunnery=Universe.getInstance().getPlayer().getRPG().getAttribute(Actor_RPG.GUNNERY);
		crewStats.setGunnery(gunnery);
		crewStats.setNavigation(piloting);
	}

	public ArrayList<NPC> getCrewList() {
		return crewList;
	}

	public void run() {
		for (int i = 0; i < converters.size(); i++) {
			if (converters.get(i).isActive()) {
				converters.get(i).run(this,false);
			}
		}
	}
	public void battleRrun() {
		for (int i = 0; i < converters.size(); i++) {
			if (converters.get(i).isActive()) {
				converters.get(i).run(this,true);
			}
		}
	}
	public int getCrewCapacity() {
		return crewCapacity;
	}

	public void setCrewCapacity(int crewCapacity) {
		this.crewCapacity = crewCapacity;
	}

	public int getCrewCount() {
		return crewList.size();
	}

	public void addCrew(NPC npc) {
		
		if (npc.getCrewSkill()!=null && npc.getActorFaction().getFilename().equals("player"))
		{
			crewStats.addCrewSkill(npc.getCrewSkill());
		}
		crewList.add(npc);
	}

	public void runConversions() {
		for (int i = 0; i < converters.size(); i++) {
			if (converters.get(i).isActive()) {
				converters.get(i).doConversion(this);
			}

		}
	}

	public void runDecompose() {
		for (int i = 0; i < converters.size(); i++) {
			if (converters.get(i).isActive()) {
				converters.get(i).setLastAccessTimestamp(Universe.getClock());
			}
		}
	}

	public void setFuelEfficiency(float thrustCost) {

		fuelEfficiency = thrustCost;
	}

	public float getFuelEfficiency() {
		return fuelEfficiency;
	}

	public void addConverter(ShipConverter converter) {
		converters.add(converter);
	}

	public void addResource(String name, float value, float max, boolean noncombat) {
		value = (float) (Math.round(value * 100d) / 100d);
		SpaceshipResource resource = resources.get(name);
		if (resource != null) {
			resource.setResourceAmount(value + resource.getResourceAmount());
			resource.setResourceCap(max + resource.getResourceCap());
		} else {
			resources.put(name, new SpaceshipResource(name, value, max,noncombat));
		}
	}

	public void subtractResource(String name, float value) {
		SpaceshipResource resource = resources.get(name);
		if (resource != null) {
			resource.setResourceAmount(resource.getResourceAmount() - value);
		}
	}

	public boolean isLooseItems() {
		return looseItems;
	}

	public void setLooseItems(boolean looseItems) {
		this.looseItems = looseItems;
	}

	public Iterator<SpaceshipResource> getIterator() {
		return resources.values().iterator();
	}

	public String[] getResourceKeys() {
		Set<String> keys = resources.keySet();
		String[] strings = new String[keys.size() + 1];
		Iterator<String> it = keys.iterator();
		for (int i = 0; i < keys.size(); i++) {
			strings[i] = it.next();
		}
		strings[keys.size()] = "FOOD";
		return strings;
	}

	public String[] getCombatResourceKeys() {
		Set<String> keys = resources.keySet();
		String[] strings = new String[keys.size()];
		Iterator<String> it = keys.iterator();
		int count=0;
		for (int i = 0; i < keys.size(); i++) {
			String str=it.next();
			if (!resources.get(str).isNonCombat())
			{
				strings[count] = str;
				count++;
			}

		}
		return Arrays.copyOfRange(strings, 0, count);
	}

	public SpaceshipResource getResource(String type) {
		if (type.equals("NOTHING"))
		{
			return new SpaceshipResource("NOTHING",100,100,true);
		}
		return resources.get(type);
	}

	public int getMoveCost() {
		return (int) moveCost;
	}

	public void setMoveCost(float moveCost) {
		this.moveCost = moveCost;
	}

	public float getManouverability() {
		return manouverability;
	}

	public void setManouverability(float evasion) {
		this.manouverability = evasion;
	}

	public float getArmour() {
		return armour;
	}

	public void setArmour(float armour) {
		this.armour = armour;
	}

	public float getSolar() {
		return solar;
	}

	public void setSolar(float solar) {
		this.solar = solar;
	}

	public ArrayList<ShipConverter> getConverters() {
		return converters;
	}

	public SpaceshipShield getShield() {
		return shield;
	}

	public void setShield(SpaceshipShield shield) {
		this.shield = shield;
	}

	public List<SpaceshipWeapon> getWeapons() {
		return weapons;
	}

	public void setWeapons(List<SpaceshipWeapon> weapons) {
		this.weapons = weapons;
	}

	public CrewStats getCrewStats() {
		return crewStats;
	}

	public void setCrewStats(CrewStats crewStats) {
		this.crewStats = crewStats;
	}

	public int getFTL() {
		return FTL;
	}

	public void setFTL(int fTL) {
		FTL = fTL;
	}

}
