package solarview.spaceEncounter.rendering;

import java.nio.FloatBuffer;

import particlesystem.ParticleEmitterAdvanced;
import particlesystem.particleEffectors.ParticleDraw;
import shared.Vec2f;

public class WarpEffect {

	private ParticleEmitterAdvanced particleEmitter;
	private int level;
	private float clock;
	private float interval;
	private boolean discharge;
	
	public WarpEffect(Vec2f position)
	{
		discharge=false;
		particleEmitter=new ParticleEmitterAdvanced(256, "particles/white", position.replicate(), 2, 2, 0.2F, true);
		float []start={0.5F,0.5F,1.0F};
		float []end={1.0F,0.0F,0.0F};
		
		particleEmitter.setColours(start,end);		

	}

	public void update(float dt)
	{
		particleEmitter.Update(dt);
		if (level>0)
		{
			clock+=dt;
			if (clock>interval && !discharge)
			{
				clock=0;
				particleEmitter.SpawnParticles(2);
			}
		}
	}
	
	public void draw(int viewMatrix, int objmatrix, int tintvar, FloatBuffer matrix44Buffer) {
		particleEmitter.draw(matrix44Buffer, objmatrix, tintvar);
	}
	
	public void discard()
	{
		particleEmitter.Discard();
	}

	public void setPosition(Vec2f position) {
		particleEmitter.setPosition(position.replicate());
	}

	public void setLevel(int level) {
		this.level = level;
		this.interval=1/((float)level);
	}

	public void discharge() {
		if (!discharge)
		{
			particleEmitter.runEffector(new ParticleDraw(particleEmitter.getPosition().replicate(),-8));
			
			discharge=true;
		}
	}

	public boolean isDischarge() {
		return discharge;
	}
	
	
}
