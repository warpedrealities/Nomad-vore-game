package artificial_intelligence;

import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.script.Bindings;
import javax.script.CompiledScript;

import org.luaj.vm2.*;
import org.luaj.vm2.lib.jse.*;

import actor.npc.NPC;
import artificial_intelligence.detection.Sense;

public class Script_AI implements Controller {
	private Globals m_globals;
	private LuaValue m_script;

	private ScriptMemory localMemory;
	private ScriptMemory sharedMemory;
	private String m_name;

	boolean active;

	public class Memory
	{
		public ScriptMemory localMemory;
		public ScriptMemory sharedMemory;
		public Memory(ScriptMemory localMemory2, ScriptMemory sharedMemory2) {
			this.localMemory=localMemory2;
			this.sharedMemory=sharedMemory2;
		}
		public int getValue(int index) {
			return localMemory.getValue(index);
		}
		public void setValue(int index, int value) {
			localMemory.setValue(index, value);
		}
		public ScriptMemory getShared()
		{
			return sharedMemory;
		}
	};
	
	private Memory memory;
	
	public boolean getActive() {
		return active;
	}

	public void setActive(boolean value) {
		active = value;
	}

	public Script_AI(String file, ScriptMemory sharedMemory) {
		m_name = file;
		this.sharedMemory=sharedMemory;
		localMemory = new ScriptMemory();
		memory=new Memory(localMemory,sharedMemory);
		// load lua file
		m_globals = JsePlatform.standardGlobals();
		try {
			m_script = m_globals.load(new FileReader("assets/data/ai/" + file + ".lua"), "main.lua");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LuaError e) {
			System.err.println("script name"+m_name);
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
			LuaValue luaShared = CoerceJavaToLua.coerce(sharedMemory);			
			LuaValue luacontrollable = CoerceJavaToLua.coerce(controllable);
			LuaValue luasense = CoerceJavaToLua.coerce(senses);
			LuaValue luacontrol = m_globals.get("main");
			if (!luacontrol.isnil()) {
				luacontrol.call(luacontrollable, luasense, luascript);
			} else {
				System.out.println("Lua function not found");
			}
		} catch (LuaError e) {			
			System.err.println("script name"+m_name);			
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
