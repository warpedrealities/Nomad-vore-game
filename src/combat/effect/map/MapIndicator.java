package combat.effect.map;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import gui.GUIBase;
import nomad.universe.Universe;
import shared.Vec2f;
import shared.Vertex;

public class MapIndicator extends GUIBase {
	private int m_VBO, m_VAO, m_VIO, m_indicecount;
	private float gridWidth, gridHeight;
	private Vec2f position;
	Matrix4f m_matrix;
	private boolean m_draw;
	private int texture;

	public MapIndicator(Vec2f position, int texture, int width, int height) {
		this.texture = texture;
		this.position = position;
		this.gridWidth = width;
		this.gridHeight = height;
		m_VBO = -1;
		m_VAO = -1;
		m_VIO = -1;
		m_indicecount = 0;

		m_draw = false;

		m_matrix = new Matrix4f();
		m_matrix.m00 = 1;
		m_matrix.m11 = 1;
		m_matrix.m22 = 1;
		m_matrix.m33 = 1;
		m_matrix.m30 = position.x;
		m_matrix.m31 = position.y;
		generate(Universe.getInstance().getPlayer().getPosition());
	}

	public void setSize(float width, float height) {
		m_matrix.m00 = width / gridWidth;
		m_matrix.m11 = height / gridHeight;
	}

	public void generate(Vec2f playerPos) {

		m_draw = true;
		// build new elements based on a specific level
		m_VAO = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(m_VAO);

		m_VBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO);

		int count = 2;
		// build the vertex buffer here
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(4 * 6 * count);
		// build indice buffer, it's not complex its a repeating pattern of six
		// offset through the sequence
		IntBuffer indiceBuffer = BufferUtils.createIntBuffer(6 * count);

		buildVertical(playerPos, verticesBuffer);
		buildHorizontal(playerPos, verticesBuffer);
		// build squares, such squares

		for (int index = 0; index < count; index++) {
			int buffer[] = new int[6];
			buffer[0] = index * 4;
			buffer[1] = (index * 4) + 1;
			buffer[2] = (index * 4) + 3;
			buffer[3] = (index * 4) + 1;
			buffer[4] = (index * 4) + 2;
			buffer[5] = (index * 4) + 3;
			indiceBuffer.put(buffer);
		}
		verticesBuffer.flip();
		indiceBuffer.flip();
		m_indicecount = count * 6;
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STREAM_DRAW);

		GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, Vertex.size, 0);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, Vertex.size, 4 * 4);

		m_VIO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m_VIO);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indiceBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

	}

	private void buildHorizontal(Vec2f playerPos, FloatBuffer verticesBuffer) {
		// TODO Auto-generated method stub
		Vertex v[] = new Vertex[4];
		v[0] = new Vertex(0, playerPos.y + 0.5F, 0, 0, 1);
		v[1] = new Vertex(playerPos.x + 0.5F, playerPos.y + 0.5F, 0, 1, 1);
		v[2] = new Vertex(playerPos.x + 0.5F, playerPos.y + 0.6F, 0, 1, 0);
		v[3] = new Vertex(0, playerPos.y + 0.6F, 0, 0, 0);
		for (int k = 0; k < 4; k++) {
			verticesBuffer.put(v[k].pos);
			verticesBuffer.put(v[k].tex);
		}
	}

	private void buildVertical(Vec2f playerPos, FloatBuffer verticesBuffer) {
		// TODO Auto-generated method stub
		Vertex v[] = new Vertex[4];
		v[0] = new Vertex(playerPos.x + 0.5F, 0, 0, 0, 1);
		v[1] = new Vertex(playerPos.x + 0.6F, 0, 0, 1, 1);
		v[2] = new Vertex(playerPos.x + 0.6F, playerPos.y + 0.5F, 0, 1, 0);
		v[3] = new Vertex(playerPos.x + 0.5F, playerPos.y + 0.5F, 0, 0, 0);
		for (int k = 0; k < 4; k++) {
			verticesBuffer.put(v[k].pos);
			verticesBuffer.put(v[k].tex);
		}
	}

	@Override
	public void update(float DT) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean ClickEvent(Vec2f pos) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void Draw(FloatBuffer buffer, int matrixloc) {

		m_matrix.store(buffer);
		buffer.flip();
		GL20.glUniformMatrix4fv(matrixloc, false, buffer);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
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

	@Override
	public void discard() {
		m_draw = false;

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
	public void AdjustPos(Vec2f p) {
		position.add(p);
		m_matrix.m30 = position.x;
		m_matrix.m31 = position.y;
	}

}
