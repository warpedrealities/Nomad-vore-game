package item;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import item.instances.ItemBlueprintInstance;
import item.instances.ItemCaptureInstance;
import item.instances.ItemContainerInstance;
import item.instances.ItemDepletableInstance;
import item.instances.ItemExpositionInstance;
import item.instances.ItemKeyInstance;
import item.instances.ItemStack;
import shared.ParserHelper;

public class ItemLibrary {

	ArrayList<Item> m_items;

	public ItemLibrary() {
		m_items = new ArrayList<Item>();
		File file = new File("assets/data/items");

		// find the names of all files in the item folder
		File[] files = file.listFiles();
		// use reader to generate items
		int index = 0;
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().contains(".svn") == false) {
				if (files[i].isDirectory() == true) {
					index = subLoad(files[i].getName(), index);
				} else {
					m_items.add(Reader(files[i].getName(), index));
					index++;
				}

			}
		}
	}

	private int subLoad(String filename, int index) {
		File file = new File("assets/data/items" + "/" + filename);
		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().contains(".svn") == false) {
				if (files[i].isDirectory() == true) {
					index = subLoad(filename + "/" + files[i].getName(), index);
				} else {
					m_items.add(Reader(filename + "/" + files[i].getName(), index));
					index++;
				}

			}
		}
		return index;
	}

	private Item findItem(String name) {
		for (int i = 0; i < m_items.size(); i++) {
			if (m_items.get(i).getName().equals(name)) {
				return m_items.get(i);
			}
		}
		return null;
	}

	public Item getItem(String name) {
		if (name.equals("gold")) {
			return new ItemCoin(false);
		}
		if (name.equals("credits")) {
			return new ItemCoin(true);
		}
		if (name.equals("questItem")) {
			return new QuestItem();
		}
		Item item = findItem(name);
		if (item != null) {
			if (ItemHasEnergy.class.isInstance(item)) {
				ItemHasEnergy IE = (ItemHasEnergy) item;
				if (IE.getEnergy() != null) {
					return new ItemDepletableInstance(item);
				}
			}
			if (ItemExposition.class.isInstance(item)) {
				return new ItemExpositionInstance((ItemExposition) item);
			}
			if (ItemBlueprint.class.isInstance(item)) {
				return new ItemBlueprintInstance((ItemBlueprint) item);
			}
			if (ItemKey.class.isInstance(item)) {
				return new ItemKeyInstance((ItemKey) item);
			}
			if (ItemCapture.class.isInstance(item)) {
				return new ItemCaptureInstance((ItemCapture) item);
			}
			if (ItemContainer.class.isInstance(item)) {
				return new ItemContainerInstance(item);
			}
			return item;

		}
		return null;
	}

	Item Reader(String filename, int id) {
		Document doc = ParserHelper.LoadXML("assets/data/items/" + filename);
		Element root = doc.getDocumentElement();
		Element n = (Element) doc.getFirstChild();
		Element Enode = (Element) n;
		if (Enode.getTagName() == "ItemConsumable") {
			return new ItemConsumable(Enode, id);
		}
		if (Enode.getTagName() == "ItemEquip") {
			return new ItemEquip(Enode, id);
		}
		if (Enode.getTagName() == "ItemWeapon") {
			return new ItemWeapon(Enode, id);
		}
		if (Enode.getTagName() == "ItemAmmo") {
			return new ItemAmmo(Enode, id);
		}
		if (Enode.getTagName() == "ItemResource") {
			return new ItemResource(Enode, id);
		}
		if (Enode.getTagName() == "ItemWidget") {
			return new ItemWidget(Enode, id);
		}
		if (Enode.getTagName() == "ItemExposition") {
			return new ItemExposition(Enode, id);
		}
		if (Enode.getTagName() == "ItemKey") {
			return new ItemKey(Enode, id);
		}
		if (Enode.getTagName() == "ItemBlueprint") {
			return new ItemBlueprint(Enode, id);
		}
		if (Enode.getTagName() == "ItemCapture") {
			return new ItemCapture(Enode, id);
		}
		if (Enode.getTagName() == "ItemContainer") {
			return new ItemContainer(Enode, id);
		}
		return null;

	}

	public Item getItem(DataInputStream dstream) throws IOException {
		// TODO Auto-generated method stub
		int c = dstream.read();
		if (c == 0) {
			// return item;
			return findItem(ParserHelper.LoadString(dstream));
		}
		if (c == 1) {
			Item item = findItem(ParserHelper.LoadString(dstream));
			ItemDepletableInstance instance = new ItemDepletableInstance(item);
			instance.setEnergy(dstream.readFloat());
			// return itemdepletable
			return instance;
		}
		if (c == 2) {
			// return itemstack
			return new ItemStack(findItem(ParserHelper.LoadString(dstream)), dstream.read());
		}
		if (c == 3) {
			// return itemexpositioninstance
			Item item = findItem(ParserHelper.LoadString(dstream));
			ItemExpositionInstance iei = new ItemExpositionInstance((ItemExposition) item);
			iei.setExposition(ParserHelper.LoadString(dstream));
			return iei;
		}
		if (c == 4) {
			// return itemexpositioninstance
			Item item = findItem(ParserHelper.LoadString(dstream));
			ItemKeyInstance iki = new ItemKeyInstance((ItemKey) item);
			iki.setLock(ParserHelper.LoadString(dstream));
			return iki;
		}
		if (c == 5) {
			ItemCoin item = new ItemCoin();
			item.setCount(dstream.readInt());
			item.setIsCredits(dstream.readBoolean());
			return item;
		}
		if (c == 6) {
			ItemBlueprintInstance item = new ItemBlueprintInstance(findItem(ParserHelper.LoadString(dstream)));
			item.setRecipe(ParserHelper.LoadString(dstream));
			return item;
		}
		if (c == 7) {
			ItemCaptureInstance item = new ItemCaptureInstance(findItem(ParserHelper.LoadString(dstream)));
			if (dstream.readBoolean()) {
				item.setShip(ParserHelper.LoadString(dstream));
			}
			return item;
		}
		if (c == 8) {
			ItemContainerInstance item = new ItemContainerInstance(findItem(ParserHelper.LoadString(dstream)));
			item.load(dstream);
			return item;
		}
		if (c == 9) {
			QuestItem item = new QuestItem();
			item.load(dstream);
			return item;
		}	
		return null;
	}

	public String readItemDesc(String name) {

		Item it = findItem(name);
		if (it != null) {
			return it.getDescription();
		}
		return "";
	}

}
