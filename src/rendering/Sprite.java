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

public class Sprite implements Square_Int,Renderable {

	protected int m_VBO;
	protected int m_VAO;
	protected int m_VIO;
	protected int indicecount;
	protected int numFrames;
	protected Vec2f spritePosition;
	protected Matrix4f m_matrix;
	protected float spriteSize = 1;
	protected float spriteDepth = 0.4F;
	boolean m_visible;
	protected SpriteBatch batch;
	
	public Sprite() {

	}

	private void commonConstruct(Vec2f vec2f, int numFrames) {
		spritePosition = new Vec2f((int) vec2f.x, (int) vec2f.y);
		m_matrix = new Matrix4f();
		m_matrix.m00 = spriteSize;
		m_matrix.m11 = spriteSize;
		m_matrix.m22 = 1;
		m_matrix.m33 = 1;

		m_matrix.m30 = spritePosition.x + 0.5F;
		m_matrix.m31 = spritePosition.y + 0.5F;
		this.numFrames = numFrames;
		generate(numFrames);
	}

	public Sprite(Vec2f vec2f, float spriteSize, int numFrames) {
		this.spriteSize = spriteSize;
		commonConstruct(vec2f, numFrames);

	}

	public Sprite(Vec2f vec2f) {
		commonConstruct(vec2f, 4);
	}

	protected void generate(int numFrames) {
		float sqrt = (float) Math.sqrt((float) numFrames);

		int U = 0 % (int) sqrt;
		int V = 0 / (int) sqrt;
		m_VAO = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(m_VAO);

		m_VBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO);
		// build the vertex buffer here
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(4 * 6);
		// build indice buffer, it's not complex its a repeating pattern of six
		// offset through the sequence
		IntBuffer indiceBuffer = BufferUtils.createIntBuffer(6);
		float Uf = U * (1 / sqrt);
		float Vf = V * (1 / sqrt);
		// build the four vertexes for the square
		Vertex v[] = new Vertex[4];

		v[0] = new Vertex(0, 0, 0, Uf, Vf + (1 / sqrt));
		v[1] = new Vertex(0 + 1, 0, 0, Uf + (1 / sqrt), Vf + (1 / sqrt));
		v[2] = new Vertex(0 + 1, 0 + 1, 0, Uf + (1 / sqrt), Vf);
		v[3] = new Vertex(0, 0 + 1, 0, Uf, Vf);

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

	protected void regen(int select) {
		float sqrt = (float) Math.sqrt((float) numFrames);

		int U = select % (int) sqrt;
		int V = select / (int) sqrt;
		float Uf = U * (1.0F / sqrt);
		float Vf = V * (1.0F / sqrt);
		// build the four vertexes for the square
		Vertex v[] = new Vertex[4];
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(4 * 6);
		v[0] = new Vertex(0, 0, 0, Uf, Vf + (1 / sqrt));
		v[1] = new Vertex(0 + 1, 0, 0, Uf + (1 / sqrt), Vf + (1 / sqrt));
		v[2] = new Vertex(0 + 1, 0 + 1, 0, Uf + (1 / sqrt), Vf);
		v[3] = new Vertex(0, 0 + 1, 0, Uf, Vf);

		for (int k = 0; k < 4; k++) {
			v[k].pos[0] -= 0.5F;
			v[k].pos[1] -= 0.5F;
			verticesBuffer.put(v[k].pos);
			verticesBuffer.put(v[k].tex);
		}

		verticesBuffer.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, verticesBuffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	public void setVisible(boolean visible) {
		m_visible = visible;
	}

	public void setImage(int value) {
		regen(value);
	}

	public boolean getVisible() {
		return m_visible;
	}

	public void draw(int objmatrix, int tintvar, FloatBuffer matrix44fbuffer) {

		m_matrix.store(matrix44fbuffer);
		matrix44fbuffer.flip();
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
	}

	@Override
	public void setFlashing(int bool) {
		// TODO Auto-generated method stub

	}

	
	public float getSpriteSize() {
		return spriteSize;
	}

	public void reposition(Vec2f p) {
		spritePosition.x = (int) p.x;
		spritePosition.y = (int) p.y;
		m_matrix.m00 = spriteSize;
		m_matrix.m11 = spriteSize;
		m_matrix.m22 = 1;
		m_matrix.m33 = 1;
		m_matrix.m30 = 0;
		m_matrix.m31 = 0;
		m_matrix.m32 = 0;

		m_matrix.m30 = spritePosition.x + 0.5F;
		m_matrix.m31 = spritePosition.y + 0.5F;
	}

	public void repositionF(Vec2f p) {
		spritePosition.x = p.x;
		spritePosition.y = p.y;
		m_matrix.m00 = spriteSize;
		m_matrix.m11 = spriteSize;
		m_matrix.m22 = 1;
		m_matrix.m33 = 1;
		m_matrix.m30 = 0;
		m_matrix.m31 = 0;
		m_matrix.m32 = 0;

		m_matrix.m30 = spritePosition.x + 0.5F;
		m_matrix.m31 = spritePosition.y + 0.5F;
	}

	public void setSpriteSize(float spriteSize) {
		this.spriteSize = spriteSize;

		m_matrix.m00 = spriteSize;
		m_matrix.m11 = spriteSize;
		m_matrix.m22 = 1;
		m_matrix.m33 = 1;
		m_matrix.m30 = 0;
		m_matrix.m31 = 0;
		m_matrix.m32 = 0;

		m_matrix.m30 = spritePosition.x + 0.5F;
		m_matrix.m31 = spritePosition.y + 0.5F;
	}

	@Override
	public void setSpriteBatch(SpriteBatch batch) {
		this.batch=batch;
	}
	
	public SpriteBatch getBatch()
	{
		return this.batch;
	}

	@Override
	public void setColour(float r, float g, float b) {
		// TODO Auto-generated method stub
		
	}
}
