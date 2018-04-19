package zone.Environment;

import java.io.DataInputStream;
import java.io.IOException;

public class EnvironmentalConditionLoader {

	
	EnvironmentalCondition loadCondition(DataInputStream dstream) throws IOException
	{
		int type=dstream.readInt();
		switch (type)
		{
		case 0:
			DamagingCondition condition0=new DamagingCondition();
			condition0.load(dstream);
			return condition0;
			
		case 1:
			ModifierCondition condition1=new ModifierCondition();
			condition1.load(dstream);
			return condition1;
		}
		
		return null;
	}
}
