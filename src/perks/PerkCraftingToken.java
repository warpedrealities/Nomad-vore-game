package perks;

import org.w3c.dom.Element;

public class PerkCraftingToken extends PerkElement {

	private String token;
	
	public PerkCraftingToken(Element node) {
		token=node.getAttribute("token");
	}

	public String getToken() {
		return token;
	}

	
}
