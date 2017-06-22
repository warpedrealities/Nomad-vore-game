package graphics;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import shared.Callback;
import shared.SceneBase;
import shared.Tools;
import shared.Vertex;

public class Screen_Fade {

	float clock;
	boolean running;
	float duration = 1.0F;
	protected int m_VBO;
	protected int m_VAO;
	protected int m_VIO;
	protected int indicecount;
	protected int textureId;
	protected int tintId;
	protected Matrix4f m_matrix;
	protected Callback callBack;

	public Screen_Fade(Callback callback) {
		this.callBack = callback;
		tintId = SceneBase.getVariables()[0];
		m_matrix = new Matrix4f();
		m_matrix.m00 = 1;
		m_matrix.m11 = 1;
		m_matrix.m22 = 1;
		m_matrix.m33 = 1;

		m_matrix.m30 = 0;
		m_matrix.m31 = 0;
		generate();
		textureId = Tools.loadPNGTexture("assets/art/black.png", GL13.GL_TEXTURE0);
	}

	public void run() {
		clock = 0;
		running = true;
	}

	public void run(float duration) {
		clock = 0;
		this.duration = duration;
		running = true;
	}

	public void update(float DT) {
		if (running == true) {
			clock += DT * 0.5F;

			if (clock > duration * 0.5F && clock < duration * 0.51F) {
				callBack.Callback();
				clock = duration * 0.51F;
			}

			if (clock > duration) {
				clock = 0;
				running = false;
			}
		}
	}

	public boolean active() {
		if (clock > 0 && clock < duration) {
			return true;
		}
		return false;
	}

	private void generate() {
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

		v[0] = new Vertex(-20, -20, 0, 0, 1);
		v[1] = new Vertex(20, -20, 0, 1, 1);
		v[2] = new Vertex(20, 20, 0, 1, 0);
		v[3] = new Vertex(-20, 20, 0, 0, 0);

		for (int k = 0; k < 4; k++) {
			v[k].pos[0] -= 0.5F;
			v[k].pos[1] -= 0.5F;
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
		indicecount = 6;
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_DYNAMIC_DRAW);

		GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, Vertex.size, 0);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, Vertex.size, 4 * 4);

		m_VIO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m_VIO);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indiceBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	public void draw(int objmatrix, FloatBuffer matrix44fbuffer) {
		if (clock > 0) {
			float v = Math.abs(clock - 0.5F);
			v = v * 2.0F;
			v = 1 - v;
			GL20.glUniform4f(tintId, 1.0F, 1.0F, 1.0F, v);
			m_matrix.store(matrix44fbuffer);
			matrix44fbuffer.flip();
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
			GL20.glUniformMatrix4fv(objmatrix, false, matrix44fbuffer);
			// link mesh
			if (indicecount > 0) {
				// bind all the links to chunk elements
				GL30.glBindVertexArray(m_VAO);
				GL20.glEnableVertexAttribArray(0);
				GL20.glEnableVertexAttribArray(1);
				GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m_VIO);
				GL11.glDrawElements(GL11.GL_TRIANGLES, indicecount, GL11.GL_UNSIGNED_INT, 0);
				GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
				GL20.glDisableVertexAttribArray(0);
				GL20.glDisableVertexAttribArray(1);
				GL30.glBindVertexArray(0);
			}
		}
	}

	public void discard() {
		GL11.glDeleteTextures(textureId);
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

}
