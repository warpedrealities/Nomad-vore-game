package artificial_intelligence;

import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.script.Bindings;
import javax.script.CompiledScript;

import org.luaj.vm2.*;
import org.luaj.vm2.lib.jse.*;

import actor.npc.NPC;

public class Script_AI implements Controller {
	Globals m_globals;
	LuaValue m_script;

	ScriptMemory memory;
	String m_name;

	boolean active;

	public boolean getActive() {
		return active;
	}

	public void setActive(boolean value) {
		active = value;
	}

	public Script_AI(String file) {
		m_name = file;

		memory = new ScriptMemory();

		// load lua file
		m_globals = JsePlatform.standardGlobals();
		try {
			m_script = m_globals.load(new FileReader("assets/data/ai/" + file + ".lua"), "main.lua");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LuaError e) {
			e.printStackTrace();
		}
	}

	public String getName() {
		return m_name;
	}

	public void RunAI(NPC controllable, Sense senses) {
		active = true;
		try {
			m_script.call();
			LuaValue luascript = CoerceJavaToLua.coerce(memory);
			LuaValue luacontrollable = CoerceJavaToLua.coerce(controllable);
			LuaValue luasense = CoerceJavaToLua.coerce(senses);
			LuaValue luacontrol = m_globals.get("main");
			if (!luacontrol.isnil()) {
				luacontrol.call(luacontrollable, luasense, luascript);
			} else {
				System.out.println("Lua function not found");
			}
		} catch (LuaError e) {
			e.printStackTrace();
		}

	}

	public void runMaster() {
		// TODO Auto-generated method stub
		try {
			m_script.call();
			LuaValue luascript = CoerceJavaToLua.coerce(memory);
			LuaValue luacontrol = m_globals.get("tick");
			if (!luacontrol.isnil()) {
				luacontrol.call(luascript);
			} else {

			}
		} catch (LuaError e) {
			e.printStackTrace();
		}
	}

}
