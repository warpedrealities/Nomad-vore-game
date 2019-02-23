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

import rendering.calculators.SectionRotator;
import shared.Vec2f;
import shared.Vertex;

public class CircleOverlay {

	private int VBO,VIO,VAO,indiceCount;
	protected Vec2f spritePosition;
	protected Matrix4f matrix;

	public CircleOverlay()
	{
		matrix= new Matrix4f();
		matrix.setIdentity();
		matrix.m00=4;
		matrix.m11=4;
		generate();
		generateIndices();
	}

	private void generateIndices() {
		IntBuffer indiceBuffer = BufferUtils.createIntBuffer(18);
		int buffer[] = new int[18];
		for (int i = 0; i < 3; i++) {
			int index = i * 6;
			int startVertex = i * 4;
			buffer[index + 0] = startVertex + 0;
			buffer[index + 1] = startVertex + 1;
			buffer[index + 2] = startVertex + 3;
			buffer[index + 3] = startVertex + 1;
			buffer[index + 4] = startVertex + 2;
			buffer[index + 5] = startVertex + 3;
		}
		indiceBuffer.put(buffer);
		indiceBuffer.flip();
		VIO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, VIO);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indiceBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	private void generate()
	{

		VAO = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(VAO);

		VBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
		// build the vertex buffer here
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(12 * 6);
		// build indice buffer, it's not complex its a repeating pattern of six
		// offset through the sequence

		// build the four vertexes for the square
		Vertex v[] = new Vertex[12];

		v[0] = new Vertex(0.5F, 1.2F, 0, 0.0F, 0.0F);
		v[1] = new Vertex(0.5F, 1.1F, 0, 1.0F, 0.0F);
		v[2] = new Vertex(-0.5F, 1.1F, 0, 1.0F, 1.0F);
		v[3] = new Vertex(-0.5F, 1.2F, 0, 1.0F, 0.0F);

		v = SectionRotator.generateBlock(0, 4, v, 45);
		v = SectionRotator.generateBlock(0, 8, v, -45);


		for (int k = 0; k < 12; k++) {
			verticesBuffer.put(v[k].pos);
			verticesBuffer.put(v[k].tex);
		}

		verticesBuffer.flip();
		indiceCount = 18;
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_DYNAMIC_DRAW);

		GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, Vertex.size, 0);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, Vertex.size, 4 * 4);

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	public void regen(float width)
	{
		int sectionCount = getSectionCount(width);
		Vertex v[] = new Vertex[sectionCount * 4];

		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer((sectionCount * 4) * 6);
		indiceCount = sectionCount * 6;

		v[0] = new Vertex(0.5F, 1.2F, 0, 0.0F, 0.0F);
		v[1] = new Vertex(0.5F, 1.1F, 0, 1.0F, 0.0F);
		v[2] = new Vertex(-0.5F, 1.1F, 0, 1.0F, 1.0F);
		v[3] = new Vertex(-0.5F, 1.2F, 0, 1.0F, 0.0F);
		if (width > 1) {
			if (width == 2) {
				v = SectionRotator.generateBlock(1, 4, v, 45);
				v = SectionRotator.generateBlock(-1, 8, v, -45);
			} else {
				v = SectionRotator.generateBlock(0, 4, v, 45);
				v = SectionRotator.generateBlock(0, 8, v, -45);
			}
		}

		for (int k = 0; k < (sectionCount * 4); k++) {
			verticesBuffer.put(v[k].pos);
			verticesBuffer.put(v[k].tex);
		}

		verticesBuffer.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, verticesBuffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	private int getSectionCount(float width) {
		if (width == 1) {
			return 1;
		} else {
			return 3;
		}
	}

	public void draw(int objmatrix, int tintvar, FloatBuffer matrix44fbuffer) {
		matrix.store(matrix44fbuffer);
		matrix44fbuffer.flip();
		GL20.glUniformMatrix4fv(objmatrix, false, matrix44fbuffer);
		// link mesh
		if (indiceCount > 0) {
			// bind all the links to chunk elements
			GL30.glBindVertexArray(VAO);
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, VIO);
			GL11.glDrawElements(GL11.GL_TRIANGLES, indiceCount, GL11.GL_UNSIGNED_INT, 0);
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			GL30.glBindVertexArray(0);
		}
	}

	public void discard() {
		if (VAO != -1) {
			GL30.glBindVertexArray(VAO);
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			if (VBO != -1) {
				GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
				GL15.glDeleteBuffers(VBO);
			}

			if (VIO != -1) {
				GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
				GL15.glDeleteBuffers(VIO);
			}
			GL30.glBindVertexArray(0);
			GL30.glDeleteVertexArrays(VAO);
		}
	}

	public void rotate(float angle)
	{
		Matrix4f.setIdentity(matrix);
		matrix.m00 = 4;
		matrix.m11 = 4;
		matrix.m22 = 4;
		matrix.m33 = 1;
		matrix.m30 = 0;
		matrix.m31 = 0;
		matrix.m32 = 0;

		// rotate things
		Matrix4f.rotate((angle) * -0.785398F, new Vector3f(0, 0, 1), matrix, matrix);


	}

	public void reposition(Vec2f position) {
		matrix.m30=position.x;
		matrix.m31=position.y;

	}

}
