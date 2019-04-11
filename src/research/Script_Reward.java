package research;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.w3c.dom.Element;

import actor.player.Player;
import nomad.FlagField;
import nomad.playerScreens.journal.JournalEntry;
import nomad.universe.Universe;

public class Script_Reward implements Entry_Reward {

	Universe universe;
	String filename;
	private Globals m_globals;
	private LuaValue m_script;

	public Script_Reward(Element e) {
		universe = Universe.getInstance();
		m_globals = JsePlatform.standardGlobals();
		filename = e.getAttribute("script");
		try {
			m_script = m_globals.load(new FileReader("assets/data/scripts/encyclopedia/rewards/" + filename + ".lua"),
					"main.lua");

		} catch (FileNotFoundException exception) {

			exception.printStackTrace();
		} catch (LuaError exception) {
			exception.printStackTrace();
		}
	}

	@Override
	public void runReward(Player player) {
		try {
			Script_Reward tools = this;
			m_script.call();
			LuaValue luaTools = CoerceJavaToLua.coerce(tools);
			LuaValue luacontrol = m_globals.get("main");
			if (!luacontrol.isnil()) {
				luacontrol.call(luaTools);
			} else {
				System.out.println("Lua function not found");
			}
		} catch (LuaError e) {
			System.out.println("filename:" + filename);
			e.printStackTrace();
		}
	}

	public FlagField getGlobalFlags() {
		return universe.getPlayer().getFlags();
	}

	public void addJournal(String file, String name) {
		this.universe.getJournal().addEntry(new JournalEntry(file, name));
	}
}
