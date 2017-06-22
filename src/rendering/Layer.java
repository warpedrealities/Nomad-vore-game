package rendering;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import shared.Vertex;
import vmo.Chunk_Int;
import vmo.Tile_Int;

public class Layer {
	int[] m_VBO, m_VAO, m_VIO, m_indicecount;

	boolean m_draw;

	public Layer() {
		m_VBO = new int[3];
		m_VAO = new int[3];
		m_VIO = new int[3];
		m_indicecount = new int[3];
		for (int i = 0; i < 3; i++) {
			m_VBO[i] = -1;
			m_VAO[i] = -1;
			m_VIO[i] = -1;
			m_indicecount[i] = 0;
		}

		m_draw = false;
	}

	public boolean getDraw() {
		return m_draw;
	}

	public int getTileCount() {
		return m_indicecount[0] / 6;
	}

	public int getWidgetCount() {
		return m_indicecount[1] / 6;
	}

	public void Discard() {
		m_draw = false;
		for (int i = 0; i < 3; i++) {
			if (m_VAO[i] != -1) {
				GL30.glBindVertexArray(m_VAO[i]);
				GL20.glDisableVertexAttribArray(0);
				GL20.glDisableVertexAttribArray(1);
				if (m_VBO[i] != -1) {
					GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
					GL15.glDeleteBuffers(m_VBO[i]);
				}

				if (m_VIO[i] != -1) {
					GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
					GL15.glDeleteBuffers(m_VIO[i]);
				}
				GL30.glBindVertexArray(0);
				GL30.glDeleteVertexArrays(m_VAO[i]);
			}
		}
	}

	public void Generate(Tile_Int[][] tiles, int offsetx, int offsety) {

		// discard elements if they already exist
		Discard();

		m_draw = true;
		// build new elements based on a specific level
		m_VAO[0] = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(m_VAO[0]);

		m_VBO[0] = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO[0]);

