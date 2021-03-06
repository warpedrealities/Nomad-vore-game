package shipsystem;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class ShipAbility {

	public enum AbilityType {
		SA_RESOURCE, SA_MODIFIER, SA_CONVERTER, SA_DISPENSER, 
		SA_SHIELD, SA_WEAPON,SA_FTL,SA_CREW,SA_SPAWNER
	};

	protected AbilityType abilityType;

	public AbilityType getAbilityType() {
		return abilityType;
	}

	public abstract void save(DataOutputStream dstream) throws IOException;

}
