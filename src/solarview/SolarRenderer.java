package solarview;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import nomad.StarSystem;
import nomad.universe.Universe;
import particlesystem.ParticleEmitterAdvanced;
import rendering.Sprite;
import rendering.SpriteBatch;
import rendering.SpriteManager;
import rendering.SpriteRotatable;
import shared.Geometry;
import shared.Vec2f;
import shared.Vec2i;

public class SolarRenderer extends SpriteManager {

	private Matrix4f m_viewMatrix;

	private float scale;
	private Vec2f currentPosition;
	private StarSystem currentSystem;
	private ParticleEmitterAdvanced particleEmitter;
	
	
	public SolarRenderer(StarSystem system) {
		super("assets/art/solar/");
		currentSystem = system;

		generate();
		scale = 4;
		currentPosition = new Vec2f(0, 0);
		m_viewMatrix = new Matrix4f();
		setMatrix();

		particleEmitter=new ParticleEmitterAdvanced(256, "particles/white", currentPosition, 1, 1, 0.1F, true);
		float []start={0.5F,0.5F,1.0F};
		float []end={1.0F,0.0F,0.0F};
		
		particleEmitter.setColours(start,end);
	}

	private void setMatrix() {
		Matrix4f.setIdentity(m_viewMatrix);
		m_viewMatrix.m00 = 0.025F * scale;
		m_viewMatrix.m11 = 0.03125F * scale;
		m_viewMatrix.m22 = 1.0F;
		m_viewMatrix.m33 = 1.0F;
		float xscale = 1 / (0.025F * scale);
		float yscale = 1 / (0.03125F * scale);
		m_viewMatrix.m30 = ((currentPosition.x * -1) / xscale) - 0.32F;
		m_viewMatrix.m31 = ((currentPosition.y * -1) / yscale) + 0.125F;

	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
		setMatrix();
	}

	public Vec2f getCurrentPosition() {
		return currentPosition;
	}

	public void setCurrentPosition(Vec2f currentPosition) {
		this.currentPosition = currentPosition;
		particleEmitter.setPosition(currentPosition);
		setMatrix();
	}

	private void generate() {
		for (int i = 0; i < currentSystem.getEntities().size(); i++) {
			// build sprite

			Sprite sprite = null;
			if (currentSystem.getEntities().get(i).getClass().getName().contains("Spaceship")) {
				sprite = new SpriteRotatable(currentSystem.getEntities().get(i).getPosition(), 1);
				sprite.setSpriteSize(currentSystem.getEntities().get(i).getSpriteSize());
			} else {
				sprite = new Sprite(currentSystem.getEntities().get(i).getPosition(),
						currentSystem.getEntities().get(i).getSpriteSize(), 1);
			}
			// sprite visible
			sprite.setVisible(true);

			// add to batch
			addSprite(sprite, currentSystem.getEntities().get(i).getSprite() + ".png");
			// attach to entity
			currentSystem.getEntities().get(i).setSpriteObj(sprite);
		}
		
		
	}
	
	public void generateWarpHelpers(float distance, Universe universe)
	{
		for (int i=0;i<8;i++)
		{
			Vec2i p=Geometry.getPos(i, 0, 0);
			StarSystem system=universe.getSystem(p.x, p.y);
			if (system!=null)
			{
				
				Vec2f pos=new Vec2f (p.x*1,p.y*-1);
				pos.normalize();
				pos.x*=40;
				pos.y*=40;
				Sprite sprite= new Sprite(pos,2,1);
				sprite.setVisible(true);
				addSprite(sprite,"warpTarget"+".png");
			}
		}
	}

	public void solarDraw(int viewMatrix, int objmatrix, int tintvar, FloatBuffer matrix44Buffer) {
		
		m_viewMatrix.store(matrix44Buffer);
		matrix44Buffer.flip();
		GL20.glUniformMatrix4fv(viewMatrix, false, matrix44Buffer);
		particleEmitter.draw(matrix44Buffer, objmatrix, tintvar);
		GL20.glUniform4f(tintvar, 1, 1,
				1, 1);	
		draw(objmatrix, tintvar, matrix44Buffer);

	
	}

	public void end() {
		discard();
		for (int i = 0; i < currentSystem.getEntities().size(); i++) {
			if (currentSystem.getEntities().get(i).getSpriteObj() != null) {
				removeSprite(currentSystem.getEntities().get(i).getSpriteObj(),currentSystem.getEntities().get(i).getSprite());
				currentSystem.getEntities().get(i).setSpriteObj(null);
			}
		}
		particleEmitter.Discard();
	}

	public ParticleEmitterAdvanced getParticleEmitter() {
		return particleEmitter;
	}

	
}
