package solarview.spaceEncounter;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import rendering.Sprite;
import rendering.SpriteManager;
import shared.Vec2f;
import solarview.SpriteRotatable;
import solarview.spaceEncounter.EncounterEntities.EncounterShip;
import vmo.Game;

public class EncounterRenderer {

	private SpriteManager spriteManager;
	private Matrix4f m_viewMatrix;
	
	public EncounterRenderer(EncounterShip []ships)
	{
		spriteManager=new SpriteManager("assets/art/solar/");
		m_viewMatrix=new Matrix4f();
		setMatrix();
		buildSprites(ships);
	}

	private void buildSprites(EncounterShip [] ships)
	{
		for (int i=0;i<ships.length;i++)
		{
			SpriteRotatable sprite=new SpriteRotatable(ships[i].getShip().getPosition(),1);	
			
			spriteManager.addSprite(sprite,ships[i].getShip().getSprite()+".png");
			sprite.setVisible(true);
			sprite.reposition(ships[i].getPosition());
			ships[i].setSprite(sprite);
		}
	}
	
	private void setMatrix()
	{
		m_viewMatrix=new Matrix4f();
		m_viewMatrix.m00=0.05F;
		m_viewMatrix.m11=0.0625F;
		m_viewMatrix.m22=1.0F;
		m_viewMatrix.m33=1.0F;
	}
	
	public void draw(int viewMatrix,int objmatrix, int tintvar, FloatBuffer matrix44Buffer)
	{
	
		spriteManager.draw(objmatrix, tintvar, matrix44Buffer);
		
	}
	
	public void discard()
	{
		spriteManager.discard();
	}
}
