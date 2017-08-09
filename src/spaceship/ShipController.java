package spaceship;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import faction.Faction;
import nomad.FlagField;
import solarview.spaceEncounter.EncounterEntities.combatControllers.CombatController;

public interface ShipController {

	public enum scriptEvents{contact(0),systemEntry(1),ai(2),combat(3),victory(4),loss(5);
		private int value;		
		scriptEvents(int v)
		{
			value=v;
		}		
		public int getValue()
		{
			return value;
		}		
	};
	
	
	public void update(Spaceship Ship);

	public boolean canAct();

	public CombatController getCombat();
	
	public Faction getFaction();
	
	public default void save(DataOutputStream dstream) throws IOException
	{
		
	}
	
	public default void load(DataInputStream dstream) throws IOException
	{

	}
	
	public default FlagField getflags()
	{
		return null;
	}

	public default void event(scriptEvents event){}

	public default void setBusy(int i){}

	public void setShip(Spaceship spaceship);;
}
