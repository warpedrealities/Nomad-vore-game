package widgets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import nomad.Universe;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.w3c.dom.Element;

import shared.ParserHelper;
import view.ViewScene;

public class WidgetScriptPortal extends WidgetPortal {

	Globals luaGlobals;
	String filename;
	LuaValue script;
	
	public WidgetScriptPortal(int sprite, String desc,int id)
	{
		super(sprite,desc,id);
		luaGlobals = JsePlatform.standardGlobals();
	}
	
	public WidgetScriptPortal(Element element)
	{
		super(element);
		luaGlobals = JsePlatform.standardGlobals();
		
	}
		
	public void setFilename(String filename) {
		this.filename = filename;
		loadScript();
	}

	@Override
	public
	void save(DataOutputStream dstream) throws IOException {
		// TODO Auto-generated method stub
		dstream.write(16);
		commonSave(dstream);
		dstream.writeByte(portalFacing);
		dstream.writeInt(portalID);		
		if (targetZone!=null)
		{
			dstream.writeBoolean(true);
			ParserHelper.SaveString(dstream, targetZone);
		}
		else
		{
			dstream.writeBoolean(false);
		}
		if (targetPosInZone!=null)
		{
			dstream.writeBoolean(true);
			targetPosInZone.Save(dstream);
		}
		else
		{
			dstream.writeBoolean(false);
		}
		
		ParserHelper.SaveString(dstream, filename);
		
	}
	public WidgetScriptPortal(DataInputStream dstream) throws IOException
	{
		super(dstream);
		luaGlobals = JsePlatform.standardGlobals();
		filename=ParserHelper.LoadString(dstream);
		loadScript();
	}
	
	private void loadScript()
	{
		try {
			script = luaGlobals.load(new FileReader("assets/data/scripts/"+filename+".lua"), "main.lua");
				
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (LuaError e)
		{  
	        e.printStackTrace();  
		}			
	}
	
	public boolean Step()
	{
		try 
		{ 	
			script.call();
			LuaValue luaflags = CoerceJavaToLua.coerce(Universe.getInstance().getPlayer().getFlags());
	        LuaValue luacontrol = luaGlobals.get("main");  
	        LuaValue luascreen= CoerceJavaToLua.coerce(ViewScene.m_interface);
	        if (!luacontrol.isnil()) 
	        {
	    		luacontrol.call(luaflags,luascreen);
	        }
	        else
	        {
	            System.out.println("Lua function not found");  	
	        }
		 } 
			catch (LuaError e)
		 {  
	           e.printStackTrace();  
		 }
			return super.Step();

	}
}
