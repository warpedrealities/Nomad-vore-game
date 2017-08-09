package particlesystem;

import shared.Vec2f;
import vmo.GameManager;

public class ParticleConeEmitter extends ParticleEmitter {

	public ParticleConeEmitter(int topcount, String sprite, Vec2f position, float maxspeed, float scale) {
		super(topcount, sprite, position, maxspeed, scale);
	}
	
	public void spawnCone(int count, Float facing,float width)
	{
		for (int i = 0; i < m_particles.length; i++) {
			if (m_particles[i].m_alive == false) {
				// generate random velocity
				float v = GameManager.m_random.nextFloat() * (maxSpeed * 0.5F);
				v = v + maxSpeed / 2;
				float angle = facing-(width/2);
				angle+=GameManager.m_random.nextFloat()*width;
				Vec2f vel = new Vec2f(0,-v);
				vel.rotate(angle);
				m_particles[i].Spawn(position, vel, 4);
				count--;
			}

			if (count <= 0) {
				break;
			}
		}		
	}

}
