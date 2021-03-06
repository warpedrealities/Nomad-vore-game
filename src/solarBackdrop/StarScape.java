package solarBackdrop;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import nomad.universe.Universe;
import rendering.Sprite;
import rendering.SpriteManager;
import shared.Vec2f;

public class StarScape extends SpriteManager {

	protected Matrix4f backdropMatrix;
	protected float viewScale;
	protected Vec2f currentPosition;
	protected float FAKE_PARALLAX=0.1F;
	
	public StarScape() {
		super("assets/art/solar/");
		currentPosition = new Vec2f(0, 0);
		backdropMatrix = new Matrix4f();
		viewScale = 4;
		setMatrix();
		FAKE_PARALLAX=0.1F;
		generate();
	}

	public StarScape(String spriteFolder) {
		super(spriteFolder);
		currentPosition = new Vec2f(0, 0);
		backdropMatrix = new Matrix4f();
		viewScale = 4;
		FAKE_PARALLAX=0.1F;
		
	}

	public Matrix4f getBackdropMatrix() {
		return backdropMatrix;
	}

	public void generate() {
		// generate a grid of points x by x spacing
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 16; j++) {
				Vec2f p = new Vec2f((i * 2) - 20, (j * 2) - 16);
				p.x += Universe.m_random.nextFloat() % 8;
				p.y += Universe.m_random.nextFloat() % 8;
				p.x -= Universe.m_random.nextFloat() % 8;
				p.y -= Universe.m_random.nextFloat() % 8;
				float s = 0.05F + (Universe.m_random.nextFloat() % 0.15F);
				Sprite sprite = new BackdropStar(p);
				int v = Universe.m_random.nextInt(4);
				sprite.setVisible(true);
				sprite.setSpriteSize(s);
				sprite.setImage(v);
				addSprite(sprite, "backdrop.png");
			}
		}
	}

	public void setScale(float scale) {
		// viewScale=scale;
		setMatrix();
	}

	public void setCurrentPosition(Vec2f pos) {
		currentPosition.x = pos.x;
		currentPosition.y = pos.y;
		setMatrix();
	}

	
	public void draw(int viewMatrix, int objmatrix, int tintvar, FloatBuffer matrix44Buffer) {

		backdropMatrix.store(matrix44Buffer);
		matrix44Buffer.flip();
		GL20.glUniformMatrix4fv(viewMatrix, false, matrix44Buffer);
		draw(objmatrix, tintvar, matrix44Buffer);

	}

	public void setDistortion(int direction, float distortion) {
		for (int i = 0; i < spriteBatches.size(); i++) {
			for (int j = 0; j < spriteBatches.get(i).getSprites().size(); j++) {
				if (BackdropStar.class.isInstance(spriteBatches.get(i).getSprites().get(j))) {
					BackdropStar star = (BackdropStar) spriteBatches.get(i).getSprites().get(j);
					star.setDistortion(direction, distortion);
				}

			}
		}
	}

	protected void setMatrix() {
		Matrix4f.setIdentity(backdropMatrix);
		backdropMatrix.m00 = 0.025F * viewScale;
		backdropMatrix.m11 = 0.03125F * viewScale;
		backdropMatrix.m22 = 1.0F;
		backdropMatrix.m33 = 1.0F;
		float xscale = 1 / (0.025F * viewScale);
		float yscale = 1 / (0.03125F * viewScale);
		backdropMatrix.m30 = ((currentPosition.x * -FAKE_PARALLAX) / xscale) - 0.32F;
		backdropMatrix.m31 = ((currentPosition.y * -FAKE_PARALLAX) / yscale) + 0.125F;

	}

}
