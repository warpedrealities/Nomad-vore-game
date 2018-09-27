package faction.violation;

import org.w3c.dom.Element;

public class FactionRule {

	public enum ViolationType {
		Interact, Attack, Seduce
	};

	private String subjectName;
	private int violationLevel;
	private ViolationType vType;

	public FactionRule(Element node) {
		subjectName = node.getAttribute("subject");
		violationLevel = Integer.parseInt(node.getAttribute("level"));
		vType = stringToVType(node.getAttribute("violation"));
	}

	public static ViolationType stringToVType(String str) {
		if (str.contains("INTERACT")) {
			return ViolationType.Interact;
		}
		if (str.contains("ATTACK")) {
			return ViolationType.Attack;
		}
		if (str.contains("SEDUCE")) {
			return ViolationType.Seduce;
		}

		return ViolationType.Attack;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public int getViolationLevel() {
		return violationLevel;
	}

	public ViolationType getvType() {
		return vType;
	}

	public void violationAction() {
	}

}
