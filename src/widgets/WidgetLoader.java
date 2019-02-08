package widgets;

import java.io.DataInputStream;
import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import shared.ParserHelper;
import shipsystem.WidgetDamage;
import shipsystem.WidgetNavConsole;
import shipsystem.WidgetSystem;
import widgets.capsules.WidgetCapsule;
import widgets.capsules.WidgetCapsuleSystem;
import widgets.scriptedEvents.WidgetScriptedEvent;
import widgets.spawner.WidgetSpawner;
import widgets.traps.Widget_Trap;

public class WidgetLoader {

	public static Widget loadWidget(DataInputStream dstream) throws IOException {
		int s = dstream.read();
		switch (s) {
		case 0:
			return new WidgetSprite(dstream);

		case 1:
			return new WidgetPortal(dstream);

		case 2:
			return new WidgetItemPile(dstream);
		case 3:
			return new WidgetHarvestable(dstream);

		case 4:
			return new WidgetDescription(dstream);
		case 5:
			return new WidgetBreakable(dstream);
		case 6:
			return new WidgetCraftingTable(dstream);
		case 7:
			return new WidgetContainer(dstream);
		case 8:
			return new WidgetSystem(dstream);
		case 9:
			return new WidgetNavConsole(dstream);
		case 10:
			return new WidgetDamage(dstream);
		case 11:
			return new WidgetDoor(dstream);
		case 12:
			return new WidgetSlot(dstream);
		case 13:
			return new WidgetConversation(dstream);
		case 14:
			return new WidgetAccomodation(dstream);
		case 15:
			return new WidgetConditionalPortal(dstream);
		case 16:
			return new WidgetScriptPortal(dstream);
		case 17:
			return new WidgetComputer(dstream);
		case 18:
			return new WidgetSpawner(dstream);
		case 19:
			return new Widget_Trap(dstream);
		case 20:
			return new WidgetScriptedEvent(dstream);
		case 21:
			return new WidgetCapture(dstream);
		case 22:
			return new WidgetScripted(dstream);
		case 23:
			return new WidgetReformer(dstream);
		case 24:
			return new WidgetCapsule(dstream);
		case 25:
			return new WidgetCapsuleSystem(dstream);
		case 26:
			return new WidgetResearch(dstream);
		}
		return null;
	}

	static public Widget genWidget(String widgetName) {
		String name = widgetName;

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

			widget = container;
		}
		if (root.getTagName().contains("shipsystem")) {
			WidgetSystem system = new WidgetSystem(root);

			widget = system;
		}
		if (root.getTagName().contains("door")) {
			WidgetDoor door = new WidgetDoor(n);

			widget = door;
		}
		if (root.getTagName().contains("computer")) {
			widget = new WidgetComputer(n);
		}
		if (root.getTagName().contains("accomodation")) {
			widget = new WidgetAccomodation(n);
		}
		if (root.getTagName().contains("capture")) {
			widget = new WidgetCapture(n);
		}
		if (root.getTagName().contains("reformer")) {
			widget = new WidgetReformer(n);
		}

		if (root.getTagName().contains("navconsole")) {
			widget = new WidgetNavConsole(root);

		}
		if (root.getTagName().contains("systemSlot")) {
			widget = new WidgetSlot(root);
			WidgetSlot ws = (WidgetSlot) widget;

		}
		if (root.getTagName().equals("capsuleSystem")) {
			widget = new WidgetCapsuleSystem(root);
		}
		if (root.getTagName().equals("capsule")) {
			widget = new WidgetCapsule(root);
		}
		if (root.getTagName().equals("research")) {
			widget = new WidgetResearch(root);
		}
		return widget;
	}
}
