package solarview.spaceEncounter.effectHandling.effects;

import font.NuFont;
import rendering.Renderable;
import rendering.Sprite;
import rendering.SpriteFont;
import shared.Vec2f;

public class EffectText implements Effect {

	private SpriteFont font;
	
	private Vec2f position;
	private float clock;
	private float velocity;
	private float r,g,b;
	
	
	private void setType(int type)
	{
		switch (type)
		{
			case 0:
				r=1; g=0; b=0;
				velocity=1.0F;
			break;
			case 1:
				r=0; g=0; b=1;
				velocity=0.5F;
			break;
		}
	}
	
	public EffectText(Vec2f position, int type, String text)
	{
		this.position=position;
		this.clock=0;
		font=new SpriteFont(position,6,0.8F);
		font.setString(text);
		setType(type);
		font.setRGB(r, g, b);
	}
	
	@Override
	public void update(float dt) {
		clock+=dt;
		position.y+=velocity*dt;
		font.ResetPos(position);
		float v=1-(clock*0.5F);
		font.setRGB(r*v, g*v, b*v);
	}

	@Override
	public boolean keepAlive() {
		if (clock>1)
		{
			return false;
		}
		return true;
	}

	@Override
	public Renderable getSprite() {
		return font;
	}

	@Override
	public String getSheet() {
		return null;
	}

	@Override
	public void setVelocity(float x, float y) {

	}

	@Override
	public void setRotation(float angle) {

	}

	@Override
	public float getRotation() {
		return 0;
	}

}
