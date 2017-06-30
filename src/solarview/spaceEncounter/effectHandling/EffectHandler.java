package solarview.spaceEncounter.effectHandling;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import rendering.SpriteManager;
import solarview.spaceEncounter.EncounterEntities.CombatAction;
import solarview.spaceEncounter.effectHandling.effects.Effect;

public class EffectHandler {

	List<EffectScript> scripts;
	List<Effect> effects;
	SpriteManager manager;
	
	public EffectHandler()
	{
		scripts=new ArrayList<EffectScript>();
		effects=new ArrayList<Effect>();
		manager=new SpriteManager("assets/art/encounter/effects");
	}
	
	public void update(float dt)
	{
		for (int i=0;i<scripts.size();i++)
		{
			
		}		
		for (int i=0;i<effects.size();i++)
		{
			
		}
	}
	
	public void draw(FloatBuffer matrix44Buffer, int objmatrix, int tintvar) {

		manager.draw(objmatrix, tintvar, matrix44Buffer);
	}

	public void discard() {
		manager.discard();
	}

	public void addEffect(CombatAction action) {
		
		
	}

}
