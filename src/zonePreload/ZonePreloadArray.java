package zonePreload;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ZonePreloadArray extends ZonePreload {

	private ArrayList<Integer> preloadArray;

	public ZonePreloadArray() {
		preloadType = PRELOADARRAY;
		preloadArray = new ArrayList<Integer>();
	}

	public ZonePreloadArray(int id) {
		preloadID = id;
		preloadType = PRELOADARRAY;
		preloadArray = new ArrayList<Integer>();
	}

	public ArrayList<Integer> getPreloadArray() {
		return preloadArray;
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {

		dstream.writeInt(preloadType);
		dstream.writeInt(preloadID);
		dstream.writeInt(preloadArray.size());
		for (int i = 0; i < preloadArray.size(); i++) {
			dstream.writeInt(preloadArray.get(i));
		}
	}

	@Override
	public void load(DataInputStream dstream) throws IOException {

		preloadID = dstream.readInt();
		int c = dstream.readInt();
		for (int i = 0; i < c; i++) {
			preloadArray.add(dstream.readInt());
		}
	}

}
