package interactionscreens.navscreen;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import shared.Vec2f;
import shared.Vertex;

public class Vortex_View {

	private int vbo;
	private int vio;
	private int vao;
	private static final int indiceCount=6;
	protected Matrix4f m_matrix;
	
	public Vortex_View(Vec2f position, Vec2f size) {
		// TODO Auto-generated constructor stub
		
		//generate matrix
		m_matrix = new Matrix4f();
		m_matrix.m00 = size.x;
		m_matrix.m11 = size.y;
		m_matrix.m22 = 1;
		m_matrix.m33 = 1;

		m_matrix.m30 = position.x;
		m_matrix.m31 = position.y;
		//generate geometry
		generateGeometry();
		
	}

	private void generateGeometry() {
		vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);

		vbo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		// build the vertex buffer here
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(4 * 6);
		// build indice buffer, it's not complex its a repeating pattern of six
		// offset through the sequence
		IntBuffer indiceBuffer = BufferUtils.createIntBuffer(6);
		// build the four vertexes for the square
		Vertex v[] = new Vertex[4];

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
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_DYNAMIC_DRAW);

		GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, Vertex.size, 0);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, Vertex.size, 4 * 4);

		vio = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vio);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indiceBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	public void draw(int objmatrix, int tintvar, FloatBuffer matrix44fbuffer) {

		m_matrix.store(matrix44fbuffer);
		matrix44fbuffer.flip();
		GL20.glUniformMatrix4fv(objmatrix, false, matrix44fbuffer);
			// bind all the links to chunk elements
			GL30.glBindVertexArray(vao);
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vio);
			GL11.glDrawElements(GL11.GL_TRIANGLES, indiceCount, GL11.GL_UNSIGNED_INT, 0);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			GL30.glBindVertexArray(0);	
	}

	public void discard() {
		// TODO Auto-generated method stub
		if (vao != -1) {
			GL30.glBindVertexArray(vao);
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			if (vbo != -1) {
				GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
				GL15.glDeleteBuffers(vbo);
			}

			if (vio != -1) {
				GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
				GL15.glDeleteBuffers(vio);
			}
			GL30.glBindVertexArray(0);
			GL30.glDeleteVertexArrays(vao);
		}
	}

}
