package zonePreload;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class ZonePreload {

	protected int preloadID;
	protected int preloadType;

	public static final int PRELOADVECTOR = 0;
	public static final int PRELOADARRAY = 1;

	public int getPreloadID() {
		return preloadID;
	}

	public int getPreloadType() {
		return preloadType;
	}

	abstract public void save(DataOutputStream dstream) throws IOException;

	abstract public void load(DataInputStream dstream) throws IOException;

}
