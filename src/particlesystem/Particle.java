package particlesystem;

import shared.Vec2f;

public class Particle {

	boolean m_alive;
	Vec2f m_position;
	Vec2f m_velocity;
	float m_lifespan;
	
	
	public Particle()
	{
		
	}
	
	public void Spawn(Vec2f position, Vec2f velocity, float life)
	{
		m_position=new Vec2f(position.x,position.y);
		m_velocity=new Vec2f(velocity.x,velocity.y);
		m_lifespan=life;
		m_alive=true;
	}
	
	public void Update(float DT)
	{
		if (m_alive==true)
		{
			m_position.x+=m_velocity.x*DT;
			m_position.y+=m_velocity.y*DT;
			
			m_lifespan-=DT;
			if (m_lifespan<=0)
			{
				m_alive=false;
			}
		}
	}
}
