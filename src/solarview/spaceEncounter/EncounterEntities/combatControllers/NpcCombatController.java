package solarview.spaceEncounter.EncounterEntities.combatControllers;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import solarview.spaceEncounter.EncounterWeaponController;
import solarview.spaceEncounter.EncounterEntities.CombatAction;
import solarview.spaceEncounter.EncounterEntities.EncounterShip;
import solarview.spaceEncounter.EncounterEntities.WeaponCheck;
import solarview.spaceEncounter.effectHandling.EffectHandler;

public class NpcCombatController implements CombatController {

	private Globals m_globals;
	private LuaValue m_script;
	private NpcCombatControllerSense sense;
	private EffectHandler effectHandler;

	
	public NpcCombatController(String file) {
		// load lua file
		sense=new NpcCombatControllerSense();
		m_globals = JsePlatform.standardGlobals();
		try {
			m_script = m_globals.load(new FileReader("assets/data/shipControllers/scripts/" + file + ".lua"), "main.lua");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (LuaError e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run(EncounterShip encounterShip,EncounterShip[]allShips, EffectHandler effect) {
		sense.setShips(allShips);
		sense.setControlledShip(encounterShip);
		try {
			m_script.call();
			LuaValue luascript = CoerceJavaToLua.coerce(this);
			LuaValue luaSense = CoerceJavaToLua.coerce(sense);	
			LuaValue luacontrol = m_globals.get("main");
			if (!luacontrol.isnil()) {
				luacontrol.call(luascript,luaSense);
			} else {
				System.out.println("Lua function not found");
			}
		} catch (LuaError e) {
			e.printStackTrace();
		}
	}
	
	public void setCourse(int heading, int thrust)
	{
		byte value=0;
		if (heading==-1)
		{
			value+=1;
		}
		if (heading==1)
		{
			value+=2;
		}
		if (thrust==1)
		{
			value+=4;
		}
		if (thrust==2)
		{
			value+=8;
		}
		sense.getControlledShip().setCourse(value);
	}
	
	public void fire(int index, int target)
	{
		if (!sense.getControlledShip().getWeapons().get(index).isReady())
		{
			return;
		}
		
		if (!WeaponCheck.checkRange(sense.getControlledShip(),
				sense.getShips()[target], 
				sense.getControlledShip().getWeapons().get(index)))
		{
			return;
		}
		if (!WeaponCheck.checkArc(sense.getControlledShip(), 
				sense.getShips()[target], 
				sense.getControlledShip().getWeapons().get(index)))
		{
			return;
		}		
		sense.getControlledShip().getActions().add(new CombatAction(sense.getControlledShip().getWeapons().get(index),
				sense.getShips()[target],index));
		return;
	}

}
