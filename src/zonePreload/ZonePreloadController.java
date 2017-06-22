package zonePreload;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ZonePreloadController {

	List<ZonePreload> preloadList;

	public ZonePreloadController() {
		preloadList = new ArrayList<ZonePreload>();

	}

	public ZonePreload getPreload(int ID) {
		for (int i = 0; i < preloadList.size(); i++) {
			if (preloadList.get(i).getPreloadID() == ID) {
				return preloadList.get(i);
			}
		}
		return null;
	}

	public void addPreload(ZonePreload preload) {
		preloadList.add(preload);
	}

	public int getCount() {
		return preloadList.size();
	}

	public void save(DataOutputStream dstream) throws IOException {
		dstream.writeInt(preloadList.size());
		for (int i = 0; i < preloadList.size(); i++) {
			preloadList.get(i).save(dstream);
		}
	}

	public void load(DataInputStream dstream) throws IOException {
		int c = dstream.readInt();
		for (int i = 0; i < c; i++) {
			preloadList.add(ZonePreloadLoader.load(dstream));
		}
	}

}
