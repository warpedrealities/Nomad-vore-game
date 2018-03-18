package widgets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.w3c.dom.Element;

import nomad.universe.Universe;
import shared.ParserHelper;
import zone.Zone;

public class WidgetScripted extends WidgetBreakable {

	private String script;
	private int tile;
	private boolean visible;

	public WidgetScripted(Element enode) {
		super(enode);
		tile=widgetSpriteNumber;
		visible=true;
	}
	@Override
	public void save(DataOutputStream dstream) throws IOException 
	{
		dstream.write(22);
		commonSave(dstream);
		super.saveBreakable(dstream);

		ParserHelper.SaveString(dstream, script);
		dstream.writeBoolean(visible);
		dstream.writeInt(tile);
	}
	
	private boolean check()
	{
		Globals globals = JsePlatform.standardGlobals();
		boolean evaluatedScriptValue = false;
		try {
			LuaValue luaScript = globals.load(new FileReader("assets/data/scripts/" + script + ".lua"),
					"main.lua");
			luaScript.call();
			LuaValue flags = CoerceJavaToLua.coerce(Universe.getInstance().getPlayer().getFlags());
			LuaValue mainFunc = globals.get("main");
			LuaValue returnVal = mainFunc.call(flags);
			evaluatedScriptValue = (boolean) CoerceLuaToJava.coerce(returnVal, Boolean.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return evaluatedScriptValue;
	}
	
	@Override
	public void Regen(long clock, Zone zone) {
		if (check())
		{
			isWalkable=true;
			isVisionBlocking=false;
			widgetSpriteNumber=10;
			visible=false;
		}
		else
		{
			visible=true;
			isWalkable=false;
			isVisionBlocking=true;		
			widgetSpriteNumber=tile;
		}
	}

	@Override
	public String getDescription() {
		if (visible)
		{
			return widgetDescription;	
		}
		return "";
	}
	public WidgetScripted(DataInputStream dstream) throws IOException {

		commonLoad(dstream);
		load(dstream);
		
		script=ParserHelper.LoadString(dstream);
		visible=dstream.readBoolean();
		tile=dstream.readInt();
	}
	public void setScript(String script) {
		this.script=script;
	}
}
