package solarview.spaceEncounter.effectHandling.effects;

import rendering.Renderable;
import rendering.Sprite;
import rendering.SpriteRotatable;
import shared.Vec2f;

public class EffectSprite implements Effect {

	private SpriteRotatable sprite;
	private Vec2f position;
	private String sheet;
	private int startFrame,currentFrame,numFrames;
	private boolean loop;
	private float clock,angle;
	private Vec2f velocity;
	
	public EffectSprite(Vec2f position, String sheet, int startFrame, int numFrames, boolean loop) {
		sprite=new SpriteRotatable(position, 16);
		sprite.setCentered(true);
		this.position=position;
		this.startFrame=startFrame;
		currentFrame=startFrame;
		this.numFrames=numFrames;
		this.sheet=sheet;
		this.loop=loop;
		clock=0;
		sprite.setImage(startFrame);
	}
	
	public void setVelocity(float x, float y)
	{
		velocity=new Vec2f(x,y);
	}
	
	public void setSize(float size)
	{
		sprite.setSpriteSize(size);
	}
	
	public Renderable getSprite()
	{
		return sprite;
	}

	public Vec2f getPosition()
	{
		return position;
	}
	@Override
	public void update(float dt) {
		sprite.setVisible(true);
		if (velocity!=null)
		{
			position.x+=velocity.x*dt;
			position.y+=velocity.y*dt;
		}
		sprite.repositionF(position);
		clock+=dt*8;
		if ((int)clock!=currentFrame-startFrame)
		{
			currentFrame=(int)clock+startFrame;
			if (currentFrame>=startFrame+numFrames && loop)
			{
				clock=0;
				currentFrame=startFrame;
			}
			sprite.setImage(currentFrame);			
		}	
	}

	
	@Override
	public boolean keepAlive() {
		if (!loop && currentFrame>=numFrames+startFrame)
		{
			return false;
		}
		return true;
	}

	@Override
	public String getSheet() {
		return sheet;
	}

	@Override
	public void setRotation(float angle) {
		this.angle=angle;
		sprite.setFacingF(angle);
	}

	@Override
	public float getRotation() {
		return angle;
	}

}
