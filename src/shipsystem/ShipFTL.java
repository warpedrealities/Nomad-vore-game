package shipsystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.w3c.dom.Element;

import shipsystem.ShipAbility.AbilityType;

public class ShipFTL extends ShipAbility {

	private int FTL;

	
	public ShipFTL(Element enode, String m_name) {
		abilityType = AbilityType.SA_FTL;
		FTL=Integer.parseInt(enode.getAttribute("value"));
	}



	public ShipFTL(DataInputStream dstream, String m_name) throws IOException {
		abilityType = AbilityType.SA_FTL;
		FTL=dstream.readInt();
	}



	@Override
	public void save(DataOutputStream dstream) throws IOException {
		dstream.writeInt(FTL);
	}



	public int getFTL() {
		return FTL;
	}
	
	

}
