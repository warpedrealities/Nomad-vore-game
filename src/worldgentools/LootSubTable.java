package worldgentools;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import item.Item;
import nomad.universe.Universe;
import shared.ParserHelper;

public class LootSubTable extends LootEntry {

	ArrayList<LootStub> loot;
	float range;

	public LootSubTable(Element node) {
		super(node);

		loot = new ArrayList<LootStub>();
		String file=node.getAttribute("file");
		Document doc = ParserHelper.LoadXML("assets/data/lootSubTables/" + file + ".xml");

		// read through the top level nodes

		Element n = (Element) doc.getFirstChild();

		NodeList children = n.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node N = children.item(i);
			if (N.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) N;
				// run each step successively
				if (Enode.getTagName() == "loot") {
					LootStub ls = new LootStub(Enode);
					range += ls.getChance();
					loot.add(ls);
				}

			}
		}

	}

	
	private Item pickLoot() {
		int r = Universe.m_random.nextInt((int) range);

		float lrange = 0;

		for (int i = 0; i < loot.size(); i++) {
			if (r > lrange && r <= lrange + loot.get(i).getChance()) {
				Item item=Universe.getInstance().getLibrary().getItem(loot.get(i).getItem());
				loot.get(i).runCommon(item);
				return item;
			} else if (r > lrange) {
				lrange += loot.get(i).getChance();
			}

		}
		return null;
	}

	@Override
	public Item genLoot() {
		float chance = getChance() * 100;
		int roll = Universe.m_random.nextInt(100);

		if (roll < chance) {
			Item item = pickLoot();

			commonFunction(item);

			return item;

		}

		return null;

	}

}
