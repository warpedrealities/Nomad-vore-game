package actorRPG.npc;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import nomad.FlagField;

public class Value_Calculator {

	class Value_Multiplier {

		public int minValue;
		public String flagName;
		public float multiplier;
		
		public Value_Multiplier(Element e) {
			minValue=Integer.parseInt(e.getAttribute("value"));
			flagName=e.getAttribute("flag");
			multiplier=Float.parseFloat(e.getAttribute("multiplier"));
		}	
	}
	
	private List<Value_Multiplier> multipliers;
	
	public Value_Calculator(Element enode) {
		multipliers=new ArrayList<Value_Multiplier>();
		NodeList children=enode.getChildNodes();
		for (int i=0;i<children.getLength();i++)
		{
			if (children.item(i).getNodeType()==Node.ELEMENT_NODE)
			{
				Element e=(Element)children.item(i);
				multipliers.add(new Value_Multiplier(e));
			}
		}
	}

	public float calcValue(FlagField flags)
	{
		float multiplier=1;
		for (int i=0;i<multipliers.size();i++)
		{
			multiplier+=processMultiplier(multipliers.get(i),flags);
		}
		return multiplier;
	}

	private float processMultiplier(Value_Multiplier value_Multiplier,FlagField flags) {
		int comp=flags.readFlag(value_Multiplier.flagName);
		if (comp>=value_Multiplier.minValue)
		{
			return value_Multiplier.multiplier;
		}
		return 0;
	}
	
}
