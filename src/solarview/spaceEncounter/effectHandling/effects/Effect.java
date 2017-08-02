package solarview.spaceEncounter.effectHandling.effects;

import rendering.Renderable;
import rendering.Sprite;
import shared.Vec2f;

public interface Effect {

	void update(float dt);

	boolean keepAlive();

	Renderable getSprite();
	
	String getSheet();
	
	void setVelocity(float x, float y);
	Vec2f getVelocity();
	void setRotation(float angle);
	float getRotation();
	
	public void setAnimationSpeed(float speed);
	
}
