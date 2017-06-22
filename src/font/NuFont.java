package font;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import shared.Vec2f;
import shared.Vertex;
import vmo.Game;
import vmo.GameManager;

public class NuFont {
	int m_VBO, m_VAO, m_VIO, m_indicecount, m_maxlength, m_textureID;
	Matrix4f m_matrix;

	Vec2f m_position;
	// need to generate a line with a maxlength
	// use buffer sub data to adjust it, this way we have enough squares for a
	// big line
	// link a texture

	public NuFont(Vec2f pos, int maxlength, float s) {
		m_position = new Vec2f(pos.x, pos.y);
		m_maxlength = maxlength;
		m_VBO = 0;
		m_VAO = 0;
		m_VIO = 0;
		m_indicecount = 0;
		s = s * Game.sceneManager.getConfig().getTextscale();
		m_matrix = new Matrix4f();
		m_matrix.m00 = s;
		m_matrix.m11 = s;
		m_matrix.m22 = 1;
		m_matrix.m33 = 1;
		m_matrix.m30 = m_position.x;
		m_matrix.m31 = m_position.y;
	}

	public void Draw(FloatBuffer matrixbuffer, int matrixloc) {
		// TODO Auto-generated method stub
		// if indicecount is greater than zero draw the array
		if (m_indicecount > 0) {
			m_matrix.store(matrixbuffer);
			matrixbuffer.flip();
			GL20.glUniformMatrix4fv(matrixloc, false, matrixbuffer);

			FontSupport.getInstance().setTexture();

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

	public void setString(String string) {
		// if VAO isnt created then build it, else buffer sub data
		if (m_VAO == 0) {
			Generate(string);
		} else {
			Amend(string);
		}
	}

	void Generate(String string) {
		// generate maxcount number of squares in a row
		m_VAO = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(m_VAO);

		m_VBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO);

		FloatBuffer verticesbuffer = FontSupport.getInstance().generateGeometry(string, m_maxlength);

		verticesbuffer.flip();
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesbuffer, GL15.GL_STATIC_DRAW);

		GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, Vertex.size, 0);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, Vertex.size, 4 * 4);

		IntBuffer indiceBuffer = BufferUtils.createIntBuffer(6 * m_maxlength);
		for (int i = 0; i < m_maxlength; i++) {
			// build indice pattern
			int[] buffer = { (i * 4) + 0, (i * 4) + 1, (i * 4) + 2, (i * 4) + 2, (i * 4) + 3, (i * 4) + 0 };
			indiceBuffer.put(buffer);
		}

		indiceBuffer.flip();

		m_indicecount = string.length() * 6;
		// indiceBuffer.put(buffer);
		m_VIO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m_VIO);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indiceBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

	}

	Vec2f getPos(byte pos) {
		// find out what the UV coordinates are from the ASCII value

		float U = pos % 16;
		float V = pos / 16;
		return new Vec2f(0.0625F * U, (0.0625F * V));
	}

	void Amend(String string) {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO);
		FloatBuffer verticesbuffer = FontSupport.getInstance().generateGeometry(string, m_maxlength);
		m_indicecount = 6 * string.length();

		verticesbuffer.flip();
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, verticesbuffer);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	public void Discard() {
		// TODO Auto-generated method stub
		if (m_VAO != 0) {
			GL30.glBindVertexArray(m_VAO);
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			if (m_VBO != 0) {
				GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
				GL15.glDeleteBuffers(m_VBO);
			}

			if (m_VIO != 0) {
				GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
				GL15.glDeleteBuffers(m_VIO);
			}
			GL30.glBindVertexArray(0);
			GL30.glDeleteVertexArrays(m_VAO);
		}
	}

	public void AdjustPos(Vec2f p) {
		// TODO Auto-generated method stub
		m_matrix.m30 = p.x + m_position.x;
		m_matrix.m31 = p.y + m_position.y;
	}

	public void ResetPos(Vec2f p) {
		m_position.x = p.x;
		m_position.y = p.y;
		m_matrix.m30 = p.x;
		m_matrix.m31 = p.y;
	}
}
