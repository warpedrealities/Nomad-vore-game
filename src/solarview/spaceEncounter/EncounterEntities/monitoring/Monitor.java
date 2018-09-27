package solarview.spaceEncounter.EncounterEntities.monitoring;

public interface Monitor {

	void reportDamage(int damage);
	
	void reportShield(int damage);
	
	void reportAttack(boolean miss);
	public int getDamage();

	public int getShieldDamage();

	public int getHits();

	public int getMisses();
}
