package worldgentools;

import org.w3c.dom.Element;

import item.Item;
import item.ItemCoin;
import item.QuestItem;
import item.instances.ItemBlueprintInstance;
import item.instances.ItemDepletableInstance;
import item.instances.ItemExpositionInstance;
import item.instances.ItemKeyInstance;
import nomad.universe.Universe;

public class LootEntry {

	String item;
	float chance;
	String addendum;
	int unique;

	public String getItem() {
		return item;
	}

	public float getChance() {
		return chance;
	}

	public String getAddendum() {
		return addendum;
	}

	public int isUnique() {
		return unique;
	}

	public void setUnique(int unique) {
		this.unique = unique;
	}

	public LootEntry(Element node) {
		if (node.getAttribute("item").length() > 0) {
			item = node.getAttribute("item");
		}

		chance = Float.parseFloat(node.getAttribute("chance"));
		if (!node.getAttribute("unique").isEmpty()) {
			unique = Integer.parseInt(node.getAttribute("unique"));
		}
		if (node.getAttribute("addendum").length() > 0) {
			addendum = node.getAttribute("addendum");
		}
	}

	protected Item commonFunction(Item item) {
		if (ItemDepletableInstance.class.isInstance(item) && addendum != null) {
			int value = Integer.parseInt(addendum);
			ItemDepletableInstance idi = (ItemDepletableInstance) item;
			idi.setEnergy(value);
		}
		if (ItemExpositionInstance.class.isInstance(item)) {
			ItemExpositionInstance iei = (ItemExpositionInstance) item;
			iei.setExposition(addendum);
		}
		if (ItemKeyInstance.class.isInstance(item)) {
			ItemKeyInstance iki = (ItemKeyInstance) item;
			iki.setLock(addendum);
		}
		if (ItemBlueprintInstance.class.isInstance(item) && addendum!=null) {
			ItemBlueprintInstance ibi = (ItemBlueprintInstance) item;
			ibi.setRecipe(addendum);
		}
		if (ItemCoin.class.isInstance(item)) {
			ItemCoin ic = (ItemCoin) item;
			ic.setCount(Integer.parseInt(getAddendum()));
		}
		return item;
	}

	public Item genLoot() {
		float chance = getChance() * 100;
		int roll = Universe.m_random.nextInt(100);

		if (roll < chance) {
			Item item=null;
			if (getItem().equals("questItem"))
			{
				item=new QuestItem(addendum);
			}
			else
			{
				item = Universe.getInstance().getLibrary().getItem(getItem());						
			}
			
			commonFunction(item);

			return item;

		}

		return null;
	}

}
