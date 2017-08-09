package faction.violation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import faction.violation.FactionRule.ViolationType;

import shared.ParserHelper;

public class FactionRules {

	private Map<String, Map<ViolationType, FactionRule>> ruleHierarchy;
	private Set<String> witnessNames;

	public FactionRules(String zoneRules) {

		ruleHierarchy = new HashMap<String, Map<ViolationType, FactionRule>>();
		witnessNames = new HashSet<String>();
		Document doc = ParserHelper.LoadXML("assets/data/factionrules/" + zoneRules + ".xml");
		Element root = doc.getDocumentElement();
		Element n = (Element) doc.getFirstChild();

		NodeList children = n.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) node;
				if (Enode.getTagName() == "rule") {
					FactionRule rule = new FactionRule(Enode);

					Map<ViolationType, FactionRule> list = ruleHierarchy.get(rule.getSubjectName());
					if (list == null) {
						list = new HashMap<ViolationType, FactionRule>();
						ruleHierarchy.put(rule.getSubjectName(), list);
					}
					list.put(rule.getvType(), rule);
				}
				if (Enode.getTagName() == "superRule") {
					FactionRule rule = new Super_Rule(Enode);

					Map<ViolationType, FactionRule> list = ruleHierarchy.get(rule.getSubjectName());
					if (list == null) {
						list = new HashMap<ViolationType, FactionRule>();
						ruleHierarchy.put(rule.getSubjectName(), list);
					}
					list.put(rule.getvType(), rule);
				}
				if (Enode.getTagName() == "witness") {
					witnessNames.add(Enode.getAttribute("name"));
				}
			}
		}
	}

	public boolean getWitness(String name) {

		if (witnessNames.contains(name)) {
			return true;
		}
		return false;
	}

	public int violationLevel(String targetname, ViolationType vtype) {

		Map<ViolationType, FactionRule> ruleset = ruleHierarchy.get(targetname);
		if (ruleset != null) {
			FactionRule rule = ruleset.get(vtype);
			if (rule != null) {
				rule.violationAction();
				return rule.getViolationLevel();

			}
		}
		return 0;
	}

}
