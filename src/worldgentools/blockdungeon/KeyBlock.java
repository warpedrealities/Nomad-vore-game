package worldgentools.blockdungeon;

import org.w3c.dom.Element;

import zone.Zone;

public class KeyBlock extends StandardBlock {

	int keyHeat;

	KeyBlock(Element element) {
		super(element);
		// TODO Auto-generated constructor stub
		if (element.getAttribute("heat").length() > 0) {
			keyHeat = Integer.parseInt(element.getAttribute("heat"));
		}

	}

	@Override
	public int getKeyHeat() {
		return keyHeat;
	}

}
