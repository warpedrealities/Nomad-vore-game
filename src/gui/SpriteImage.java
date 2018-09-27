package gui;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.util.vector.Matrix4f;

import shared.Tools;
import shared.Vec2f;
import shared.Vertex;

public class SpriteImage extends Image {

	int frameIndex, numFrames;
	Vec2f size;

	public SpriteImage(Vec2f p, Vec2f size, String texture, int numFrames) {
		m_textureID = Tools.loadPNGTexture(texture, GL13.GL_TEXTURE0);

		m_pos = p;
		// setup the ninepatch

		// setup matrix
		m_matrix = new Matrix4f();
		m_matrix.m00 = 1;
		m_matrix.m11 = 1;
		m_matrix.m22 = 1;
		m_matrix.m33 = 1;
		m_matrix.m30 = p.x;
		m_matrix.m31 = p.y;
		isVisible = true;
		this.numFrames = numFrames;
		this.size = size;
		Generate(size);
	}

	public int getFrameIndex() {
		return frameIndex;
	}

	public void setFrameIndex(int frameIndex) {
		this.frameIndex = frameIndex;
		regenerate(frameIndex);
	}

	protected void regenerate(int index) {
		float sqrt = (float) Math.sqrt((float) numFrames);

		int U = index % (int) sqrt;
		int V = index / (int) sqrt;
		float Uf = U * (1.0F / sqrt);
		float Vf = V * (1.0F / sqrt);
		// build the four vertexes for the square
		Vertex v[] = new Vertex[4];
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(4 * 6);
		v[0] = new Vertex(0, 0, 0, Uf, Vf + (1 / sqrt));
		v[1] = new Vertex(0 + size.x, 0, 0, Uf + (1 / sqrt), Vf + (1 / sqrt));
		v[2] = new Vertex(0 + size.x, 0 + size.y, 0, Uf + (1 / sqrt), Vf);
		v[3] = new Vertex(0, 0 + size.y, 0, Uf, Vf);

		for (int k = 0; k < 4; k++) {
			v[k].pos[0] -= 0.5F;
			v[k].pos[1] -= 0.5F;
			verticesBuffer.put(v[k].pos);
			verticesBuffer.put(v[k].tex);
		}

		verticesBuffer.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, verticesBuffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

}
