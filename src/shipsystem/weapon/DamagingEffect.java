package shipsystem.weapon;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;

import solarview.spaceEncounter.EncounterEntities.EncounterShipImpl;
import solarview.spaceEncounter.effectHandling.EffectHandler_Interface;
import vmo.GameManager;

public class DamagingEffect implements ShipWeaponEffect {

	int penetration = 0, disruption = 0, minDamage, maxDamage;
	float falloff = 0;

	public DamagingEffect(Element enode) {
		minDamage = Integer.parseInt(enode.getAttribute("min"));
		maxDamage = Integer.parseInt(enode.getAttribute("max"));
		if (enode.getAttribute("falloff").length() > 0) {
			falloff = Float.parseFloat(enode.getAttribute("falloff"));
		}
		if (enode.getAttribute("penetration").length() > 0) {
			penetration = Integer.parseInt(enode.getAttribute("penetration"));
		}
		if (enode.getAttribute("disruption").length() > 0) {
			penetration = Integer.parseInt(enode.getAttribute("disruption"));
		}
	}

	public DamagingEffect(DataInputStream dstream) throws IOException {
		minDamage = dstream.readInt();
		maxDamage = dstream.readInt();
		penetration = dstream.readInt();
		disruption = dstream.readInt();
		falloff = dstream.readFloat();
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		dstream.writeInt(minDamage);
		dstream.writeInt(maxDamage);
		dstream.writeInt(penetration);
		dstream.writeInt(disruption);
		dstream.writeFloat(falloff);
	}

	@Override
	public int getType() {
		return 0;
	}

	@Override
	public void apply(float distance, EncounterShipImpl ship, EffectHandler_Interface effectHandler) {
		int damage=minDamage;
		if (maxDamage>minDamage)
		{
			damage+=GameManager.m_random.nextInt(maxDamage-minDamage);
		}
		if (falloff > 0)
		{
			damage-=falloff*distance;
		}
		//apply shield and get amount of shield resistance
		if (ship.getShield() != null && ship.getShield().isActive())
		{
			damage = ship.getShield().applyDefence(damage, disruption, effectHandler, ship);
		}

		//apply armour
		damage -= ship.getShip().getShipStats().getArmour();
		if (damage<0)
		{
			damage=0;
		}
		effectHandler.drawText(ship.getPosition().replicate(), Integer.toString(damage), 0);
		if (damage>0)
		{
			ship.getMonitor().reportDamage(damage);
			ship.getShip().getShipStats().getResource("HULL").subtractResourceAmount(damage);
		}
	}

}
