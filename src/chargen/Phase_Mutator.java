package chargen;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actor.player.Player;
import perks.Perk;
import perks.PerkLibrary;

public class Phase_Mutator implements Phase {

	String phaseName;
	ArrayList<ChargenMutator> mutatorList;

	public Phase_Mutator(Element n) {
		// TODO Auto-generated constructor stub
		mutatorList = new ArrayList<ChargenMutator>();
		// populate perklist
		NodeList children = n.getChildNodes();
		phaseName = n.getAttribute("name");
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) node;
				// run each step successively
				if (Enode.getTagName() == "mutation") {
					mutatorList.add(new ChargenMutator(Enode));
				}
			}
		}
	}

	@Override
	public String[] getChoices() {

		String str[] = new String[mutatorList.size()];
		for (int i = 0; i < mutatorList.size(); i++) {
			str[i] = mutatorList.get(i).getName();
		}
		return str;
	}

	@Override
	public int getChoiceCount() {

		return mutatorList.size();
	}

	@Override
	public String getChoiceDescription(int index) {

		return mutatorList.get(index).getDescription();
	}

	@Override
	public void performChoice(int index, Player player) {
		for (int i = 0; i < mutatorList.get(index).getEffectCount(); i++) {
			player.ApplyEffect(mutatorList.get(index).getEffect(i));
		}

	}

	@Override
	public String getName() {

		return phaseName;
	}

	@Override
	public void rollback(Player player) {

	}

}
