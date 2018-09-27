package shared;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public class NinePatch_Proportional extends NinePatch {

	private float proportion, width, height;

	public NinePatch_Proportional(Vec2f p, float width, float height, float proportion, int textureID) {
		super(p, width, height, textureID);
		this.proportion = proportion;
		reGen(width, height);
		this.width = width;
		this.height = height;

	}

	public void reGen(float width, float height) {
		float prop = width * proportion;
		FloatBuffer params = BufferUtils.createFloatBuffer(4);
		GL11.glGetTexLevelParameterfv(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH, params);
		float twidth = params.get(0);
		GL11.glGetTexLevelParameterfv(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT, params);
		float theight = params.get(0);

		float borderx = 8 / twidth;
		float bordery = 8 / theight;
		Vertex vertices[] = new Vertex[16];
		FloatBuffer verticesBuffer;
		if (prop < 0.4F) {
			verticesBuffer = BufferUtils.createFloatBuffer(8 * 6);
		} else {
			if (prop > width - 0.4F) {
				verticesBuffer = BufferUtils.createFloatBuffer(16 * 6);
			} else {
				verticesBuffer = BufferUtils.createFloatBuffer(12 * 6);
			}
		}

		// build using a lawn mower style, left to right, top to bottom row by
		// row
		vertices[0] = new Vertex(0, 0, 0, 0, 1);
		vertices[1] = new Vertex(0.4F, 0, 0, borderx, 1);
		vertices[2] = new Vertex(width - 0.4F, 0, 0, 1 - borderx, 1);
		vertices[3] = new Vertex(width, 0, 0, 1, 1);
		// row 2
		vertices[4] = new Vertex(0, 0.4F, 0, 0, 1 - bordery);
		vertices[5] = new Vertex(0.4F, 0.4F, 0, borderx, 1 - bordery);
		vertices[6] = new Vertex(width - 0.4F, 0.4F, 0, 1 - borderx, 1 - bordery);
		vertices[7] = new Vertex(width, 0.4F, 0, 1, 1 - bordery);
		// row 3
		vertices[8] = new Vertex(0, height - 0.4F, 0, 0, bordery);
		vertices[9] = new Vertex(0.4F, height - 0.4F, 0, borderx, bordery);
		vertices[10] = new Vertex(width - 0.4F, height - 0.4F, 0, 1 - borderx, bordery);
		vertices[11] = new Vertex(width, height - 0.4F, 0, 1, bordery);
		// row 4
		vertices[12] = new Vertex(0, height, 0, 0, 0);
		vertices[13] = new Vertex(0.4F, height, 0, borderx, 0);
		vertices[14] = new Vertex(width - 0.4F, height, 0, 1 - borderx, 0);
		vertices[15] = new Vertex(width, height, 0, 1, 0);

		// column 1
		vertices[0] = new Vertex(0, 0, 0, 0, 1);
		vertices[4] = new Vertex(0, 0.4F, 0, 0, 1 - bordery);
		vertices[8] = new Vertex(0, height - 0.4F, 0, 0, bordery);
		vertices[12] = new Vertex(0, height, 0, 0, 0);

		if (prop < 0.4F) {
			m_indicecount = 18;
			vertices[1] = new Vertex(prop, 0, 0, borderx, 1);
			vertices[5] = new Vertex(prop, 0.4F, 0, borderx, 1 - bordery);
			vertices[9] = new Vertex(prop, height - 0.4F, 0, borderx, bordery);
			vertices[13] = new Vertex(prop, height, 0, borderx, 0);
		} else {
			vertices[1] = new Vertex(0.4F, 0, 0, borderx, 1);
			vertices[5] = new Vertex(0.4F, 0.4F, 0, borderx, 1 - bordery);
			vertices[9] = new Vertex(0.4F, height - 0.4F, 0, borderx, bordery);
			vertices[13] = new Vertex(0.4F, height, 0, borderx, 0);
		}
		if (prop >= 0.4F && prop < width - 0.4F) {
			m_indicecount = 36;
			vertices[2] = new Vertex(prop, 0, 0, 1 - borderx, 1);
			vertices[6] = new Vertex(prop, 0.4F, 0, 1 - borderx, 1 - bordery);
			vertices[10] = new Vertex(prop, height - 0.4F, 0, 1 - borderx, bordery);
			vertices[14] = new Vertex(prop, height, 0, 1 - borderx, 0);
		} else {
			vertices[2] = new Vertex(width - 0.4F, 0, 0, 1 - borderx, 1);
			vertices[6] = new Vertex(width - 0.4F, 0.4F, 0, 1 - borderx, 1 - bordery);
			vertices[10] = new Vertex(width - 0.4F, height - 0.4F, 0, 1 - borderx, bordery);
			vertices[14] = new Vertex(width - 0.4F, height, 0, 1 - borderx, 0);
		}
		if (prop > width - 0.4F) {
			m_indicecount = 54;
			vertices[3] = new Vertex(prop, 0, 0, 1, 1);
			vertices[7] = new Vertex(prop, 0.4F, 0, 1, 1 - bordery);
			vertices[11] = new Vertex(prop, height - 0.4F, 0, 1, bordery);
			vertices[15] = new Vertex(prop, height, 0, 1, 0);
		}
		for (int i = 0; i < 16; i++) {
			if (vertices[i] != null) {
				verticesBuffer.put(vertices[i].pos);
				verticesBuffer.put(vertices[i].tex);
			}

		}
		verticesBuffer.flip();

		GL30.glBindVertexArray(m_VAO);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, verticesBuffer);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}

	public void setProportion(float proportion) {
		this.proportion = proportion;
		reGen(width, height);
	}

}
