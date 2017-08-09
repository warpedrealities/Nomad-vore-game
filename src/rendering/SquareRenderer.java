package rendering;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import shared.Vec2f;
import shared.Vec2i;
import shared.Vertex;

public class SquareRenderer implements Square_Int {

	int m_VBO, m_VAO, m_VIO, m_indicecount;
	int m_spritenum;
	Vec2i m_position;
	Matrix4f m_matrix;
	boolean m_visible;
	float m_scale;
	Vector4f m_tint;

	int m_flash;
	int m_flashclock;

	public SquareRenderer(int spritenum, Vec2f p, Vector4f tint) {
		m_position = new Vec2i((int) p.x, (int) p.y);
		m_tint = tint;
		// build mesh information
		m_spritenum = spritenum;
		m_scale = 1;
		m_visible = false;
		Generate();
		// build matrix using position
		m_matrix = new Matrix4f();
		m_matrix.m00 = 1;
		m_matrix.m11 = 1;
		m_matrix.m22 = 1;
		m_matrix.m33 = 1;
		Vector3f vec = new Vector3f();
		vec.x = p.x;
		vec.y = p.y;
		vec.z = -0.4F;
		Matrix4f.translate(vec, m_matrix, m_matrix);
	}

	public SquareRenderer(int spritenum, Vec2f p, Vector4f tint, float v) {
		m_position = new Vec2i((int) p.x, (int) p.y);
		m_tint = tint;
		// build mesh information
		m_spritenum = spritenum;
		m_scale = v;
		Generate();
		// build matrix using position
		m_matrix = new Matrix4f();
		m_matrix.m00 = v;
		m_matrix.m11 = v;
		m_matrix.m22 = 1;
		m_matrix.m33 = 1;
		Vector3f vec = new Vector3f();
		vec.x = p.x;
		vec.y = p.y;
		vec.z = -0.4F;
		Matrix4f.translate(vec, m_matrix, m_matrix);
	}

	public void setColour(float r, float g, float b) {
		m_tint.x = r;
		m_tint.y = g;
		m_tint.z = b;
	}

	public SquareRenderer(int spritenum, Vec2i p, Vector4f tint) {
		m_position = p;
		m_tint = tint;
		// build mesh information
		m_spritenum = spritenum;
		m_scale = 1;
		Generate();
		// build matrix using position
		m_matrix = new Matrix4f();
		m_matrix.m00 = 1;
		m_matrix.m11 = 1;
		m_matrix.m22 = 1;
		m_matrix.m33 = 1;
		Vector3f vec = new Vector3f();
		vec.x = p.x;
		vec.y = p.y;
		Matrix4f.translate(vec, m_matrix, m_matrix);
	}

	public void setSprite(int i) {
		m_spritenum = i;
	}

	public void setAlpha(float alpha) {
		m_tint.w = alpha;
	}

