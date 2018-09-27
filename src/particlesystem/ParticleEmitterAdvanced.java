package particlesystem;

import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import particlesystem.particleEffectors.ParticleEffector;
import shared.Tools;
import shared.Vec2f;
import vmo.GameManager;

public class ParticleEmitterAdvanced extends ParticleEmitter {

	private float minSpeed;
	private float []colourStart;
	private float []colourEnd;
	private float []colourTransition;
	
	public ParticleEmitterAdvanced(int topcount, String sprite, Vec2f position, float maxspeed,float minSpeed, float scale,boolean slowParticles) {
		m_texture = Tools.loadPNGTexture("assets/art/" + sprite + ".png", GL13.GL_TEXTURE0);
		maxSpeed = maxspeed;

		m_particles = new Particle[topcount];
		for (int i = 0; i < m_particles.length; i++) {
			if (slowParticles)
			{
				m_particles[i] = new SlowingParticle();		
			}
			else
			{
				m_particles[i] = new Particle();		
			}
		}
		this.position = position;
		Generate(scale);
		m_matrix = new Matrix4f();
		Matrix4f.setIdentity(m_matrix);
		this.minSpeed=minSpeed;
	}
	
	public void setColours(float[] colourStart,float []colourEnd) {
		this.colourStart = colourStart;
		this.colourEnd=colourEnd;
		colourTransition=new float[3];
		colourTransition[0]=colourEnd[0]-colourStart[0];
		colourTransition[1]=colourEnd[1]-colourStart[1];
		colourTransition[2]=colourEnd[2]-colourStart[2];
	}

	public void SpawnParticles(int count,Vec2f v) {

		for (int i = 0; i < m_particles.length; i++) {
			if (m_particles[i].m_alive == false) {
				// generate random velocity
				Vec2f vel=v.replicate();
				m_particles[i].Spawn(position, vel, 4);
				count--;
			}

			if (count == 0) {
				break;
			}
		}

	}	
	
	public void runEffector(ParticleEffector effector)
	{
		for (int i=0;i<m_particles.length;i++)
		{
			if (m_particles[i].m_alive)
			{
				effector.effect(m_particles[i]);
			}
		}
	}
	
	protected void setColour(int tint, float lSpan)
	{
		float r=colourStart[0]+(colourTransition[0]*(1-lSpan));
		float g=colourStart[1]+(colourTransition[1]*(1-lSpan));
		float b=colourStart[2]+(colourTransition[2]*(1-lSpan));
		GL20.glUniform4f(tint, r, g,
				b, 1);	
	}	
	
	protected Vec2f getVelocity()
	{
		float v=minSpeed;
		if (maxSpeed>minSpeed)
		{
			 v+=GameManager.m_random.nextFloat() *(maxSpeed-minSpeed);
		}
		float r = GameManager.m_random.nextFloat() * 6.28F;
		Vec2f vel = new Vec2f(v,0);
		vel.rotate(r);
		return vel;
	}
	

}
