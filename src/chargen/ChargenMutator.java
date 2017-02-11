package chargen;

import mutation.Effect_Mutator;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;



public class ChargenMutator {

	private String name;
	private String description;
	private Effect_Mutator effect;
	
	public ChargenMutator(Element node)
	{
		name=node.getAttribute("name");
		description=node.getAttribute("descriptor");
		NodeList nlist=node.getElementsByTagName("effect");
		effect=new Effect_Mutator((Element)nlist.item(0));
		
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Effect_Mutator getEffect() {
		return effect;
	}
	
	
	
}
