package widgets.scriptedEvents;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import nomad.universe.Universe;
import shared.ParserHelper;
import view.ViewScene;
import widgets.Widget;

public class WidgetScriptedEvent extends Widget {

	boolean scriptTriggered;
	String scriptName;

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		dstream.write(20);
		ParserHelper.SaveString(dstream, scriptName);
		dstream.writeBoolean(scriptTriggered);
	}

	public WidgetScriptedEvent(DataInputStream dstream) throws IOException {
		scriptName = ParserHelper.LoadString(dstream);
		scriptTriggered = dstream.readBoolean();
		isWalkable = true;
	}

	public WidgetScriptedEvent(String scriptName) {
		this.scriptName = scriptName;
		isWalkable = true;
	}

	public boolean Step() {
		if (!scriptTriggered) {
			Globals globals = JsePlatform.standardGlobals();

			try {
				LuaValue script = globals.load(new FileReader("assets/data/scripts/" + scriptName + ".lua"),
						"main.lua");
				script.call();
				LuaValue view = CoerceJavaToLua.coerce(ViewScene.m_interface);
				LuaValue scriptedTools = CoerceJavaToLua
						.coerce(new ScriptedTools(Universe.getInstance().getCurrentZone()));
				LuaValue universe = CoerceJavaToLua.coerce(Universe.getInstance());
				LuaValue mainFunc = globals.get("main");
				mainFunc.call(view, universe, scriptedTools);
				ViewScene.m_interface.redraw();
			} catch (Exception e) {
				e.printStackTrace();
			}

			scriptTriggered = true;
			return true;
		}
		return false;
	}
}
