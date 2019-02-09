package entities.scripting;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import nomad.universe.Universe;

public class ScriptTool {
	private Globals m_globals;
	private LuaValue m_script;
	private String file;

	public ScriptTool(String file) {
		this.file = file;
		m_globals = JsePlatform.standardGlobals();
		try {
			m_script = m_globals.load(new FileReader("assets/data/systems/entryScripts/" + file + ".lua"), "main.lua");

		} catch (FileNotFoundException exception) {

			exception.printStackTrace();
		} catch (LuaError exception) {
			exception.printStackTrace();
		}
	}

	public void run() {
		try {
			m_script.call();
			LuaValue tools = CoerceJavaToLua.coerce(new EntryScriptToolkit(Universe.getInstance()));
			LuaValue luacontrol = m_globals.get("main");
			if (!luacontrol.isnil()) {
				luacontrol.call(tools);
			} else {
				System.out.println("Lua function not found");
			}
		} catch (LuaError e) {
			System.out.println("filename:" + file);
			e.printStackTrace();
		}
	}

}
