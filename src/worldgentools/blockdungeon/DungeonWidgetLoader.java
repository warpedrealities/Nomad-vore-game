package worldgentools.blockdungeon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import item.Item;
import shared.ParserHelper;
import widgets.Widget;
import widgets.WidgetBreakable;
import widgets.WidgetConditionalPortal;
import widgets.WidgetContainer;
import widgets.WidgetConversation;
import widgets.WidgetDoor;
import widgets.WidgetHarvestable;
import widgets.WidgetItemPile;
import widgets.WidgetPortal;
import widgets.WidgetResearch;
import widgets.WidgetScripted;
import widgets.WidgetSprite;
import widgets.traps.Widget_Trap;
import worldgentools.LootTable;

public class DungeonWidgetLoader {

	private Map<String, LootTable> lootTables;

	public DungeonWidgetLoader() {
		lootTables = new HashMap<String, LootTable>();

	}

	public Widget addItemPile(WidgetDefinition definition) {
		LootTable lt = lootTables.get(definition.getWidgetInfo());
		ArrayList<Item> itemList = lt.generateLoot();

		WidgetItemPile pile = new WidgetItemPile(2, "a pile of items containing ");
		for (int i = 0; i < itemList.size(); i++) {
			pile.AddItem(itemList.get(i));
		}

		return pile;
	}

	public Widget loadWidget(WidgetDefinition definition) {
		if (definition.getWidgetName().equals("itempile")) {
			return addItemPile(definition);
		}
		if (definition.getWidgetName().equals("sprite")) {
			return addSprite(definition);
		}
		if (definition.getWidgetName().equals("describer")) {
			return addDescriber(definition);
		}
		Document doc = ParserHelper.LoadXML("assets/data/widgets/" + definition.getWidgetName() + ".xml");
		Element root = doc.getDocumentElement();
		Element n = (Element) doc.getFirstChild();
		Widget widget = null;
		if (root.getTagName().contains("breakable")) {
			widget = new WidgetBreakable(n);
		}
		if (root.getTagName().contains("trap")) {
			widget = new Widget_Trap(n);
		}
		if (root.getTagName().contains("portal")) {
			widget = new WidgetPortal(root);
			WidgetPortal wp = (WidgetPortal) widget;
			wp.setDestination(definition.getWidgetInfo(), Integer.parseInt(definition.getWidgetVariable()));
		}
		if (root.getTagName().contains("conditionalPortal")) {
			String s[] = definition.getWidgetInfo().split("#");
			widget = new WidgetConditionalPortal(root);
			WidgetConditionalPortal wp = (WidgetConditionalPortal) widget;
			wp.setFlag(s[1]);
			wp.setValue(Integer.parseInt(s[2]));
			if (s[3].equals("greaterThan")) {
				wp.setGreaterthan(true);
			}
			wp.setForbidText(s[4]);
			wp.setDestination(s[0], Integer.parseInt(definition.getWidgetVariable()));
		}
		if (root.getTagName().contains("harvestable")) {
			widget = new WidgetHarvestable(root);
		}
		if (root.getTagName().contains("door")) {
			widget = new WidgetDoor(root);
			WidgetDoor wd = (WidgetDoor) widget;
			if (definition.getWidgetVariable() != null && definition.getWidgetVariable().length() > 0) {
				wd.setLockStrength(Integer.parseInt(definition.getWidgetVariable()));
			}
			if (definition.getWidgetInfo() != null) {
				wd.setLockKey(definition.getWidgetInfo());
			}
		}
		if (root.getTagName().contains("scripted")) {
			widget = new WidgetScripted(root);
			WidgetScripted ws = (WidgetScripted) widget;
			if (definition.getWidgetInfo() != null) {
				ws.setScript(definition.getWidgetInfo());
			}
		}
		if (root.getTagName().contains("container")) {
			widget = new WidgetContainer(root);
			WidgetContainer wc = (WidgetContainer) widget;
			LootTable lt = lootTables.get(definition.getWidgetInfo());
			wc.setItems(lt.generateLoot());
		}
		if (root.getTagName().contains("research")) {
			widget = new WidgetResearch(root);
			WidgetResearch wr = (WidgetResearch) widget;
			setupResearch(wr, definition);
		}
		if (root.getTagName().contains("conversation")) {
			widget = new WidgetConversation(root);
			WidgetConversation wc = (WidgetConversation) widget;
			wc.setConversationFileName(definition.getWidgetInfo());
			wc.setSprite(Integer.parseInt(definition.getWidgetVariable()));
		}
		return widget;
	}

	private Widget addDescriber(WidgetDefinition definition) {
		// TODO Auto-generated method stub
		return null;
	}

	private Widget addSprite(WidgetDefinition definition) {
		String[] variable = definition.getWidgetVariable().split("#");
		int x = Integer.parseInt(variable[0]);
		int y = Integer.parseInt(variable[1]);
		return new WidgetSprite(definition.getWidgetInfo(), x, y);
	}

	private void setupResearch(WidgetResearch wr, WidgetDefinition definition) {
		String[] strings = definition.getWidgetInfo().split("#");
		String group = null;
		if (strings.length > 2) {
			group = strings[2];
		}
		wr.setData(strings[0], strings[1], Integer.parseInt(definition.getWidgetVariable()), group);
	}

	public void loadLootTable(Element enode) {

		LootTable loot = new LootTable(enode);
		lootTables.put(enode.getAttribute("identity"), loot);

	}

}
