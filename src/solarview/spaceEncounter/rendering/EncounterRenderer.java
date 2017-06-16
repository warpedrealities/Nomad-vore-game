package solarview.spaceEncounter.rendering;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import rendering.SpriteManager;
import rendering.SpriteRotatable;
import shared.Vec2f;
import solarBackdrop.StarScape;
import solarview.spaceEncounter.EncounterEntities.EncounterShip;


public class EncounterRenderer {

	private SpriteManager spriteManager;
	private Matrix4f m_viewMatrix;
	private Background background;
	private StarScape stars;

	public EncounterRenderer(EncounterShip[] ships) {
		spriteManager = new SpriteManager("assets/art/solar/");
		m_viewMatrix = new Matrix4f();
		setMatrix();
		buildSprites(ships);
		stars=new StarScape();
		background=new Background();
	}

	private void buildSprites(EncounterShip[] ships) {
		for (int i = 0; i < ships.length; i++) {
			SpriteRotatable sprite = new SpriteRotatable(ships[i].getShip().getPosition(), 1);

			spriteManager.addSprite(sprite, ships[i].getShip().getSprite() + ".png");
			sprite.setVisible(true);
			sprite.reposition(ships[i].getPosition());
			ships[i].setSprite(sprite);
		}
	}

	private void setMatrix() {
		m_viewMatrix = new Matrix4f();
		m_viewMatrix.m00 = 0.05F;
		m_viewMatrix.m11 = 0.0625F;
		m_viewMatrix.m22 = 1.0F;
		m_viewMatrix.m33 = 1.0F;
	}

	public void draw(int viewMatrix, int objmatrix, int tintvar, FloatBuffer matrix44Buffer) {

		m_viewMatrix.store(matrix44Buffer); matrix44Buffer.flip();
		GL20.glUniformMatrix4fv(viewMatrix, false, matrix44Buffer);		
		
		stars.draw(objmatrix, tintvar, matrix44Buffer);
		background.draw(viewMatrix, objmatrix, tintvar, matrix44Buffer);
		GL20.glUniform4f(tintvar,1,1,1,1);
		spriteManager.draw(objmatrix, tintvar, matrix44Buffer);
		
	}

	public void position(Vec2f position,float angle)
	{

		m_viewMatrix.m30=position.x*-0.05F;
		m_viewMatrix.m31=position.y*-0.0625F;
		stars.setCurrentPosition(position);
		background.update(position);
	}
	
	public void discard() {
		stars.discard();
		spriteManager.discard();
		background.discard();
	}
}