	public void Regenerate() {
		int V = m_spritenum / 8;
		int U = m_spritenum % 8;
		float Uf = U * 0.25f;
		float Vf = V * 0.25f;
		// build the four vertexes for the square
		Vertex v[] = new Vertex[4];
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(4 * 6);
		v[0] = new Vertex(0, 0, 0, Uf, Vf + 0.25f);
		v[1] = new Vertex(0 + m_scale, 0, 0, Uf + 0.25f, Vf + 0.25f);
		v[2] = new Vertex(0 + m_scale, 0 + m_scale, 0, Uf + 0.25f, Vf);
		v[3] = new Vertex(0, 0 + m_scale, 0, Uf, Vf);

		for (int k = 0; k < 4; k++) {
			verticesBuffer.put(v[k].pos);
			verticesBuffer.put(v[k].tex);
		}

		verticesBuffer.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, verticesBuffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	void Generate() {
		int U = m_spritenum % 16;
		int V = m_spritenum / 16;
		m_VAO = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(m_VAO);

		m_VBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO);
		// build the vertex buffer here
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(4 * 6);
		// build indice buffer, it's not complex its a repeating pattern of six
		// offset through the sequence
		IntBuffer indiceBuffer = BufferUtils.createIntBuffer(6);
		float Uf = U * 0.0625f;
		float Vf = V * 0.0625f;
		// build the four vertexes for the square
		Vertex v[] = new Vertex[4];

		v[0] = new Vertex(0, 0, 0, Uf, Vf + 0.0625f);
		v[1] = new Vertex(0 + m_scale, 0, 0, Uf + 0.0625f, Vf + 0.0625f);
		v[2] = new Vertex(0 + m_scale, 0 + m_scale, 0, Uf + 0.0625f, Vf);
		v[3] = new Vertex(0, 0 + m_scale, 0, Uf, Vf);

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

	public void setFlashing(int bool) {
		m_flash = bool;
	}

	public void reposition(Vec2f p) {
		m_position.x = (int) p.x;
		m_position.y = (int) p.y;
		m_matrix.m00 = 1;
		m_matrix.m11 = 1;
		m_matrix.m22 = 1;
		m_matrix.m33 = 1;
		m_matrix.m30 = 0;
		m_matrix.m31 = 0;
		m_matrix.m32 = 0;
		Vector3f vec = new Vector3f();
		vec.x = p.x;
		vec.y = p.y;
		vec.z = -0.4F;
		Matrix4f.translate(vec, m_matrix, m_matrix);
	}

	public Vec2i getPosition() {
		return m_position;
	}

	public void Move(int direction) {
		m_matrix.m00 = 1;
		m_matrix.m11 = 1;
		m_matrix.m22 = 1;
		m_matrix.m33 = 1;
		m_matrix.m30 = 0;
		m_matrix.m31 = 0;
		m_matrix.m32 = 0;
		Vector3f vec = new Vector3f();

		switch (direction) {
		case 0:

			m_position.y += 1;
			break;
		case 1:

			m_position.y += 1;
			m_position.x += 1;
			break;
		case 2:

			m_position.x += 1;
			break;
		case 3:

			m_position.y -= 1;
			m_position.x += 1;
			break;
		case 4:

			m_position.y -= 1;
			break;
		case 5:

			m_position.y -= 1;
			m_position.x -= 1;
			break;
		case 6:

			m_position.x -= 1;
			break;
		case 7:

			m_position.y += 1;
			m_position.x -= 1;
			break;
		}
		vec.x = m_position.x;
		vec.y = m_position.y;
		vec.z = -0.4F;
		Matrix4f.translate(vec, m_matrix, m_matrix);
	}

	public void Draw(int objmatrix, int tintvar, FloatBuffer matrix44fbuffer) {
		// set tint

		if (m_flash > 0) {
			if (m_flashclock < 10) {
				GL20.glUniform4f(tintvar, m_tint.x, m_tint.y, m_tint.z, m_tint.w);
			} else {
				if (m_flash == 1) {
					GL20.glUniform4f(tintvar, 0.5F, 0, 0, m_tint.w);
				}
				if (m_flash == 2) {
					GL20.glUniform4f(tintvar, 0.5F, 0, 0.5F, m_tint.w);
				}
			}
			m_flashclock--;
			if (m_flashclock < 0) {
				m_flashclock = 20;
			}
		} else {
			GL20.glUniform4f(tintvar, m_tint.x, m_tint.y, m_tint.z, m_tint.w);
		}
		// set objmatrix
		m_matrix.store(matrix44fbuffer);
		matrix44fbuffer.flip();
		GL20.glUniformMatrix4fv(objmatrix, false, matrix44fbuffer);
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
	}

	public boolean getVisible() {
		return m_visible;
	}

	@Override
	public void setVisible(boolean visible) {
		// TODO Auto-generated method stub
		m_visible = visible;
	}

	@Override
	public void setImage(int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void repositionF(Vec2f p) {
		// TODO Auto-generated method stub

	}

}
