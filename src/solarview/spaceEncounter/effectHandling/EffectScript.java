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
import solarview.spaceEncounter.effectHandling.effects.Effect;
import solarview.spaceEncounter.effectHandling.effects.EffectBeam;
import solarview.spaceEncounter.effectHandling.effects.EffectSprite;
import solarview.spaceEncounter.interfaces.CombatAction;
import solarview.spaceEncounter.interfaces.EncounterShip;

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
	private float dt;

	public EffectScript(EncounterShip origin, CombatAction action, EffectHandler_Interface effectHandler,
			boolean miss) {
		this.origin=origin;
		this.miss=miss;
		this.action=action;
		this.sheet = action.getEffectSheet();
		this.effectHandler=effectHandler;
		effects=new Effect[16];
		clock=0;
		globals = JsePlatform.standardGlobals();
		try {
			this.script = globals.load(new FileReader("assets/data/ships/effectScripts/" +
					action.getEffectScript() + ".lua"),
					"main.lua");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void update(float dt)
	{
		this.dt=dt;
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

	public float getDT()
	{
		return dt;
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

	public Effect makeBeam(int index,Vec2f p,int startFrame,int numFrames,boolean loop,float size,float length)
	{
		EffectBeam effect=new EffectBeam(p,sheet+".png",1,startFrame,numFrames,length,loop);
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

	public double getAngle(float x0, float y0, float x1, float y1)
	{
		Vec2f p=new Vec2f(y1-y0,x0-x1);
		p.normalize();
		double angle = Math.atan2(p.y,p.x);
		if (angle<0)
		{
			angle=(Math.PI*2)+angle;
		}
		angle=angle/0.785398F;

		return angle;
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
