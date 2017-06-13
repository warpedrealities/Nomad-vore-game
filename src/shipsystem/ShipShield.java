package shipsystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;

public class ShipShield extends ShipAbility {

	private int hitpoints,absorption,regeneration,restartTime;
	private float energyCost;
	
	public ShipShield(Element node, String name)
	{
		
	}

	public ShipShield(DataInputStream dstream, String m_name) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		// TODO Auto-generated method stub

	}

	public int getHitpoints() {
		return hitpoints;
	}

	public int getAbsorption() {
		return absorption;
	}

	public int getRegeneration() {
		return regeneration;
	}

	public int getRestartTime() {
		return restartTime;
	}

	public float getEnergyCost() {
		return energyCost;
	}

	
}
