package solarview.spaceEncounter.effectHandling;

import shared.Vec2f;
import solarview.spaceEncounter.effectHandling.effects.Effect;

public interface EffectHandler_Interface {

	void addEffect(Effect effect);
	
	void removeEffect(Effect effect);
	
	void drawText(Vec2f position,String text, int type);
}
