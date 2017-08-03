package particlesystem.particleEffectors;

import particlesystem.Particle;
import shared.Vec2f;

public class ParticleDraw implements ParticleEffector {

	private Vec2f position;
	private float force;
	private Vec2f vector;
	
	public ParticleDraw(Vec2f position, float force)
	{
		this.force=force;
		this.position=position;
		vector=new Vec2f(0,0);
	}
	
	@Override
	public void effect(Particle particle) {
		vector.x=particle.getPosition().x;
		vector.y=particle.getPosition().y;
		vector.subtract(position);
		vector.normalize();
		vector.x*=-force;
		vector.y*=-force;
		particle.setVelocity(vector.replicate());
	}

}
