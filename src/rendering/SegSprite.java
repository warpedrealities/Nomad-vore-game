package rendering;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import shared.Tools;
import shared.Vec2f;
import shared.Vertex_ext;
import vmo.Tile_Int;

public class SegSprite {

	int m_texture, m_width, m_height;
	int m_VIO, m_VBO, m_VAO, m_indicecount;
	Tile_Int m_tiles[];
	Matrix4f m_matrix;

	public SegSprite(String filename, Tile_Int tiles[], Vec2f position, int width, int height) {
		position.y += 1;
		m_matrix = new Matrix4f();
		m_matrix = Matrix4f.setIdentity(m_matrix);
		// Vector3f scale=new Vector3f((float)width/2,(float)height/2,1);
		// m_matrix.scale(scale);
		m_matrix.translate(new Vector2f(position.x, position.y));
		m_texture = Tools.loadPNGTexture("assets/art/bigsprites/" + filename + ".png", GL13.GL_TEXTURE0);
		m_tiles = tiles;
		m_width = width;
		m_height = height;
	}

	void Create(int count) {
		Discard();

		// build new elements based on a specific level
		m_VAO = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(m_VAO);

		m_VBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO);

		// build the vertex buffer here
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(4 * Vertex_ext.size * count);
		// build indice buffer, it's not complex its a repeating pattern of six
		// offset through the sequence
		IntBuffer indiceBuffer = BufferUtils.createIntBuffer(6 * count);
		int index = 0; // use this to keep track of where we're placing things
						// in the list

		// build squares, such squares
		for (int i = 0; i < 4; i++) {
			boolean b = false;

			if (m_tiles == null) {
				b = true;
			}
			if (m_tiles != null) {
				if (m_tiles[i].getExplored() == true) {
					b = true;
				}
			}

			if (b == true) {
				float spanx = (float) m_width / 2;
				float spany = (float) m_height / 2;
				float x = i % 2;
				float y = 1 + (i / 2);

				float uf = (float) x / 2;
				float vf = (float) y / 2;

				x = x * spanx;
				y = y * spany;

				float lighting = 0.5F;
				if (m_tiles != null) {
					if (m_tiles[i].getVisible() == true) {
						lighting = 1;
					}

				} else {
					lighting = 1;
				}

				Vertex_ext v[] = new Vertex_ext[4];
				v[0] = new Vertex_ext((x), (y), 0, uf, vf + 0.5f, lighting);
				v[1] = new Vertex_ext((x), (y) + spany, 0, uf, vf, lighting);
				v[2] = new Vertex_ext((x) + spanx, (y) + spany, 0, uf + 0.5f, vf, lighting);
				v[3] = new Vertex_ext((x) + spanx, (y), 0, uf + 0.5f, vf + 0.5f, lighting);
				for (int k = 0; k < 4; k++) {
					verticesBuffer.put(v[k].pos);
					verticesBuffer.put(v[k].tex);
					verticesBuffer.put(v[k].m_light);
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

		verticesBuffer.flip();
		indiceBuffer.flip();
		m_indicecount = count * 6;
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_DYNAMIC_DRAW);

		GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, Vertex_ext.size, 0);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, Vertex_ext.size, 4 * 4);
		GL20.glVertexAttribPointer(2, 1, GL11.GL_FLOAT, false, Vertex_ext.size, 4 * 6);
		m_VIO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m_VIO);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indiceBuffer, GL15.GL_DYNAMIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

	}

	void Alter(int count) {
		// build the vertex buffer here
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(4 * Vertex_ext.size * count);
		// build indice buffer, it's not complex its a repeating pattern of six
		// offset through the sequence
		int index = 0; // use this to keep track of where we're placing things
						// in the list

		// build squares, such squares
		// build squares, such squares
		for (int i = 0; i < 4; i++) {
			boolean b = false;

			if (m_tiles == null) {
				b = true;
			}
			if (m_tiles != null) {
				if (m_tiles[i].getExplored() == true) {
					b = true;
				}
			}

			if (b == true) {
				float spanx = (float) m_width / 2;
				float spany = (float) m_height / 2;
				float x = i % 2;
				float y = 1 + (i / 2);

				float uf = (float) x / 2;
				float vf = (float) y / 2;

				x = x * spanx;
				y = y * spany;

				float lighting = 0.5F;
				if (m_tiles != null) {
					if (m_tiles[i].getVisible() == true) {
						lighting = 1;
					}

				} else {
					lighting = 1;
				}

				Vertex_ext v[] = new Vertex_ext[4];
				v[0] = new Vertex_ext((x), (y), 0, uf, vf + 0.5f, lighting);
				v[1] = new Vertex_ext((x), (y) + spany, 0, uf, vf, lighting);
				v[2] = new Vertex_ext((x) + spanx, (y) + spany, 0, uf + 0.5f, vf, lighting);
				v[3] = new Vertex_ext((x) + spanx, (y), 0, uf + 0.5f, vf + 0.5f, lighting);
				for (int k = 0; k < 4; k++) {
					verticesBuffer.put(v[k].pos);
					verticesBuffer.put(v[k].tex);
					verticesBuffer.put(v[k].m_light);
				}
				// increment index for purposes of alignment
				index++;
			}
		}

		verticesBuffer.flip();
		GL30.glBindVertexArray(m_VAO);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, verticesBuffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}

	public void Generate() {
		int count = 0;
		if (m_tiles == null) {
			count = 4;
		} else {
			for (int i = 0; i < 4; i++) {
				if (m_tiles[i].getExplored() == true) {
					count++;
				}
			}
		}

		if (m_indicecount / 6 != count) {
			Create(count);
		} else {
			Alter(count);
		}

	}

	public void Draw(int objmatrix, FloatBuffer matrix44Buffer) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, m_texture);
		// set position
		m_matrix.store(matrix44Buffer);
		matrix44Buffer.flip();
		GL20.glUniformMatrix4fv(objmatrix, false, matrix44Buffer);
		if (m_indicecount > 0) {
			// bind all the links to chunk elements
			GL30.glBindVertexArray(m_VAO);
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL20.glEnableVertexAttribArray(2);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m_VIO);
			GL11.glDrawElements(GL11.GL_TRIANGLES, m_indicecount, GL11.GL_UNSIGNED_INT, 0);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			GL20.glDisableVertexAttribArray(2);
			GL30.glBindVertexArray(0);
		}
	}

	public void CleanTexture() {
		GL11.glDeleteTextures(m_texture);
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

}
