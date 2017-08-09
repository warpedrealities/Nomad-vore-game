package rendering;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

import shared.Vec2f;
import shared.Vertex;
import shared.Vertex_ext;
import vmo.Tile_Int;

public class RenderLayer extends Layer {

	Matrix4f m_matrix;
	Vec2f m_position;

	public RenderLayer() {

		m_matrix = new Matrix4f();
		m_matrix = Matrix4f.setIdentity(m_matrix);
	}

	public void Create(Tile_Int[][] tiles, int count, int offsetx, int offsety) {
		// discard elements if they already exist
		Discard(0);

		m_draw = true;
		// build new elements based on a specific level
		m_VAO[0] = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(m_VAO[0]);

		m_VBO[0] = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO[0]);

		// build the vertex buffer here
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(4 * Vertex_ext.size * count);
		// build indice buffer, it's not complex its a repeating pattern of six
		// offset through the sequence
		IntBuffer indiceBuffer = BufferUtils.createIntBuffer(6 * count);
		int index = 0; // use this to keep track of where we're placing things
						// in the list

		// build squares, such squares
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				if (tiles[i + offsetx][j + offsety] != null) {
					if (tiles[i + offsetx][j + offsety].getImage() > 0
							&& tiles[i + offsetx][j + offsety].getExplored() == true) {
						Tile_Int tile = tiles[i + offsetx][j + offsety];
						int tileCountX = tile.getTileset().getTileCountX();
						int tileCountY = tile.getTileset().getTileCountY();
						float scaleX = 1.0f / tileCountX;
						float scaleY = 1.0f / tileCountY;
						// generate square,
						// precalc UV based on value
						int V = (tile.getImage() - 1) / tileCountX;
						int U = (tile.getImage() - 1) % tileCountX;
						float Uf = U * scaleX;
						float Vf = V * scaleY;

						float lighting = 0.5F;
						if (tiles[i + offsetx][j + offsety].getVisible() == true) {
							lighting = 1;
						}
						Vertex_ext v[] = new Vertex_ext[4];
						v[0] = new Vertex_ext(i, j, 0, Uf, Vf + scaleY, lighting);
						v[1] = new Vertex_ext(i + 1, j, 0, Uf + scaleX, Vf + scaleY, lighting);
						v[2] = new Vertex_ext(i + 1, j + 1, 0, Uf + scaleX, Vf, lighting);
						v[3] = new Vertex_ext(i, j + 1, 0, Uf, Vf, lighting);
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
			}
		}

