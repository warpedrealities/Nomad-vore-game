package worldgentools;

import org.w3c.dom.Element;

import nomad.universe.Universe;

public class LootStub {

	String item;
	float chance;

	public LootStub(Element enode) {
		item = enode.getAttribute("item");
		chance = Float.parseFloat(enode.getAttribute("chance"));
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

}
