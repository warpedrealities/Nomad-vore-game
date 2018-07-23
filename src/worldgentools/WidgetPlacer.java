package worldgentools;

import item.Item;
import item.instances.ItemDepletableInstance;
import item.instances.ItemExpositionInstance;
import item.instances.ItemKeyInstance;
import nomad.universe.Universe;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actor.npc.NPC;
import shared.ParserHelper;
import shared.Vec2f;
import shared.Vec2i;
import shipsystem.WidgetDamage;
import shipsystem.WidgetNavConsole;
import shipsystem.WidgetSystem;
import actorRPG.RPG_Helper;
import widgets.Widget;
import widgets.WidgetAccomodation;
import widgets.WidgetBreakable;
import widgets.WidgetCapture;
import widgets.WidgetComputer;
import widgets.WidgetConditionalPortal;
import widgets.WidgetContainer;
import widgets.WidgetConversation;
import widgets.WidgetCraftingTable;
import widgets.WidgetDescription;
import widgets.WidgetDoor;
import widgets.WidgetHarvestable;
import widgets.WidgetItemPile;
import widgets.WidgetPortal;
import widgets.WidgetReformer;
import widgets.WidgetScriptPortal;
import widgets.WidgetScripted;
import widgets.WidgetSlot;
import widgets.WidgetSprite;
import widgets.capsules.WidgetCapsuleSystem;
import widgets.scriptedEvents.WidgetScriptedEvent;
import widgets.spawner.WidgetSpawner;
import zone.Tile;
import zone.Zone;
import zone.TileDef.TileMovement;

public class WidgetPlacer {
	Zone zone;
	PointsOfInterest pointsOfInterest;

	public WidgetPlacer() {

	}

	public WidgetPlacer(Zone zone) {
		this.zone = zone;

	}

	public WidgetPlacer(Zone zone, PointsOfInterest pointsOfInterest) {
		this.zone = zone;
		this.pointsOfInterest = pointsOfInterest;
	}

