package solarBackdrop;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import nomad.universe.Universe;
import rendering.Sprite;
import shared.Vec2f;

public class BackdropStar extends Sprite {

	private float r, g, b;

	public BackdropStar(Vec2f pos) {
		this.numFrames = 4;
		spritePosition = new Vec2f(pos.x, pos.y);
		int imageNum = Universe.m_random.nextInt(4);
		m_matrix = new Matrix4f();
		float s = Universe.m_random.nextFloat() % 2;
		spriteSize = s;
		m_matrix.m00 = s;
		m_matrix.m11 = s;
		m_matrix.m22 = 1;
		m_matrix.m33 = 1;
		Vector3f vec = new Vector3f();
		vec.x = pos.x + 0.5F;
		vec.y = pos.y + 0.5F;
		vec.z = -1;
		// Matrix4f.rotate(((float)2)*-0.785398F, new Vector3f(0,0,1), m_matrix,
		// m_matrix);
		m_matrix.m30 = pos.x + 0.5F;
		m_matrix.m31 = pos.y + 0.5F;

		r = 0.8F + (Universe.m_random.nextFloat() % 0.2F);
		g = 0.8F + (Universe.m_random.nextFloat() % 0.2F);
		b = 0.8F + (Universe.m_random.nextFloat() % 0.2F);

		generate(4);

	}

	public void draw(int objmatrix, int tintvar, FloatBuffer matrix44fbuffer) {

		GL20.glUniform4f(tintvar, r, g, b, 1);
		super.draw(objmatrix, tintvar, matrix44fbuffer);
	}

	public void setDistortion(int direction, float distortion) {
		float facing = ((float) direction) * 0.785398F;
		Matrix4f.setIdentity(m_matrix);
		m_matrix.m00 = (float) (spriteSize * Math.cos(facing));
		m_matrix.m01 = (float) (spriteSize * Math.sin(facing) * -1);
		m_matrix.m10 = (float) (Math.sin(facing) * spriteSize * distortion);
		m_matrix.m11 = (float) Math.cos(facing) * spriteSize * distortion;
		m_matrix.m22 = spriteSize;
		m_matrix.m33 = 1;
		m_matrix.m30 = 0;
		m_matrix.m31 = 0;
		m_matrix.m32 = 0;

		// rotate things
		// Matrix4f.rotate(((float)facing)*-0.785398F, new Vector3f(0,0,1),
		// m_matrix, m_matrix);
		m_matrix.m30 = spritePosition.x + 0.5F;
		m_matrix.m31 = spritePosition.y + 0.5F;

	}

}
