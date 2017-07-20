package solarview.spaceEncounter.effectHandling.effects;

import rendering.Renderable;
import rendering.Sprite;

public interface Effect {

	void update(float dt);

	boolean keepAlive();

	Renderable getSprite();
	
	String getSheet();
	
	void setVelocity(float x, float y);
	
	void setRotation(float angle);
	float getRotation();
	
}
