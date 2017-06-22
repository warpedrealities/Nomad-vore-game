package chargen;

import mutation.Effect_Mutator;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ChargenMutator {

	private String name;
	private String description;
	private List<Effect_Mutator> effects;

	public ChargenMutator(Element node) {
		name = node.getAttribute("name");
		description = node.getAttribute("descriptor");
		NodeList nlist = node.getElementsByTagName("effect");
		effects = new ArrayList<Effect_Mutator>();
		for (int i = 0; i < nlist.getLength(); i++) {
			effects.add(new Effect_Mutator((Element) nlist.item(i)));
		}
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public int getEffectCount() {
		return effects.size();
	}

	public Effect_Mutator getEffect(int i) {
		return effects.get(i);
	}

}
