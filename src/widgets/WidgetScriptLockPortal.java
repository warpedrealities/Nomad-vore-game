package widgets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.w3c.dom.Element;

import shared.ParserHelper;
import view.ViewScene;

public class WidgetScriptLockPortal extends WidgetPortal {

	Globals luaGlobals;
	String filename, forbidText;
	LuaValue script;

	public WidgetScriptLockPortal(Element element) {
		super(element);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void save(DataOutputStream dstream) throws IOException {
		// TODO Auto-generated method stub
		dstream.write(27);
		commonSave(dstream);
		dstream.writeByte(portalFacing);
		dstream.writeInt(portalID);
		if (targetZone != null) {
			dstream.writeBoolean(true);
			ParserHelper.SaveString(dstream, targetZone);
		} else {
			dstream.writeBoolean(false);
		}
		if (targetPosInZone != null) {
			dstream.writeBoolean(true);
			targetPosInZone.Save(dstream);
		} else {
			dstream.writeBoolean(false);
		}

		ParserHelper.SaveString(dstream, filename);
		ParserHelper.SaveString(dstream, forbidText);
	}

	public WidgetScriptLockPortal(DataInputStream dstream) throws IOException {
		super(dstream);
		luaGlobals = JsePlatform.standardGlobals();
		filename = ParserHelper.LoadString(dstream);
		forbidText = ParserHelper.LoadString(dstream);
		loadScript();
	}

	public WidgetScriptLockPortal(int sprite, String description, int id) {
		super(sprite, description, id);
	}

	private void loadScript() {
		try {
			script = luaGlobals.load(new FileReader("assets/data/scripts/scriptlockportal/" + filename + ".lua"),
					"main.lua");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LuaError e) {
			e.printStackTrace();
		}
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	private boolean check() {
		Globals globals = JsePlatform.standardGlobals();
		boolean evaluatedScriptValue = false;
		try {
			script.call();
			LuaValue view = CoerceJavaToLua.coerce(ViewScene.m_interface);
			LuaValue player = CoerceJavaToLua
					.coerce(ViewScene.m_interface.getSceneController().getUniverse().getPlayer());
			LuaValue mainFunc = globals.get("main");
			LuaValue returnVal = mainFunc.call(view, player);
			evaluatedScriptValue = (boolean) CoerceLuaToJava.coerce(returnVal, Boolean.class);
			if (evaluatedScriptValue) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean Step() {
		if (check()) {
			return super.Step();
		} else {
			ViewScene.m_interface.DrawText(forbidText);
		}
		return false;
	}

	public void setForbidText(String forbidText) {
		this.forbidText = forbidText;
	}
}
