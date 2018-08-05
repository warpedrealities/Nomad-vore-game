package worldgentools.blockdungeon.advanced;

import org.w3c.dom.Element;

import zone.Zone;

public class AdvancedKeyBlock extends AdvancedStandardBlock {

	int keyHeat;

	AdvancedKeyBlock(Element element) {
		super(element);
		if (element.getAttribute("heat").length() > 0) {
			keyHeat = Integer.parseInt(element.getAttribute("heat"));
		}

	}

	@Override
	public int getKeyHeat() {
		return keyHeat;
	}

}
