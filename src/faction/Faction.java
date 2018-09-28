package faction;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actorRPG.npc.NPCStatblockLibrary;
import nomad.FlagField;
import shared.ParserHelper;

public class Faction {

	String filename;
	Map<String, Integer> factionRelationships;
	int defaultRelationship;

	private FlagField factionFlags;

	public Faction() {
		factionRelationships = new HashMap<String, Integer>();
	}

	public Faction(String filename) {
		this.filename = filename;
		factionRelationships = new HashMap<String, Integer>();
		Document doc = ParserHelper.LoadXML("assets/data/factions/" + filename + ".xml");

		Element root = (Element) doc.getFirstChild();
		NodeList children = root.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) children.item(i);

				if (element.getTagName().equals("factionRelationship")) {
					factionRelationships.put(element.getAttribute("ID"),
							Integer.parseInt(element.getAttribute("value")));
				}
				if (element.getTagName().equals("defaultRelationship")) {
					defaultRelationship = Integer.parseInt(element.getAttribute("value"));
				}

			}
		}
	}

	public int getRelationship(String factionName) {
		if (factionName.equals(filename)) {
			return 100;
		}

		Integer value = factionRelationships.get(factionName);
		if (value != null) {
			return value;
		}
		Faction faction = FactionLibrary.getInstance().getFaction(factionName);

		value = faction.factionRelationships.get(filename);
		if (value != null) {
			factionRelationships.put(factionName, value);
			return value;
		}

		if (faction.defaultRelationship <= defaultRelationship) {
			factionRelationships.put(factionName, faction.defaultRelationship);
			return faction.defaultRelationship;
		} else {
			factionRelationships.put(factionName, defaultRelationship);
			return defaultRelationship;
		}
	}

	public void save(DataOutputStream dstream) throws IOException {

		ParserHelper.SaveString(dstream, filename);
		dstream.writeInt(defaultRelationship);
		Set<String> keyset = factionRelationships.keySet();
		dstream.writeInt(keyset.size());
		Iterator<String> it = keyset.iterator();

		while (it.hasNext()) {
			String faction = it.next();
			ParserHelper.SaveString(dstream, faction);
			dstream.writeInt(factionRelationships.get(faction));
		}
		if (factionFlags != null) {
			dstream.writeBoolean(true);
			factionFlags.save(dstream);
		} else {
			dstream.writeBoolean(false);
		}
	}

	public void load(DataInputStream dstream) throws IOException {
		filename = ParserHelper.LoadString(dstream);
		defaultRelationship = dstream.readInt();
		int c = dstream.readInt();

		for (int i = 0; i < c; i++) {
			String faction = ParserHelper.LoadString(dstream);
			int value = dstream.readInt();
			factionRelationships.put(faction, value);
		}

		if (dstream.readBoolean() == true) {
			factionFlags = new FlagField();
			factionFlags.load(dstream);
		}

	}

	public String getFilename() {
		return filename;
	}

	public FlagField getFactionFlags() {
		if (factionFlags == null) {
			factionFlags = new FlagField();
		}
		return factionFlags;
	}

	public void modDisposition(String name, int dispositionModifier) {

		Integer value = factionRelationships.get(name);
		if (value == null) {
			value = defaultRelationship;
		}
		int v = value + dispositionModifier;
		if (v < 0) {
			v = 0;
		}
		if (v > 100) {
			v = 100;
		}

		factionRelationships.put(name, v);
		Faction alt = FactionLibrary.getInstance().getFaction(name);
		alt.factionRelationships.put(filename, v);

		NPCStatblockLibrary.getInstance().resetThreat();
	}

}
