package description;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import actor.player.Player_LOOK;
import shared.ParserHelper;

public class MacroLibrary {

	private static MacroLibrary instance;

	static public MacroLibrary getInstance() {
		if (instance == null) {
			instance = new MacroLibrary();
		}
		return instance;
	}

	Map<String, Macro> macroMap;

	public MacroLibrary() {
		macroMap = new HashMap<String, Macro>();
	}

	public String lookupMacro(Player_LOOK look, String name) {
		// check if it exists
		Macro m = macroMap.get(name);
		if (m == null) {
			// if not load it
			m = loadMacro(name);
		}

		// now use it
		return m.readMacro(look);
	}

	private Macro loadMacro(String name) {
		Document doc = ParserHelper.LoadXML("assets/data/description/macros/" + name + ".xml");
		Element root = doc.getDocumentElement();
		Element node = (Element) doc.getFirstChild();

		if (node.getTagName().equals("macrorange")) {
			return new Macro_Range(node, name);

		}

		if (node.getTagName().equals("macrovalue")) {
			return new Macro_Value(node, name);

		}
		return null;

	}

}
