package solarview.spaceEncounter.effectHandling;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import rendering.SpriteManager;
import shared.Vec2f;
import solarview.spaceEncounter.EncounterEntities.CombatAction;
import solarview.spaceEncounter.EncounterEntities.EncounterShip;
import solarview.spaceEncounter.effectHandling.effects.Effect;
import solarview.spaceEncounter.effectHandling.effects.EffectText;

public class EffectHandler implements EffectHandler_Interface {

	List<EffectScript> scripts;
	List<Effect> effects;
	SpriteManager manager;
	
	public EffectHandler()
	{
		scripts=new ArrayList<EffectScript>();
		effects=new ArrayList<Effect>();
		manager=new SpriteManager("assets/art/encounter/effects/");
		
	}
	
	public void update(float dt)
	{
		for (int i=0;i<scripts.size();i++)
		{
			scripts.get(i).update(dt);
			if (!scripts.get(i).isAlive())
			{
				scripts.get(i).discard(effects);
				scripts.remove(i);
				i--;
			}
		}		
		for (int i=0;i<effects.size();i++)
		{
			effects.get(i).update(dt);
			if (!effects.get(i).keepAlive())
			{
				manager.removeSprite(effects.get(i).getSprite(), effects.get(i).getSheet());
				effects.remove(i);
				i--;
			}
		}
	}
	
	public void draw(FloatBuffer matrix44Buffer, int objmatrix, int tintvar) {

		manager.draw(objmatrix, tintvar, matrix44Buffer);
	}

	public void discard() {
		manager.discard();
	}

	public void addEffect(Effect effect)
	{
		effects.add(effect);
		manager.addSprite(effect.getSprite(), effect.getSheet());
	}
	
	public void addScript(EncounterShip origin,CombatAction action, boolean miss) {
		
		scripts.add(new EffectScript(origin,action,this,miss));
	}

	@Override
	public void removeEffect(Effect effect) {
		manager.removeSprite(effect.getSprite(), effect.getSheet());
		effects.remove(effect);
	}

	@Override
	public void drawText(Vec2f position,String text, int type) {
		EffectText e=new EffectText(position,type,text);
		manager.addSprite(e.getSprite(),null);
		effects.add(e);
	}

	public SpriteManager getSpriteManager() {
		return manager;
	}


}
