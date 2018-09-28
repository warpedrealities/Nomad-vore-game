package solarview.spaceEncounter.effectHandling.effects;

import rendering.Renderable;
import rendering.SpriteBeam;
import shared.Vec2f;

public class EffectBeam implements Effect {

	private SpriteBeam beam;
	private Vec2f position;
	private String sheet;
	private int startFrame,currentFrame,numFrames;
	private boolean loop;
	private float speed=1;
	private float clock;
	
	public EffectBeam(Vec2f p, String sheet, float size, int startFrame, int numFrames, float length,boolean loop) 
	{
		this.position=p;
		beam=new SpriteBeam(p,16,size,length);
		this.startFrame=startFrame;
		this.currentFrame=startFrame;
		this.numFrames=numFrames;
		this.loop=loop;
		this.sheet=sheet;
		
	}

	@Override
	public void update(float dt) {
		
		beam.repositionF(position);
		if (numFrames>1)
		{
			clock+=dt*8*speed;
			if ((int)clock!=currentFrame-startFrame)
			{
				currentFrame=(int)clock+startFrame;
				if (currentFrame>=startFrame+numFrames && loop)
				{
					clock=0;
					currentFrame=startFrame;
				}
				beam.setImage(currentFrame);			
			}				
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
	public Renderable getSprite() {
		return beam;
	}

	@Override
	public String getSheet() {
		return sheet;
	}

	
	@Override
	public void setVelocity(float x, float y) {
		beam.setLength(currentFrame, x);
	}

	@Override
	public void setRotation(float angle) {
		beam.setFacing(angle);
	}

	@Override
	public float getRotation() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setSize(float size) {
		beam.setWidth(size);
	}

	@Override
	public void setAnimationSpeed(float speed) {
		this.speed=speed;
	}

	@Override
	public Vec2f getVelocity() {
		// TODO Auto-generated method stub
		return null;
	}

}
