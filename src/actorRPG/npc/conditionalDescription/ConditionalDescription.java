package actorRPG.npc.conditionalDescription;

import java.util.ArrayList;
import java.util.List;

import javax.xml.soap.Node;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import actorRPG.npc.conditionalDescription.FlagCriteria.FlagType;
import faction.Faction;
import nomad.FlagField;
import nomad.universe.Universe;

public class ConditionalDescription {

	private List<FlagCriteria> flagCriteria;
	private String text;
	public ConditionalDescription(Element enode) {
		flagCriteria = new ArrayList<FlagCriteria>();

		NodeList children = enode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) children.item(i);

				if (e.getTagName().equals("text")) {
					text = e.getTextContent();
				}
				if (e.getTagName().equals("criteria")) {
					flagCriteria.add(new FlagCriteria(e));
				}
			}
		}
	}

	public boolean isActive(FlagField flags, Faction actorFaction) {
		for (int i = 0; i < flagCriteria.size(); i++) {
			if (!processCriteria(flagCriteria.get(i), flags, actorFaction)) {
				return false;
			}
		}
		return true;
	}

	private boolean processCriteria(FlagCriteria criteria, FlagField flags, Faction actorFaction) {
		int value = getComparisonValue(criteria.getFlagType(), criteria.getFlagName(), flags, actorFaction);
		int comparison = criteria.getValue();
		switch (criteria.getComparison()) {
		case LESSTHAN:
			return value < comparison;

		case EQUAL:
			return comparison == value;

		case MORETHAN:
			return value >= comparison;

		}
		return false;
	}

	private int getComparisonValue(FlagType flagType, String flagName, FlagField flags, Faction actorFaction) {
		switch (flagType) {
		case LOCAL:
			return flags.readFlag(flagName);

		case FACTION:
			return actorFaction.getFactionFlags().readFlag(flagName);
		case GLOBAL:
			return Universe.getInstance().getPlayer().getFlags().readFlag(flagName);
		default:
			return 0;
		}

	}

	public String getText() {
		return text;
	}

}
