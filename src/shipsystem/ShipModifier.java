package shipsystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;

import shipsystem.ShipAbility.AbilityType;

public class ShipModifier extends ShipAbility{

	static public final int FUEL_EFFICIENCY=0;
	static public final int MANOUVERABILITY=1;
	static public final int ARMOUR=2;
	static public final int SPEED=3;
	
	
	private float modification;
	private int modifies;
	private int uid;
	
	
	public int strToModifier(String str)
	{
		if (str.equals("FUEL_EFFICIENCY"))
		{
			return FUEL_EFFICIENCY;
		}
		if (str.equals("MANOUVERABILITY"))
		{
			return MANOUVERABILITY;
		}	
		if (str.equals("ARMOUR"))
		{
			return ARMOUR;
		}		
		return -1;
	}
	
	public ShipModifier(Element e)
	{
		abilityType=AbilityType.SA_MODIFIER;
		modifies=strToModifier(e.getAttribute("modifies"));
		modification=Float.parseFloat(e.getAttribute("modification"));
		uid=Integer.parseInt(e.getAttribute("uid"));
	}
	
	public ShipModifier(DataInputStream dstream) throws IOException {
		abilityType=AbilityType.SA_MODIFIER;
		modifies=dstream.readInt();
		modification=dstream.readFloat();
		uid=dstream.readInt();
	}
	
	@Override
	public void save(DataOutputStream dstream) throws IOException {
		dstream.writeInt(modifies);
		dstream.writeFloat(modification);
		dstream.writeInt(uid);
		
	}

	public float getModification() {
		return modification;
	}

	public int getModifies() {
		return modifies;
	}

	public int getUid() {
		return uid;
	}
	
	

}
