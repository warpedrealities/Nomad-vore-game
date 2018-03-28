package worldgentools;

import org.w3c.dom.Element;

import item.Item;
import item.instances.ItemBlueprintInstance;
import nomad.universe.Universe;

public class LootStub {

	String item;
	float chance;
	String addendum;

	public LootStub(Element enode) {
		item = enode.getAttribute("item");
		chance = Float.parseFloat(enode.getAttribute("chance"));
		if (enode.getAttribute("addendum").length()>0)
		{
			addendum=enode.getAttribute("addendum");
		}
	}

	public LootStub(String item, int chance) {
		this.item = item;
		this.chance = chance;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public float getChance() {
		return chance;
	}

	public void setChance(float chance) {
		this.chance = chance;
	}

	public String getAddendum() {
		return addendum;
	}

	public void runCommon(Item item) {
		if (ItemBlueprintInstance.class.isInstance(item))
		{
			ItemBlueprintInstance ibi=(ItemBlueprintInstance)item;
			ibi.setRecipe(addendum);
		}
	}

}
