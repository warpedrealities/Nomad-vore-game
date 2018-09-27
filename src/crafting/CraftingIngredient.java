package crafting;

import org.w3c.dom.Element;

public class CraftingIngredient {

	private String itemName;
	private int itemQuantity;

	public CraftingIngredient(Element enode) {

		itemName = enode.getAttribute("item");
		itemQuantity = Integer.parseInt(enode.getAttribute("quantity"));
	}

	public String getName() {
		return itemName;
	}

	public int getQuantity() {
		return itemQuantity;
	}

}
