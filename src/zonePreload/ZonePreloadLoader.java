package zonePreload;

import java.io.DataInputStream;
import java.io.IOException;

public class ZonePreloadLoader {

	public static ZonePreload load(DataInputStream dstream) throws IOException {
		
		int type=dstream.readInt();
		ZonePreload zp;
		switch (type)
		{
		case ZonePreload.PRELOADVECTOR:
		zp=new ZonePreloadVector();
		zp.load(dstream);
		return zp;
	
		
		case ZonePreload.PRELOADARRAY:
		zp=new ZonePreloadArray();
		zp.load(dstream);
		return zp;
		}
		
		return null;
	}

}
