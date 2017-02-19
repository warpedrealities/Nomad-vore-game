package combat.statusEffects;

import java.io.DataInputStream;
import java.io.IOException;

public class StatusLoader {

	static public StatusEffect loadStatusEffect(DataInputStream dstream) throws IOException
	{
		int c=dstream.readInt();
		StatusEffect e;
		switch (c)
		{
		case 0:
			e=new Status_AttribMod();
			e.load(dstream);
			return e;
		
		case 1:
			e=new Status_Stun();
			e.load(dstream);
			return e;
		case 2:
			e=new Status_Bind();
			e.load(dstream);
			return e;	
		
		case 3:
			e=new Status_SubAbilityMod();
			e.load(dstream);
			return e;
		case 4:
			e=new Status_Stealth();
			e.load(dstream);
			return e;
		case 5:
			e=new Status_Defence();
			e.load(dstream);
			return e;
		case 6:
			e=new Status_DoT();
			e.load(dstream);
			return e;
		}
		return null;
	}
	
}
