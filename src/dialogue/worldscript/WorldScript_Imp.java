package dialogue.worldscript;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import entities.Entity;

public class WorldScript_Imp implements WorldScript {

	private WorldManipulator entity;
	private LuaValue script;
	private Globals globals;
	
	public WorldScript_Imp(Entity entity)
	{
		this.entity=new WorldManipulator_Imp(entity);
	}
	
	@Override
	public void initialize(String scriptName) {
		globals= JsePlatform.standardGlobals();
		try {
			script = globals.load(new FileReader("assets/data/conversations/worldScripts/" +scriptName + ".lua"), "main.lua");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		script.call();
		LuaValue mainFunc = globals.get("main");
		LuaValue luaEntity=CoerceJavaToLua.coerce(entity);
		mainFunc.call(luaEntity);		
	}

}
