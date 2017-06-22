package nomad;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import shared.ParserHelper;

public class FlagField {

	private Map<String, Integer> flagMap;

	public FlagField() {
		flagMap = new HashMap<String, Integer>();

	}

	public void setFlag(String flagname, int value) {
		flagMap.put(flagname, value);
	}

	public void incrementFlag(String flagname) {
		flagMap.put(flagname, flagMap.get(flagname) + 1);
	}

	public int readFlag(String flagname) {
		Integer v = flagMap.get(flagname);
		if (v != null) {
			return v;
		}
		return 0;
	}

	public void save(DataOutputStream dstream) throws IOException {
		Set<String> keys = flagMap.keySet();
		Iterator<String> it = keys.iterator();
		dstream.writeInt(keys.size());
		while (it.hasNext()) {
			String str = it.next();
			ParserHelper.SaveString(dstream, str);
			dstream.writeInt(flagMap.get(str));
		}

	}

	public void load(DataInputStream dstream) throws IOException {
		int c = dstream.readInt();
		for (int i = 0; i < c; i++) {
			flagMap.put(ParserHelper.LoadString(dstream), dstream.readInt());
		}

	}

	public void clear() {
		// TODO Auto-generated method stub
		flagMap.clear();
	}
}
