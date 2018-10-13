package actorRPG.npc.conditionalDescription;

import org.w3c.dom.Element;

public class FlagCriteria {

	public enum Comparison {
		LESSTHAN, EQUAL, MORETHAN
	};

	public enum FlagType {
		LOCAL, FACTION, GLOBAL
	};

	private Comparison comparison;
	private FlagType flagType;
	private String flagName;
	private int value;

	public FlagCriteria(Element enode) {
		value = Integer.parseInt(enode.getAttribute("value"));
		comparison = Comparison.valueOf(enode.getAttribute("comparison"));
		flagType = FlagType.valueOf(enode.getAttribute("flagType"));
		flagName = enode.getAttribute("flagName");
	}

	public Comparison getComparison() {
		return comparison;
	}

	public FlagType getFlagType() {
		return flagType;
	}

	public int getValue() {
		return value;
	}

	public String getFlagName() {
		return flagName;
	}

}
