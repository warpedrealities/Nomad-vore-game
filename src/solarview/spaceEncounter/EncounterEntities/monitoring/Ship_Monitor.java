package solarview.spaceEncounter.EncounterEntities.monitoring;

public class Ship_Monitor implements Monitor {

	private int damage,shieldDamage,hits,misses;
	
	@Override
	public void reportDamage(int damage) {
		this.damage+=damage;
	}

	@Override
	public void reportShield(int damage) {
		this.shieldDamage+=damage;
	}

	@Override
	public void reportAttack(boolean miss) {
		if (miss)
		{
			misses++;
		}
		else
		{
			hits++;
		}
	}

	public int getDamage() {
		return damage;
	}

	public int getShieldDamage() {
		return shieldDamage;
	}

	public int getHits() {
		return hits;
	}

	public int getMisses() {
		return misses;
	}

}