		int count = 0;

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				if (tiles[i + offsetx][j + offsety].getImage() > 0) {
					count++;
				}
			}
		}

		// build the vertex buffer here
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(4 * 6 * count);
		// build indice buffer, it's not complex its a repeating pattern of six
		// offset through the sequence
		IntBuffer indiceBuffer = BufferUtils.createIntBuffer(6 * count);
		int index = 0; // use this to keep track of where we're placing things
						// in the list

		// build squares, such squares
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				if (tiles[i + offsetx][j + offsety].getImage() > 0) {
					// generate square,

					// precalc UV based on value
					int V = (tiles[i + offsetx][j + offsety].getImage() - 1) / 4;
					int U = (tiles[i + offsetx][j + offsety].getImage() - 1) % 4;
					float Uf = U * 0.25f;
					float Vf = V * 0.25f;

					Vertex v[] = new Vertex[4];
					v[0] = new Vertex(i, j, 0, Uf, Vf + 0.25f);
					v[1] = new Vertex(i + 1, j, 0, Uf + 0.25f, Vf + 0.25f);
					v[2] = new Vertex(i + 1, j + 1, 0, Uf + 0.25f, Vf);
					v[3] = new Vertex(i, j + 1, 0, Uf, Vf);
					for (int k = 0; k < 4; k++) {
						verticesBuffer.put(v[k].pos);
						verticesBuffer.put(v[k].tex);
					}
					// generate indices offset by 6*index
					int buffer[] = new int[6];
					buffer[0] = index * 4;
					buffer[1] = (index * 4) + 1;
					buffer[2] = (index * 4) + 3;
					buffer[3] = (index * 4) + 1;
					buffer[4] = (index * 4) + 2;
					buffer[5] = (index * 4) + 3;
					indiceBuffer.put(buffer);
					// increment index for purposes of alignment
					index++;
				}
			}
		}

		verticesBuffer.flip();
		indiceBuffer.flip();
		m_indicecount[0] = count * 6;
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STREAM_DRAW);

		GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, Vertex.size, 0);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, Vertex.size, 4 * 4);

		m_VIO[0] = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m_VIO[0]);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indiceBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

	}

	public void Generate(Chunk_Int chunk) {

		// discard elements if they already exist
		Discard();

		m_draw = true;
		// build new elements based on a specific level
		m_VAO[0] = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(m_VAO[0]);

		m_VBO[0] = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO[0]);

		int count = 0;

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				if (chunk.getTiles(0)[i][j].getImage() > 0) {
					count++;
				}
			}
		}

		// build the vertex buffer here
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(4 * 6 * count);
		// build indice buffer, it's not complex its a repeating pattern of six
		// offset through the sequence
		IntBuffer indiceBuffer = BufferUtils.createIntBuffer(6 * count);
		int index = 0; // use this to keep track of where we're placing things
						// in the list

		// build squares, such squares
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				if (chunk.getTiles(0)[i][j].getImage() > 0) {
					// generate square,

					// precalc UV based on value
					int V = (chunk.getTiles(0)[i][j].getImage() - 1) / 4;
					int U = (chunk.getTiles(0)[i][j].getImage() - 1) % 4;
					float Uf = U * 0.25f;
					float Vf = V * 0.25f;

					Vertex v[] = new Vertex[4];
					v[0] = new Vertex(i, j, 0, Uf, Vf);
					v[1] = new Vertex(i + 1, j, 0, Uf + 0.25f, Vf);
					v[2] = new Vertex(i + 1, j + 1, 0, Uf + 0.25f, Vf + 0.25f);
					v[3] = new Vertex(i, j + 1, 0, Uf, Vf + 0.25f);
					for (int k = 0; k < 4; k++) {
						verticesBuffer.put(v[k].pos);
						verticesBuffer.put(v[k].tex);
					}
					// generate indices offset by 6*index
					int buffer[] = new int[6];
					buffer[0] = index * 4;
					buffer[1] = (index * 4) + 1;
					buffer[2] = (index * 4) + 3;
					buffer[3] = (index * 4) + 1;
					buffer[4] = (index * 4) + 2;
					buffer[5] = (index * 4) + 3;
					indiceBuffer.put(buffer);
					// increment index for purposes of alignment
					index++;
				}
			}
		}

		verticesBuffer.flip();
		indiceBuffer.flip();
		m_indicecount[0] = count * 6;
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);

		GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, Vertex.size, 0);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, Vertex.size, 4 * 4);

		m_VIO[0] = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m_VIO[0]);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indiceBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	public void GenerateWidgets(int level, Chunk_Int chunk) {
		m_draw = true;
		// build new elements based on a specific level

		int count = 0;

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				if (chunk.getTiles(0)[i][j].getWidget() > Byte.MIN_VALUE) {
					count++;
				}
			}
		}
		if (count > 0) {
			m_VAO[1] = GL30.glGenVertexArrays();
			GL30.glBindVertexArray(m_VAO[1]);

			m_VBO[1] = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO[1]);

			// build the vertex buffer here
			FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(4 * 6 * count);
			// build indice buffer, it's not complex its a repeating pattern of
			// six offset through the sequence
			IntBuffer indiceBuffer = BufferUtils.createIntBuffer(6 * count);
			int index = 0; // use this to keep track of where we're placing
							// things in the list

			// build squares, such squares
			for (int i = 0; i < 16; i++) {
				for (int j = 0; j < 16; j++) {
					if (chunk.getTiles(0)[i][j].getWidget() > Byte.MIN_VALUE) {
						// generate square,

						// precalc UV based on value
						int V = (chunk.getTiles(0)[i][j].getWidget() - 1 - Byte.MIN_VALUE) / 4;
						int U = (chunk.getTiles(0)[i][j].getWidget() - 1 - Byte.MIN_VALUE) % 4;
						float Uf = U * 0.25f;
						float Vf = V * 0.25f;

						Vertex v[] = new Vertex[4];
						v[0] = new Vertex(i, j, 0, Uf, Vf + 0.25f);
						v[1] = new Vertex(i + 1, j, 0, Uf + 0.25f, Vf + 0.25f);
						v[2] = new Vertex(i + 1, j + 1, 0, Uf + 0.25f, Vf);
						v[3] = new Vertex(i, j + 1, 0, Uf, Vf);
						for (int k = 0; k < 4; k++) {
							verticesBuffer.put(v[k].pos);
							verticesBuffer.put(v[k].tex);
						}
						// generate indices offset by 6*index
						int buffer[] = new int[6];
						buffer[0] = index * 4;
						buffer[1] = (index * 4) + 1;
						buffer[2] = (index * 4) + 3;
						buffer[3] = (index * 4) + 1;
						buffer[4] = (index * 4) + 2;
						buffer[5] = (index * 4) + 3;
						indiceBuffer.put(buffer);
						// increment index for purposes of alignment
						index++;
					}
				}
			}

			verticesBuffer.flip();
			indiceBuffer.flip();

			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);

			GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, Vertex.size, 0);
			GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, Vertex.size, 4 * 4);

			m_VIO[1] = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m_VIO[1]);
			GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indiceBuffer, GL15.GL_STATIC_DRAW);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		}
		m_indicecount[1] = count * 6;

	}

	public void Draw() {
		if (m_indicecount[0] > 0) {
			// bind all the links to chunk elements
			GL30.glBindVertexArray(m_VAO[0]);
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m_VIO[0]);
			GL11.glDrawElements(GL11.GL_TRIANGLES, m_indicecount[0], GL11.GL_UNSIGNED_INT, 0);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			GL30.glBindVertexArray(0);
		}
	}

	public void DrawWidgets() {
		if (m_indicecount[1] > 0) {
			// bind all the links to chunk elements
			GL30.glBindVertexArray(m_VAO[1]);
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m_VIO[1]);
			GL11.glDrawElements(GL11.GL_TRIANGLES, m_indicecount[1], GL11.GL_UNSIGNED_INT, 0);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			GL30.glBindVertexArray(0);
		}
	}

}
