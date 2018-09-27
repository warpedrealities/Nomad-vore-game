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
import shared.Vec2f;
import shared.Vertex;

public class MapGrid extends GUIBase{
	private int[] m_VBO, m_VAO, m_VIO, m_indicecount;
	private float gridWidth,gridHeight;
	private Vec2f position;
	Matrix4f m_matrix;	
	private boolean m_draw;
	private int texture;
	public MapGrid(Vec2f position,int texture) {
		this.texture=texture;
		this.position=position;
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
		
		m_matrix = new Matrix4f();
		m_matrix.m00 = 1;
		m_matrix.m11 = 1;
		m_matrix.m22 = 1;
		m_matrix.m33 = 1;
		m_matrix.m30 = position.x;
		m_matrix.m31 = position.y;		
	}

	public boolean getDraw() {
		return m_draw;
	}

	public int getTileCount() {
		return m_indicecount[0] / 6;
	}

	public void setSize(float width,float height)
	{
		m_matrix.m00 = width/gridWidth;	
		m_matrix.m11 = height/gridHeight;
	}

	public void Generate(int [][]grid) {

		// discard elements if they already exist
		gridWidth=grid.length;
		gridHeight=grid[0].length;		
		m_draw = true;
		// build new elements based on a specific level
		m_VAO[0] = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(m_VAO[0]);

		m_VBO[0] = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO[0]);

		int count = grid.length*grid[0].length;

		// build the vertex buffer here
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(4 * 6 * count);
		// build indice buffer, it's not complex its a repeating pattern of six
		// offset through the sequence
		IntBuffer indiceBuffer = BufferUtils.createIntBuffer(6 * count);
		int index = 0; // use this to keep track of where we're placing things
						// in the list

		// build squares, such squares
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
					// generate square,
					// precalc UV based on value
					int V = (grid[i][j])/ 4;
					int U = (grid[i][j]) % 4;
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



	public void Draw() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);	
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
		// TODO Auto-generated method stub
		m_matrix.store(buffer);
		buffer.flip();
		GL20.glUniformMatrix4fv(matrixloc, false, buffer);

		Draw();
	}

	@Override
	public void discard() {
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

	@Override
	public void AdjustPos(Vec2f p) {
		position.add(p);
		m_matrix.m30 = position.x;
		m_matrix.m31 = position.y;	
	}

}
