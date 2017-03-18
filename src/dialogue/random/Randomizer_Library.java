package dialogue.random;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import actor.Player_LOOK;
import description.Macro;
import description.MacroLibrary;
import description.Macro_Range;
import description.Macro_Value;
import shared.ParserHelper;

public class Randomizer_Library {

	private static Randomizer_Library instance;	

	static public Randomizer_Library getInstance()
	{
		if (instance==null)
		{
			instance=new Randomizer_Library();
		}
		return instance;
	}
	
	Map<String, Randomizer> randomizerMap;
	
	public Randomizer_Library()
	{
		randomizerMap=new HashMap<String,Randomizer>();
	}
	
	public String getRandomizedText(String name)
	{
		//check if it exists
		Randomizer it=randomizerMap.get(name);
		if (it==null)
		{
			//if not load it
			it=loadRandomizer(name);
		}
			
		//now use it
		return it.getRandom();
	}
	
	private Randomizer loadRandomizer(String name)
	{
		Document doc=ParserHelper.LoadXML("assets/data/textRandomizers/"+name+".xml");
		Element root=doc.getDocumentElement();
	    Element node=(Element)doc.getFirstChild();

	    	return new Randomizer(node);

	}
}
