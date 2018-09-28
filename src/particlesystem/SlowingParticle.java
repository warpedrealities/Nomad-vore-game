package particlesystem;

public class SlowingParticle extends Particle {
	
	public void Update(float DT) {
		super.Update(DT);
		if (m_alive)
		{
			m_velocity.x*=1.0-DT;
			m_velocity.y*=1.0-DT;			
		}		
	}
}
