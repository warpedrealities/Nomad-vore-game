package gui;

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

import input.MouseHook;
import shared.Tools;
import shared.Vec2f;
import shared.Vertex;

public class ImagePortrait extends GUIBase {

	int m_VBO, m_VAO, m_VIO, m_indicecount, m_textureID;
	Matrix4f m_matrix;
	float height;
	Vec2f m_pos, size;
	boolean isVisible, expanded;

	public ImagePortrait(Vec2f p, Vec2f size, String texture) {
		m_textureID = Tools.loadPNGTexture(texture, GL13.GL_TEXTURE0);
		this.size = size;
		m_pos = p;
		// setup the ninepatch
		Generate(size);

		// setup matrix
		m_matrix = new Matrix4f();
		m_matrix.m00 = 1;
		m_matrix.m11 = 1;
		m_matrix.m22 = 1;
		m_matrix.m33 = 1;
		m_matrix.m30 = p.x;
		m_matrix.m31 = p.y;
		isVisible = true;

	}

	public void setTexture(String texture, float height) {
		this.height = height;
		GL11.glDeleteTextures(m_textureID);
		m_textureID = Tools.loadPNGTexture(texture, GL13.GL_TEXTURE0);
		if (!expanded) {
			regenerate(height);
		} else {
			regenerateExpanded(height);
		}

	}

	private void regenerateExpanded(float height) {
		// TODO Auto-generated method stub
		Vertex v[] = new Vertex[4];
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(4 * 6);
		float halfHidth = size.x / 2;
		float halfHeight = size.y / 2;
		float lengthening = size.y * (height - 1);

		v[0] = new Vertex(-halfHidth, -halfHeight - lengthening, 0, 0, 1);
		v[1] = new Vertex(-halfHidth + size.x, -halfHeight - lengthening, 0, 1, 1);
		v[2] = new Vertex(-halfHidth + size.x, -halfHeight + (size.y), 0, 1, 0);
		v[3] = new Vertex(-halfHidth, -halfHeight + (size.y), 0, 0, 0);

		for (int k = 0; k < 4; k++) {
			verticesBuffer.put(v[k].pos);
			verticesBuffer.put(v[k].tex);
		}

		verticesBuffer.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, verticesBuffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	private void regenerate(float height) {
		// TODO Auto-generated method stub

		// build the four vertexes for the square
		Vertex v[] = new Vertex[4];
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(4 * 6);
		float halfHidth = size.x / 2;
		float halfHeight = size.y / 2;
		float u = 1 / height;

		v[0] = new Vertex(-halfHidth, -halfHeight, 0, 0, u);
		v[1] = new Vertex(-halfHidth + size.x, -halfHeight, 0, 1, u);
		v[2] = new Vertex(-halfHidth + size.x, -halfHeight + size.y, 0, 1, 0);
		v[3] = new Vertex(-halfHidth, -halfHeight + size.y, 0, 0, 0);

		for (int k = 0; k < 4; k++) {
			verticesBuffer.put(v[k].pos);
			verticesBuffer.put(v[k].tex);
		}

		verticesBuffer.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, verticesBuffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public void setSize(float x, float y) {
		m_matrix = Matrix4f.setIdentity(m_matrix);
		m_matrix.m30 = m_pos.x;
		m_matrix.m31 = m_pos.y;
		Matrix4f.scale(new Vector3f(x, y, 1), m_matrix, m_matrix);
	}

	protected void Generate(Vec2f size) {

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
		float halfHidth = size.x / 2;
		float halfHeight = size.y / 2;

		v[0] = new Vertex(-halfHidth, -halfHeight, 0, 0, 1);
		v[1] = new Vertex(-halfHidth + size.x, -halfHeight, 0, 1, 1);
		v[2] = new Vertex(-halfHidth + size.x, -halfHeight + size.y, 0, 1, 0);
		v[3] = new Vertex(-halfHidth, -halfHeight + size.y, 0, 0, 0);

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
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_DYNAMIC_DRAW);

		GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, Vertex.size, 0);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, Vertex.size, 4 * 4);

		m_VIO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m_VIO);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indiceBuffer, GL15.GL_DYNAMIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	@Override
	public void update(float DT) {
		// TODO Auto-generated method stub
		Vec2f mouse = MouseHook.getInstance().getPosition();
		float x = m_pos.x - (size.x / 2);
		float y = m_pos.y - (size.y / 2);
		if (mouse.x > x && mouse.y > y && mouse.x < x + size.x && mouse.y < y + size.y) {
			if (!expanded) {
				regenerateExpanded(height);
				expanded = true;
			}
		} else {
			if (expanded) {
				regenerate(height);
				expanded = false;
			}
		}
	}

	@Override
	public boolean ClickEvent(Vec2f pos) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void Draw(FloatBuffer buffer, int matrixloc) {
		if (isVisible == true) {

			m_matrix.store(buffer);
			buffer.flip();
			GL20.glUniformMatrix4fv(matrixloc, false, buffer);

			GL11.glBindTexture(GL11.GL_TEXTURE_2D, m_textureID);

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

	public boolean isVisible() {
		return isVisible;
	}

	@Override
	public void discard() {
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
		GL11.glDeleteTextures(m_textureID);
	}

	public boolean isExpanded() {
		return expanded;
	}

	@Override
	public void AdjustPos(Vec2f p) {

		m_matrix.m30 = p.x;
		m_matrix.m31 = p.y;
	}

}
