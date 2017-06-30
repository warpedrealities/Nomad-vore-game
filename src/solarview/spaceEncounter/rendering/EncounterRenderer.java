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
import solarview.spaceEncounter.effectHandling.EffectHandler;

public class EncounterRenderer {

	private SpriteManager spriteManager;
	private Matrix4f m_viewMatrix;
	private Background background;

	private TrailControl trailControl;
	
	private CircleHandler circle;
	
	private EffectHandler effectHandler;
	
	public EncounterRenderer(EncounterShip[] ships) {
		spriteManager = new SpriteManager("assets/art/solar/");
		m_viewMatrix = new Matrix4f();
		setMatrix();
		buildSprites(ships);

		background = new Background();
		trailControl=new TrailControl(ships);
		circle=new CircleHandler();
		circle.setWidth(8);
	}

	private void buildSprites(EncounterShip[] ships) {
		for (int i = 0; i < ships.length; i++) {
			SpriteRotatable sprite = new SpriteRotatable(ships[i].getShip().getPosition(), 1);
			sprite.setCentered(true);
			spriteManager.addSprite(sprite, ships[i].getShip().getSprite() + ".png");
			sprite.setVisible(true);
			
			sprite.repositionF(ships[i].getPosition());
			sprite.setFacing(ships[i].getHeading());
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

		m_viewMatrix.store(matrix44Buffer);
		matrix44Buffer.flip();
		GL20.glUniformMatrix4fv(viewMatrix, false, matrix44Buffer);

	
		background.draw(viewMatrix, objmatrix, tintvar, matrix44Buffer);
		GL20.glUniform4f(tintvar, 1, 1, 1, 1);
		spriteManager.draw(objmatrix, tintvar, matrix44Buffer);
		
		trailControl.draw(matrix44Buffer,objmatrix,tintvar);
		
		effectHandler.draw(matrix44Buffer,objmatrix,tintvar);
		
		circle.draw(objmatrix, tintvar, matrix44Buffer);
	}

	public void position(Vec2f position, float angle) {
		trailControl.reposition();
		m_viewMatrix.m30 = position.x * -0.05F;
		m_viewMatrix.m31 = position.y * -0.0625F;
	
		background.update(position);
		circle.setPosition(position);
	}

	public void discard() {

		spriteManager.discard();
		background.discard();
		trailControl.discard();
		circle.discard();
		effectHandler.discard();
	}

	public TrailControl getTrailControl() {
		return trailControl;
	}

	public CircleHandler getCircle() {
		return circle;
	}

	public void setEffectHandler(EffectHandler effectHandler) {
		this.effectHandler = effectHandler;
	}
	
	
	
}
