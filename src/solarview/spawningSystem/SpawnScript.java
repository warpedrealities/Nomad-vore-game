package solarview.spawningSystem;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.w3c.dom.Element;

import nomad.Entity;

public class SpawnScript {
	private Globals m_globals;
	private LuaValue m_script;
	private String filename;
	
	public SpawnScript(Element e) {
		m_globals = JsePlatform.standardGlobals();
		filename=e.getAttribute("script");
		try {
			m_script = m_globals.load(new FileReader("assets/data/systems/spawnScripts/" + filename + ".lua"), "main.lua");
			
		} catch (FileNotFoundException exception) {

			exception.printStackTrace();
		} catch (LuaError exception) {
			exception.printStackTrace();
		}	
	}

	public void run(ArrayList<Entity> entitiesInSystem, List<Entity> systemEntities) {
		// TODO Auto-generated method stub
		try {
			SpawnScriptTools tools=new SpawnScriptTools(entitiesInSystem,systemEntities);
			m_script.call();
			LuaValue luaTools = CoerceJavaToLua.coerce(tools);
			LuaValue luacontrol = m_globals.get("main");
			if (!luacontrol.isnil()) {
				luacontrol.call(luaTools);
			} else {
				System.out.println("Lua function not found");
			}
		} catch (LuaError e) {
			System.out.println("filename:"+filename);
			e.printStackTrace();
		}	
	}

	
	
	
}
