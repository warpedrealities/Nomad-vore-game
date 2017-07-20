package solarview.spaceEncounter.effectHandling;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;
import org.luaj.vm2.lib.jse.JsePlatform;

import shared.Vec2f;
import solarview.spaceEncounter.EncounterEntities.CombatAction;
import solarview.spaceEncounter.EncounterEntities.EncounterShip;
import solarview.spaceEncounter.effectHandling.effects.Effect;
import solarview.spaceEncounter.effectHandling.effects.EffectSprite;

public class EffectScript {

	private boolean miss;
	private CombatAction action;
	private EffectHandler_Interface effectHandler;
	private Effect[] effects;
	private float clock;
	private String sheet;
	private Globals globals;
	private LuaValue script,function;
	private boolean complete;
	private EncounterShip origin;
	
	public EffectScript(EncounterShip origin, CombatAction action, EffectHandler_Interface effectHandler, boolean miss) {
		this.origin=origin;
		this.miss=miss;
		this.action=action;
		this.sheet=action.getWeapon().getWeapon().getWeapon().getEffectSheet();
		this.effectHandler=effectHandler;
		effects=new Effect[16];
		clock=0;
		globals = JsePlatform.standardGlobals();
		try {
			this.script = globals.load(new FileReader("assets/data/ships/effectScripts/" + 
					action.getWeapon().getWeapon().getWeapon().getEffectScript() + ".lua"),
					"main.lua");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	public void update(float dt)
	{
		clock+=dt;
		script.call();
		function= globals.get("main");
		LuaValue lThis= CoerceJavaToLua.coerce(this);

		LuaValue lClock=CoerceJavaToLua.coerce(clock);	
		LuaValue returnVal;
		LuaValue lBoolean=CoerceJavaToLua.coerce(miss);	

		returnVal= function.call(lThis,lClock,lBoolean);
		complete = (boolean) CoerceLuaToJava.coerce(returnVal, Boolean.class);	
	}
	
	public Effect getEffect(int i)
	{
		return effects[i];
	}
	
	public void removeEffect(int i)
	{
		effectHandler.removeEffect(effects[i]);
	}
	
	public Effect makeSprite(int index,Vec2f p,int startFrame,int numFrames,boolean loop,float size)
	{
		EffectSprite effect=new EffectSprite(p,sheet+".png",startFrame,numFrames,loop);
		effect.setSize(size);
		effectHandler.addEffect(effect);
		effects[index]=effect;
		return effects[index];
	}


	public void resolve()
	{
		complete=true;
		//apply damage
		action.getTarget().attack(origin.getPosition().getDistance(action.getTarget().getPosition()),action, effectHandler);
	}	
	
	public boolean isAlive()
	{
		if (complete)
		{
			return false;
		}
		
		return true;
		
	}
	
	public double getAngle(float x, float y)
	{	
		double angle = Math.toDegrees(Math.atan2(y, x))-90;
		return angle/45;
	}
	
	public Vec2f getOrigin()
	{
		return origin.getPosition();
	}
	
	public Vec2f getEmitter()
	{
		return origin.getEmitter(action.getWeaponIndex());
	}
	
	public Vec2f getTarget()
	{
		return action.getTarget().getPosition();
	}
	public Vec2f leadTarget(float v)
	{
		
		return action.getTarget().getLeading(v);
	}
		
	public void discard(List<Effect> effects)
	{
		
	}
	//each script controls 1 or more effects
	
	//scripts require a clock
	
	//each script needs its origin, emitter number, hit/miss

	//each script needs its target also
	
}
