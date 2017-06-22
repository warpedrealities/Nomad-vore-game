package faction;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import shared.ParserHelper;

public class FactionLibrary {

	Map<String, Faction> factionMap;

	private static FactionLibrary instance;

	public static FactionLibrary getInstance() {
		if (instance == null) {
			instance = new FactionLibrary();
		}
		return instance;
	}

	public void clean() {
		factionMap.clear();
	}

	private FactionLibrary() {
		factionMap = new HashMap<String, Faction>();
	}

	public Faction getFaction(String name) {
		Faction faction = factionMap.get(name);
		if (faction == null) {
			faction = new Faction(name);
			factionMap.put(name, faction);
		}

		return faction;
	}

	public void save(DataOutputStream dstream) throws IOException {
		Set<String> keySet = factionMap.keySet();

		Iterator<String> it = keySet.iterator();
		dstream.writeInt(keySet.size());
		while (it.hasNext()) {
			String string = it.next();
			ParserHelper.SaveString(dstream, string);
			factionMap.get(string).save(dstream);

		}

	}

	public void load(DataInputStream dstream) throws IOException {
		clean();
		int c = dstream.readInt();
		for (int i = 0; i < c; i++) {
			String key = ParserHelper.LoadString(dstream);
			Faction faction = new Faction();
			faction.load(dstream);
			factionMap.put(key, faction);
		}

	}

	public void verifyRelationships() {

	}

}