	public void placeItem(Element Rnode, int offsetx, int offsety) {
		int x = Integer.parseInt(Rnode.getAttribute("x")) + offsetx;
		int y = Integer.parseInt(Rnode.getAttribute("y")) + offsety;

		WidgetItemPile pile = new WidgetItemPile(2, "a pile of items containing ");
		NodeList children = Rnode.getChildNodes();
		int index = 0;
		if (children.getLength() > 0) {
			for (int i = 0; i < children.getLength(); i++) {
				Node node = children.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element Enode = (Element) node;

					if (Enode.getTagName().equals("lootTable")) {
						LootTable lootTable=new LootTable(Enode);
						List<Item> items= lootTable.generateLoot();
						for (int j=0;j<items.size();j++)
						{
							pile.AddItem(items.get(j));
						}
					}
					if (Enode.getTagName() == "item") {
						Item item = Universe.getInstance().getLibrary().getItem(Enode.getAttribute("itemname"));
						if (ItemExpositionInstance.class.isInstance(item)) {
							ItemExpositionInstance iei = (ItemExpositionInstance) item;
							iei.setExposition(Enode.getAttribute("exposition"));
						}
						if (ItemKeyInstance.class.isInstance(item)) {
							ItemKeyInstance iki = (ItemKeyInstance) item;
							iki.setLock(Enode.getAttribute("lock"));
						}
						if (ItemDepletableInstance.class.isInstance(item)) {
							ItemDepletableInstance idi = (ItemDepletableInstance) item;
							if (Enode.getAttribute("energy").length() > 0) {
								idi.setEnergy(Float.parseFloat(Enode.getAttribute("energy")));
							}

						}
						pile.AddItem(item);
					}
				}
			}
		}
		zone.getTile(x, y).setWidget(pile);

	}

	public void placeItemPOI(Element enode, PointsOfInterest pointsOfInterest2) {

		Vec2i p = pointsOfInterest2.getNextPOI();
		int x = p.x;
		int y = p.y;

		WidgetItemPile pile = new WidgetItemPile(2, "a pile of items containing ");
		NodeList children = enode.getChildNodes();
		int index = 0;
		if (children.getLength() > 0) {
			for (int i = 0; i < children.getLength(); i++) {
				Node node = children.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element Enode = (Element) node;

					if (Enode.getTagName() == "item") {
						Item item = Universe.getInstance().getLibrary().getItem(Enode.getAttribute("itemname"));
						if (ItemExpositionInstance.class.isInstance(item)) {
							ItemExpositionInstance iei = (ItemExpositionInstance) item;
							iei.setExposition(Enode.getAttribute("exposition"));
						}
						if (ItemDepletableInstance.class.isInstance(item)) {
							ItemDepletableInstance idi = (ItemDepletableInstance) item;
							if (Enode.getAttribute("energy").length() > 0) {
								idi.setEnergy(Float.parseFloat(Enode.getAttribute("energy")));
							}

						}
						pile.AddItem(item);
					}
				}
			}
		}
		zone.getTile(x, y).setWidget(pile);
	}

	public void Breakable(Element breakablenode, int offsetx, int offsety) {
		int x = Integer.parseInt(breakablenode.getAttribute("x")) + offsetx;
		int y = Integer.parseInt(breakablenode.getAttribute("y")) + offsety;
		int sprite = Integer.parseInt(breakablenode.getAttribute("sprite"));
		String description = null;
		Item[] items = new Item[Integer.parseInt(breakablenode.getAttribute("items"))];
		int hp = Integer.parseInt(breakablenode.getAttribute("hp"));
		int resistances[] = new int[3];
		NodeList children = breakablenode.getChildNodes();
		int index = 0;
		if (children.getLength() > 0) {
			for (int i = 0; i < children.getLength(); i++) {
				Node node = children.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element Enode = (Element) node;

					if (Enode.getTagName() == "description") {
						description = Enode.getTextContent();
					}

					if (Enode.getTagName() == "Item") {
						items[index] = Universe.getInstance().getLibrary().getItem(Enode.getTextContent());
						index++;
					}

					if (Enode.getTagName() == "resistance") {
						int value = RPG_Helper.AttributefromString(Enode.getAttribute("resists"));
						int strength = Integer.parseInt(Enode.getAttribute("strength"));
						resistances[value] = strength;
					}
				}
			}
		}

		WidgetBreakable breakable = new WidgetBreakable(sprite, description, breakablenode.getAttribute("name"), items,
				hp, resistances);
		zone.getTiles()[x][y].setWidget(breakable);

	}

	private Widget genSprite(Element Enode) {
		WidgetSprite sprite = new WidgetSprite(Enode.getAttribute("file"),
				Integer.parseInt(Enode.getAttribute("width")), Integer.parseInt(Enode.getAttribute("height")));

		return sprite;
	}

	private Widget genDescriber(Element enode) {

		WidgetDescription widget = new WidgetDescription(enode.getTextContent());
		return widget;
	}

	private Widget genScriptedEvent(Element enode) {

		WidgetScriptedEvent widget = new WidgetScriptedEvent(enode.getTextContent());
		return widget;
	}

	private Widget genSpawner(Element enode) {
		WidgetSpawner widget = new WidgetSpawner(enode);
		return widget;
	}

	private Widget genHullDamage(Element enode) {
		boolean exterior=false;
		if ("true".equals(enode.getAttribute("exterior")))
		{
			exterior=true;
		}
		WidgetDamage widget = new WidgetDamage(Integer.parseInt(enode.getAttribute("value")),exterior);
		return widget;
	}

	public Widget genWidget(Element Enode) {
		String name = Enode.getAttribute("name");
		if (name.equals("SPAWNER")) {
			return genSpawner(Enode);
		}
		if (name.equals("DAMAGE")) {
			return genHullDamage(Enode);
		}
		if (name.equals("SPRITE")) {
			return genSprite(Enode);
		}
		if (name.equals("DESCRIBER")) {
			return genDescriber(Enode);
		}
		if (name.equals("SCRIPTEDEVENT")) {
			return genScriptedEvent(Enode);
		}
		LootTable lootTable = null;
		Document doc = ParserHelper.LoadXML("assets/data/widgets/" + name + ".xml");
		Element root = doc.getDocumentElement();
		Element n = (Element) doc.getFirstChild();
		Widget widget = null;
		if (root.getTagName().contains("harvestable")) {
			widget = new WidgetHarvestable(n);
		}
		if (root.getTagName().contains("breakable")) {
			widget = new WidgetBreakable(n);
		}
		if (root.getTagName().contains("craftingtable")) {
			widget = new WidgetCraftingTable(n);
		}
		if (root.getTagName().contains("container")) {
			WidgetContainer container = new WidgetContainer(n);
			if (lootTable == null) {
				lootTable = new LootTable(Enode.getElementsByTagName("lootTable").item(0));
			}
			container.addItems(lootTable.generateLoot());
			widget = container;
		}
		if (root.getTagName().contains("shipsystem")) {
			WidgetSystem system = new WidgetSystem(root);
			NodeList subnodes = Enode.getElementsByTagName("resource");
			if (subnodes != null && subnodes.getLength() > 0) {
				Element e = (Element) subnodes.item(0);
				system.setResource(e.getAttribute("type"), Float.parseFloat(e.getAttribute("value")));
			}

			widget = system;
		}
		if (root.getTagName().contains("door")) {
			WidgetDoor door = new WidgetDoor(n);
			NodeList subnodes = Enode.getElementsByTagName("lock");
			if (subnodes != null) {
				Element e = (Element) subnodes.item(0);
				door.setLockKey(e.getAttribute("key"));
				door.setLockStrength(Integer.parseInt(e.getAttribute("strength")));
			}
			widget = door;
		}
		if (root.getTagName().contains("accomodation")) {
			widget = new WidgetAccomodation(n);
		}
		if (root.getTagName().contains("computer")) {
			widget = new WidgetComputer(n);
		}

		if (root.getTagName().contains("navconsole")) {
			widget = new WidgetNavConsole(root);
		}
		if (root.getTagName().contains("conversation")) {
			widget = new WidgetConversation(root);
			WidgetConversation wc = (WidgetConversation) widget;
			wc.setConversationFileName(Enode.getAttribute("info"));
			wc.setSprite(Integer.parseInt(Enode.getAttribute("variable")));
		}
		if (root.getTagName().contains("systemSlot")) {
			widget = new WidgetSlot(root);
			WidgetSlot ws = (WidgetSlot) widget;
			NodeList children = Enode.getElementsByTagName("contains");
			if (children.getLength() > 0) {
				ws.setWidget((WidgetBreakable) genWidget((Element) children.item(0)),null);
			}
			children = Enode.getElementsByTagName("widgetItem");
			if (children.getLength() > 0) {
				ws.setWidgetItem(((Element) children.item(0)).getTextContent());
			}
			children = Enode.getElementsByTagName("facing");
			if (children.getLength() > 0) {
				Element e = (Element) children.item(0);
				ws.setFacing(Integer.parseInt(e.getAttribute("value")));
			}
		}
		if (root.getTagName().contains("reformer")) {
			widget = new WidgetReformer(root);
		}
		if (root.getTagName().contains("capture")) {
			widget = new WidgetCapture(root);
		}
		if (root.getTagName().contains("capsuleSystem")) {
			widget = new WidgetCapsuleSystem(root);		
		}
		if (root.getTagName().contains("scripted")) {
			WidgetScripted scripted = new WidgetScripted(n);
			NodeList subnodes = Enode.getElementsByTagName("script");
			if (subnodes != null) {
				Element e = (Element) subnodes.item(0);
				scripted.setScript(e.getAttribute("value"));
			}
			widget=scripted;
		}
		NodeList descOverride = Enode.getElementsByTagName("description");
		if (widget != null && descOverride.getLength() > 0) {
			Element e = (Element) descOverride.item(0);
			widget.setDescription(e.getTextContent().replace("\n", ""));
		}
		return widget;
	}

	public void placeWidget(Element Enode, int xoffset, int yoffset) {
		int x = Integer.parseInt(Enode.getAttribute("x"));
		int y = Integer.parseInt(Enode.getAttribute("y"));
		Widget widget = genWidget(Enode);
		zone.getTiles()[x + xoffset][y + yoffset].setWidget(widget);

	}

	void placeConditionalPortal(Element enode, int offsetx, int offsety) {
		int x = Integer.parseInt(enode.getAttribute("x")) + offsetx;
		int y = Integer.parseInt(enode.getAttribute("y")) + offsety;
		String string = null;
		int id = 0;
		if (enode.getAttribute("destination").length() > 0) {
			string = enode.getAttribute("destination");
		}
		if (enode.getAttribute("ID") != null) {
			id = Integer.parseInt(enode.getAttribute("ID"));
		}
		NodeList children = enode.getChildNodes();
		String description = null;
		String forbidText = null;
		String flag = null;
		int value = 0;
		boolean greaterthan = false;

		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) children.item(i);
				if (element.getTagName().equals("description")) {
					description = element.getTextContent().replace("\n", "");
				}
				if (element.getTagName().equals("forbidtext")) {
					forbidText = element.getTextContent();
				}
				if (element.getTagName().equals("condition")) {
					flag = element.getAttribute("flag");
					if (element.getAttribute("operator").equals("greaterthan")) {
						greaterthan = true;
					}
					value = Integer.parseInt(element.getAttribute("value"));
				}
			}
		}
		WidgetConditionalPortal portal = new WidgetConditionalPortal(Integer.parseInt(enode.getAttribute("sprite")),
				description, id);
		portal.setDestination(string, id);
		if (enode.getAttribute("facing").length() > 0) {
			portal.setFacing(Integer.parseInt(enode.getAttribute("facing")));
		}
		portal.setFlag(flag);
		portal.setForbidText(forbidText);
		portal.setGreaterthan(greaterthan);
		portal.setValue(value);
		zone.getTile(x, y).setWidget(portal);

	}

	public void placeScriptedPortal(Element enode, int offsetx, int offsety) {
		int x = Integer.parseInt(enode.getAttribute("x")) + offsetx;
		int y = Integer.parseInt(enode.getAttribute("y")) + offsety;
		String string = null;
		int id = 0;
		if (enode.getAttribute("destination").length() > 0) {
			string = enode.getAttribute("destination");
		}
		if (enode.getAttribute("ID") != null) {
			id = Integer.parseInt(enode.getAttribute("ID"));
		}
		NodeList children = enode.getChildNodes();
		String description = null;
		String script = null;

		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) children.item(i);
				if (element.getTagName().equals("description")) {
					description = element.getTextContent().replace("\n", "");
				}
				if (element.getTagName().equals("script")) {
					script = element.getAttribute("value");
				}
			}
		}
		WidgetScriptPortal portal = new WidgetScriptPortal(Integer.parseInt(enode.getAttribute("sprite")), description,
				id);
		portal.setDestination(string, id);
		if (enode.getAttribute("facing").length() > 0) {
			portal.setFacing(Integer.parseInt(enode.getAttribute("facing")));
		}
		portal.setFilename(script);
		zone.getTile(x, y).setWidget(portal);

	}

	public void SeedWidgets(Element Enode, boolean[][] grid) {

		// max population
		int min = Integer.parseInt(Enode.getAttribute("min"));
		// min population
		int max = Integer.parseInt(Enode.getAttribute("max"));
		int number = 0;
		int count = min;
		if (max>min)
		{
			count+=(Universe.m_random.nextInt(max - min));
		}
		// npc name
		String name = Enode.getAttribute("name");
		LootTable lootTable = null;
		Document doc = ParserHelper.LoadXML("assets/data/widgets/" + name + ".xml");
		Element root = doc.getDocumentElement();
		Element n = (Element) doc.getFirstChild();

		while (number < count) {
			int x = Universe.m_random.nextInt(zone.getWidth());
			int y = Universe.m_random.nextInt(zone.getHeight());
			if (zone.getTiles()[x][y] != null && grid[x][y] == true) {
				if (zone.getTiles()[x][y].getDefinition().getMovement() == TileMovement.WALK
						&& zone.getTiles()[x][y].getWidgetObject() == null) {
					Widget widget = null;
					if (root.getTagName().contains("harvestable")) {
						widget = new WidgetHarvestable(n);
					}
					if (root.getTagName().contains("breakable")) {
						widget = new WidgetBreakable(n);
					}
					if (root.getTagName().contains("conversation")) {
						WidgetConversation wc = new WidgetConversation(n);
						Element e=(Element)Enode.getElementsByTagName("conversation").item(0);
						wc.setConversationFileName(e.getAttribute("value"));
						e=(Element)Enode.getElementsByTagName("sprite").item(0);
						wc.setSprite(Integer.parseInt(e.getAttribute("value")));
						widget=wc;
					}
					if (root.getTagName().contains("container")) {
						WidgetContainer container = new WidgetContainer(n);
						if (lootTable == null) {
							lootTable = new LootTable(Enode.getElementsByTagName("lootTable").item(0));
						}
						container.setItems(lootTable.generateLoot());
						widget = container;
					}
					zone.getTiles()[x][y].setWidget(widget);
					number++;

				}
			}

		}
	}

	public void placeWidgetPOI(Element enode, boolean[][] grid) {

		LootTable lootTable = null;
		Vec2i p = pointsOfInterest.getNextPOI();
		String name = enode.getAttribute("name");

		Document doc = ParserHelper.LoadXML("assets/data/widgets/" + name + ".xml");
		Element root = doc.getDocumentElement();
		Element n = (Element) doc.getFirstChild();
		Widget widget = null;
		if (root.getTagName().contains("craftingtable")) {
			widget = new WidgetCraftingTable(n);
		}

		if (root.getTagName().contains("harvestable")) {
			widget = new WidgetHarvestable(n);
		}
		if (root.getTagName().contains("breakable")) {
			widget = new WidgetBreakable(n);
		}
		if (root.getTagName().contains("reformer")) {
			widget = new WidgetReformer(n);
		}
		if (root.getTagName().contains("container")) {
			WidgetContainer container = new WidgetContainer(n);
			if (lootTable == null) {
				lootTable = new LootTable(enode.getElementsByTagName("lootTable").item(0));
			}
			container.setItems(lootTable.generateLoot());
			widget = container;
		}
		Tile t = zone.getTiles()[p.x][p.y];
		t.setWidget(widget);

	}

	public void placeSpawner(Element enode) {

	}

}
