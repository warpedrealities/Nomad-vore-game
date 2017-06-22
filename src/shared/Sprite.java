package shared;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Sprite {

	int m_VBO, m_VAO, m_VIO, m_indicecount, m_texture;

	Matrix4f m_matrix;

	public Sprite(String spritename, Vec2f position, float scale) {
		m_texture = Tools.loadPNGTexture("assets/art/" + spritename + ".png", GL13.GL_TEXTURE0);
		m_matrix = new Matrix4f();
		Matrix4f.setIdentity(m_matrix);
		Vector3f vec = new Vector3f();
		vec.x = position.x;
		vec.y = position.y;

		Matrix4f.translate(vec, m_matrix, m_matrix);

		m_matrix.m00 = scale;
		m_matrix.m11 = scale;
		Generate(scale);

	}

	void Generate(float scale) {

		m_VAO = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(m_VAO);

		m_VBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO);
		// build the vertex buffer here
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(4 * 6);
		// build indice buffer, it's not complex its a repeating pattern of six
		// offset through the sequence
		IntBuffer indiceBuffer = BufferUtils.createIntBuffer(6);

		// build the four vertexes for the square
		Vertex v[] = new Vertex[4];

		/*
		 * v[0]=new Vertex(0, 0, 0,0, 1); v[1]=new Vertex(0+scale, 0, 0,1,1);
		 * v[2]=new Vertex(0+scale, 0+scale, 0, 1, 0); v[3]=new Vertex(0,
		 * 0+scale, 0, 0, 0);
		 */

		v[0] = new Vertex(0, 0, 0, 0, 1);
		v[1] = new Vertex(0 + 1, 0, 0, 1, 1);
		v[2] = new Vertex(0 + 1, 0 + 1, 0, 1, 0);
		v[3] = new Vertex(0, 0 + 1, 0, 0, 0);

		for (int k = 0; k < 4; k++) {
			verticesBuffer.put(v[k].pos);
			verticesBuffer.put(v[k].tex);
		}

		int buffer[] = new int[6];
		buffer[0] = 0;
		buffer[1] = 1;
		buffer[2] = 3;
		buffer[3] = 1;
		buffer[4] = 2;
		buffer[5] = 3;
		indiceBuffer.put(buffer);

		verticesBuffer.flip();
		indiceBuffer.flip();
		m_indicecount = 6;
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);

		GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, Vertex.size, 0);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, Vertex.size, 4 * 4);

		m_VIO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m_VIO);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indiceBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

	}

	public int getTexture() {
		return m_texture;
	}

	public void Draw(int objmatrix, int tintvar, FloatBuffer matrix44fbuffer) {

		// set objmatrix
		m_matrix.store(matrix44fbuffer);
		matrix44fbuffer.flip();
		GL20.glUniformMatrix4fv(objmatrix, false, matrix44fbuffer);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, m_texture);

		// link mesh
		if (m_indicecount > 0) {
			// bind all the links to chunk elements
			GL30.glBindVertexArray(m_VAO);
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m_VIO);
			GL11.glDrawElements(GL11.GL_TRIANGLES, m_indicecount, GL11.GL_UNSIGNED_INT, 0);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			GL30.glBindVertexArray(0);
		}
	}

	public void Discard() {
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
		GL11.glDeleteTextures(m_texture);
	}

}