		verticesBuffer.flip();
		indiceBuffer.flip();
		m_indicecount[0] = count * 6;
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_DYNAMIC_DRAW);

		GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, Vertex_ext.size, 0);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, Vertex_ext.size, 4 * 4);
		GL20.glVertexAttribPointer(2, 1, GL11.GL_FLOAT, false, Vertex_ext.size, 4 * 6);
		m_VIO[0] = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m_VIO[0]);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indiceBuffer, GL15.GL_STREAM_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

	}

	public void Alter(Tile_Int[][] tiles, int count, int offsetx, int offsety) {

		// build the vertex buffer here
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(4 * Vertex_ext.size * count);

		int index = 0; // use this to keep track of where we're placing things
						// in the list

		// build squares, such squares
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				if (tiles[i + offsetx][j + offsety] != null) {
					if (tiles[i + offsetx][j + offsety].getImage() > 0
							&& tiles[i + offsetx][j + offsety].getExplored() == true) {
						Tile_Int tile = tiles[i + offsetx][j + offsety];
						int tileCountX = tile.getTileset().getTileCountX();
						int tileCountY = tile.getTileset().getTileCountY();
						float scaleX = 1.0f / tileCountX;
						float scaleY = 1.0f / tileCountY;
						// generate square,
						// precalc UV based on value
						int V = (tile.getImage() - 1) / tileCountX;
						int U = (tile.getImage() - 1) % tileCountX;
						float Uf = U * scaleX;
						float Vf = V * scaleY;

						float lighting = 0.5F;
						if (tiles[i + offsetx][j + offsety].getVisible() == true) {
							lighting = 1;
						}
						Vertex_ext v[] = new Vertex_ext[4];
						v[0] = new Vertex_ext(i, j, 0, Uf, Vf + scaleY, lighting);
						v[1] = new Vertex_ext(i + 1, j, 0, Uf + scaleX, Vf + scaleY, lighting);
						v[2] = new Vertex_ext(i + 1, j + 1, 0, Uf + scaleX, Vf, lighting);
						v[3] = new Vertex_ext(i, j + 1, 0, Uf, Vf, lighting);
						for (int k = 0; k < 4; k++) {
							verticesBuffer.put(v[k].pos);
							verticesBuffer.put(v[k].tex);
							verticesBuffer.put(v[k].m_light);
						}
						// increment index for purposes of alignment
						index++;
					}
				}

			}
		}
		verticesBuffer.flip();
		GL30.glBindVertexArray(m_VAO[0]);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO[0]);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, verticesBuffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}

	public void Discard(int subset) {
		m_draw = false;
		if (m_VAO[subset] != -1) {
			GL30.glBindVertexArray(m_VAO[subset]);
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			if (m_VBO[subset] != -1) {
				GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
				GL15.glDeleteBuffers(m_VBO[subset]);
			}

			if (m_VIO[subset] != -1) {
				GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
				GL15.glDeleteBuffers(m_VIO[subset]);
			}
			GL30.glBindVertexArray(0);
			GL30.glDeleteVertexArrays(m_VAO[subset]);
		}

	}

	@Override
	public void Generate(Tile_Int[][] tiles, int offsetx, int offsety) {
		int count = 0;

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				if (tiles[i + offsetx][j + offsety] != null) {
					if (tiles[i + offsetx][j + offsety].getImage() > 0
							&& tiles[i + offsetx][j + offsety].getExplored() == true) {
						count++;
					}
				}

			}
		}

		if (getTileCount() != count) {
			Create(tiles, count, offsetx, offsety);
		} else if (count > 0) {
			Alter(tiles, count, offsetx, offsety);
		}

	}

	public void GenerateWidgets(Tile_Int[][] tiles, int offsetx, int offsety) {
		int count = 0;

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				if (tiles[i + offsetx][j + offsety] != null) {
					if (tiles[i + offsetx][j + offsety].getImage() > 0
							&& tiles[i + offsetx][j + offsety].getExplored() == true
							&& tiles[i + offsetx][j + offsety].getWidget() > 0) {
						count++;
					}
				}

			}
		}
		if (getWidgetCount() != count) {
			CreateWidgets(tiles, count, offsetx, offsety);
		} else if (count > 0) {
			AlterWidgets(tiles, count, offsetx, offsety);
		}

	}

	public void CreateWidgets(Tile_Int[][] tiles, int count, int offsetx, int offsety) {
		// discard elements if they already exist
		Discard(1);

		m_draw = true;
		// build new elements based on a specific level
		m_VAO[1] = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(m_VAO[1]);

		m_VBO[1] = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO[1]);

		// build the vertex buffer here
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(4 * Vertex_ext.size * count);
		// build indice buffer, it's not complex its a repeating pattern of six
		// offset through the sequence
		IntBuffer indiceBuffer = BufferUtils.createIntBuffer(6 * count);
		int index = 0; // use this to keep track of where we're placing things
						// in the list

		// build squares, such squares
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				if (tiles[i + offsetx][j + offsety] != null) {
					if (tiles[i + offsetx][j + offsety].getWidget() > 0
							&& tiles[i + offsetx][j + offsety].getExplored() == true) {
						// generate square,

						// precalc UV based on value
						int V = (tiles[i + offsetx][j + offsety].getWidget() - 1) / 16;
						int U = (tiles[i + offsetx][j + offsety].getWidget() - 1) % 16;
						float Uf = U * 0.0625f;
						float Vf = V * 0.0625f;

						float lighting = 0.5F;
						if (tiles[i + offsetx][j + offsety].getVisible() == true) {
							lighting = 1;
						}
						Vertex_ext v[] = new Vertex_ext[4];
						v[0] = new Vertex_ext(i, j, 0, Uf, Vf + 0.0625f, lighting);
						v[1] = new Vertex_ext(i + 1, j, 0, Uf + 0.0625f, Vf + 0.0625f, lighting);
						v[2] = new Vertex_ext(i + 1, j + 1, 0, Uf + 0.0625f, Vf, lighting);
						v[3] = new Vertex_ext(i, j + 1, 0, Uf, Vf, lighting);
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
			}
		}

		verticesBuffer.flip();
		indiceBuffer.flip();
		m_indicecount[1] = count * 6;
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_DYNAMIC_DRAW);

		GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, Vertex_ext.size, 0);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, Vertex_ext.size, 4 * 4);
		GL20.glVertexAttribPointer(2, 1, GL11.GL_FLOAT, false, Vertex_ext.size, 4 * 6);
		m_VIO[1] = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m_VIO[1]);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indiceBuffer, GL15.GL_STREAM_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	public void AlterWidgets(Tile_Int[][] tiles, int count, int offsetx, int offsety) {
		// build the vertex buffer here
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(4 * Vertex_ext.size * count);

		int index = 0; // use this to keep track of where we're placing things
						// in the list

		// build squares, such squares
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				if (tiles[i + offsetx][j + offsety] != null) {
					if (tiles[i + offsetx][j + offsety].getWidget() > 0
							&& tiles[i + offsetx][j + offsety].getExplored() == true) {
						// generate square,

						// precalc UV based on value
						int V = (tiles[i + offsetx][j + offsety].getWidget() - 1) / 16;
						int U = (tiles[i + offsetx][j + offsety].getWidget() - 1) % 16;
						float Uf = U * 0.0625f;
						float Vf = V * 0.0625f;

						float lighting = 0.5F;
						if (tiles[i + offsetx][j + offsety].getVisible() == true) {
							lighting = 1;
						}
						Vertex_ext v[] = new Vertex_ext[4];
						v[0] = new Vertex_ext(i, j, 0, Uf, Vf + 0.0625f, lighting);
						v[1] = new Vertex_ext(i + 1, j, 0, Uf + 0.0625f, Vf + 0.0625f, lighting);
						v[2] = new Vertex_ext(i + 1, j + 1, 0, Uf + 0.0625f, Vf, lighting);
						v[3] = new Vertex_ext(i, j + 1, 0, Uf, Vf, lighting);
						for (int k = 0; k < 4; k++) {
							verticesBuffer.put(v[k].pos);
							verticesBuffer.put(v[k].tex);
							verticesBuffer.put(v[k].m_light);
						}
						// increment index for purposes of alignment
						index++;
					}
				}
			}
		}

		verticesBuffer.flip();
		GL30.glBindVertexArray(m_VAO[1]);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO[1]);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, verticesBuffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}

	public void setPosition(Vec2f position) {
		m_position = position;
		// set matrix
		m_matrix.translate(new Vector2f(position.x, position.y));

	}

	public void Draw(int objmatrix, FloatBuffer matrix44Buffer) {
		// set position
		m_matrix.store(matrix44Buffer);
		matrix44Buffer.flip();
		GL20.glUniformMatrix4fv(objmatrix, false, matrix44Buffer);
		if (m_indicecount[0] > 0) {
			// bind all the links to chunk elements
			GL30.glBindVertexArray(m_VAO[0]);
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL20.glEnableVertexAttribArray(2);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m_VIO[0]);
			GL11.glDrawElements(GL11.GL_TRIANGLES, m_indicecount[0], GL11.GL_UNSIGNED_INT, 0);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			GL20.glDisableVertexAttribArray(2);
			GL30.glBindVertexArray(0);
		}
	}

	public void DrawWidgets(int objmatrix, FloatBuffer matrix44Buffer) {
		m_matrix.store(matrix44Buffer);
		matrix44Buffer.flip();
		GL20.glUniformMatrix4fv(objmatrix, false, matrix44Buffer);
		if (m_indicecount[1] > 0) {
			// bind all the links to chunk elements
			GL30.glBindVertexArray(m_VAO[1]);
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL20.glEnableVertexAttribArray(2);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m_VIO[1]);
			GL11.glDrawElements(GL11.GL_TRIANGLES, m_indicecount[1], GL11.GL_UNSIGNED_INT, 0);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			GL20.glDisableVertexAttribArray(2);
			GL30.glBindVertexArray(0);
		}
	}

	public boolean getContains(Vec2f position) {

		return false;
	}

	public Vec2f getPosition() {
		return m_position;
	}

}
