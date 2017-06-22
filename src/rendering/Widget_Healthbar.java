package rendering;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import shared.Vertex;

public class Widget_Healthbar implements Widget {
	int m_VAO, m_VBO, m_VIO;
	int m_max;

	public Widget_Healthbar(int value, int max) {
		m_max = max;

		// build meshes
		m_VAO = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(m_VAO);

		m_VBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO);

		// load from actor

		// set VAO,VBO,VIO
		// int d=m_actor.getSprite(i);
		// create arrays
		// build the vertex buffer here
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(8 * 6);
		// build indice buffer, it's not complex its a repeating pattern of six
		// offset through the sequence
		IntBuffer indiceBuffer = BufferUtils.createIntBuffer(12);

		// precalc UV based on value
		// int V=d/8;
		// int U=d%8;
		float Uf = 0.875F;
		float Vf = 0.0F;
		float h = value;
		float proportion = h / m_max;

		Vertex v[] = new Vertex[8];

		v[0] = new Vertex(-0.5F, -0, 0, Uf, Vf);
		v[1] = new Vertex(-0.5F + (2 * proportion), -0, 0, Uf + 0.0625F, Vf);
		v[2] = new Vertex(-0.5F + (2 * proportion), -0.2F, 0, Uf + 0.0625F, Vf + 0.0625f);
		v[3] = new Vertex(-0.5F, -0.2F, 0, Uf, Vf + 0.0625F);
		Uf += 0.0625F;
		v[4] = new Vertex(1.5F - (2 * (1 - proportion)), -0, 0, Uf, Vf);
		v[5] = new Vertex(-0.5F + 2, -0, 0, Uf + 0.0625F, Vf);
		v[6] = new Vertex(-0.5F + 2, -0.2F, 0, Uf + 0.0625F, Vf + 0.0625f);
		v[7] = new Vertex(1.5F - (2 * (1 - proportion)), -0.2F, 0, Uf, Vf + 0.0625F);

		for (int j = 0; j < 8; j++) {
			verticesBuffer.put(v[j].pos);
			verticesBuffer.put(v[j].tex);
		}
		int buffer[] = new int[12];
		buffer[0] = 0;
		buffer[1] = 1;
		buffer[2] = 3;
		buffer[3] = 1;
		buffer[4] = 2;
		buffer[5] = 3;
		buffer[6] = 4 + 0;
		buffer[7] = 4 + 1;
		buffer[8] = 4 + 3;
		buffer[9] = 4 + 1;
		buffer[10] = 4 + 2;
		buffer[11] = 4 + 3;
		indiceBuffer.put(buffer);

		verticesBuffer.flip();
		indiceBuffer.flip();

		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);

		GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, Vertex.size, 0);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, Vertex.size, 4 * 4);

		m_VIO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m_VIO);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indiceBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

	}

	void Regen(int value) {
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(8 * 6);
		// buffersub data to change meshes
		float Uf = 0.875F;
		float Vf = 0.0F;
		float h = value;
		float proportion = h / m_max;

		Vertex v[] = new Vertex[8];

		v[0] = new Vertex(-0.5F, -0, 0, Uf, Vf);
		v[1] = new Vertex(-0.5F + (2 * proportion), -0, 0, Uf + 0.0625F, Vf);
		v[2] = new Vertex(-0.5F + (2 * proportion), -0.2F, 0, Uf + 0.0625F, Vf + 0.0625f);
		v[3] = new Vertex(-0.5F, -0.2F, 0, Uf, Vf + 0.0625F);
		Uf += 0.0625F;
		v[4] = new Vertex(1.5F - (2 * (1 - proportion)), -0, 0, Uf, Vf);
		v[5] = new Vertex(-0.5F + 2, -0, 0, Uf + 0.0625F, Vf);
		v[6] = new Vertex(-0.5F + 2, -0.2F, 0, Uf + 0.0625F, Vf + 0.0625f);
		v[7] = new Vertex(1.5F - (2 * (1 - proportion)), -0.2F, 0, Uf, Vf + 0.0625F);

		for (int j = 0; j < 8; j++) {
			verticesBuffer.put(v[j].pos);
			verticesBuffer.put(v[j].tex);
		}
		verticesBuffer.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, verticesBuffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	@Override
	public void Draw(FloatBuffer matrix44Buffer, int matrixloc, int colourloc) {
		// TODO Auto-generated method stub
		GL20.glUniform4f(colourloc, 1, 1, 1, 1);
		GL30.glBindVertexArray(m_VAO);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m_VIO);
		// draw, onwards
		GL11.glDrawElements(GL11.GL_TRIANGLES, 12, GL11.GL_UNSIGNED_INT, 0);
		// unlink
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
	}

	@Override
	public void Discard() {
		// TODO Auto-generated method stub
		if (m_VAO != -1) {
			GL30.glBindVertexArray(m_VAO);
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			if (m_VBO != -1) {
				GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
				GL15.glDeleteBuffers(m_VBO);
			}

			if (m_VIO != -1) {
				GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
				GL15.glDeleteBuffers(m_VIO);
			}
			GL30.glBindVertexArray(0);
			GL30.glDeleteVertexArrays(m_VAO);
		}
	}

	@Override
	public void setValue(int value) {
		Regen(value);
	}
}
