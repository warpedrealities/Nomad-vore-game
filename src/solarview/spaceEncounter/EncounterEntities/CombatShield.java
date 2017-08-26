package solarview.spaceEncounter.EncounterEntities;

import shared.Vec2f;
import solarview.spaceEncounter.EncounterEntities.monitoring.Monitor;
import solarview.spaceEncounter.effectHandling.EffectHandler;
import solarview.spaceEncounter.effectHandling.EffectHandler_Interface;
import solarview.spaceEncounter.effectHandling.effects.EffectSprite;
import spaceship.Spaceship;
import spaceship.stats.SpaceshipShield;

public class CombatShield {

	public enum ShieldStatus {
		OFF, ON, STARTUP, COOLDOWN, FASTCHARGE
	};

	private Spaceship ship;
	private int hitpoints, cooldown;

	private ShieldStatus status;

	private SpaceshipShield shield;

	public CombatShield(Spaceship ship, SpaceshipShield shield) {
		this.ship = ship;
		this.shield = shield;
		status = ShieldStatus.OFF;
	}

	public void toggleStatus() {
		switch (status) {
		case ON:
			shutdown();
			break;
		case FASTCHARGE:
			shutdown();
			break;
		case OFF:
			if (hasEnergy()) {
				status = ShieldStatus.STARTUP;
			}
			break;
		}
	}

	public ShieldStatus getStatus() {
		return status;
	}

	public int getHitpoints() {
		return hitpoints;
	}

	public int getCooldown() {
		return cooldown;
	}

	private int weakenShield(int damage) {
		if (damage >= shield.getAbsorption()) {
			hitpoints -= shield.getAbsorption();
			damage -= shield.getAbsorption();
		} else {
			hitpoints -= damage;
			damage = 0;
		}
		return damage;
	}

	public int applyDefence(int damage, int disruption,EffectHandler_Interface effectHandler,EncounterShip ship) {
		if (status == ShieldStatus.FASTCHARGE) {
			status = ShieldStatus.ON;
		}
		int h=hitpoints;
		damage = weakenShield(damage);
		hitpoints -= disruption;

		if (damage < 0) {
			damage = 0;
		}
		if (hitpoints <= 0) {
			EffectSprite e=new EffectSprite(ship.getPosition(),"shield.png",8,4,false);
			e.setSize(2);
			effectHandler.addEffect(e);
			shutdown();
		}
		else
		{
			EffectSprite e=new EffectSprite(ship.getPosition(),"shield.png",4,4,false);
			e.setSize(2);
			effectHandler.addEffect(e);		
		}
		String s=Integer.toString(h-hitpoints);
		ship.getMonitor().reportShield(h-hitpoints);	
		effectHandler.drawText(ship.getPosition().replicate(),s, 1);
		return damage;
	}

	public boolean isActive() {
		if (status == ShieldStatus.FASTCHARGE || status == ShieldStatus.ON) {
			return true;
		}
		return false;
	}

	private void shutdown() {
		status = ShieldStatus.COOLDOWN;
		hitpoints = 0;
		cooldown = shield.getRestartTime();
	}

	private boolean hasEnergy() {
		float energy = ship.getShipStats().getResource("ENERGY").getResourceAmount();
		if (energy >= shield.getEnergyCost()) {
			return true;
		}
		return false;
	}

	public void update(EffectHandler effectHandler, Vec2f p) {
		if (isActive()) {

			if (status == ShieldStatus.FASTCHARGE) {
				hitpoints += 10;
				if (hitpoints > shield.getHitpoints()) {
					status = ShieldStatus.ON;
				}
			}
			hitpoints += shield.getRegeneration();
			if (hitpoints > shield.getHitpoints()) {
				hitpoints = shield.getHitpoints();
			}

			if (hasEnergy()) {
				ship.getShipStats().getResource("ENERGY").setResourceAmount(
						ship.getShipStats().getResource("ENERGY").getResourceAmount() - shield.getEnergyCost());
			} else {
				shutdown();
			}
		} else if (status == ShieldStatus.COOLDOWN) {
			cooldown--;
			if (cooldown == 0) {
				status = ShieldStatus.OFF;
			}
		}
		else if (status==ShieldStatus.STARTUP)
		{
			EffectSprite e=new EffectSprite(p,"shield.png",0,4,false);
			e.setSize(2);
			effectHandler.addEffect(e);	
			status=ShieldStatus.FASTCHARGE;
		}
	}
}
