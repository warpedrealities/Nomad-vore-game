package shared;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class NinePatch_Alternate extends NinePatch {

	private boolean alternate;
	private float width,height;
	public NinePatch_Alternate(Vec2f p, float width, float height, int textureID) {
		super(p, width, height, textureID);
		alternate=false;
		this.width=width;
		this.height=height;
	}

	protected void Generate(float width, float height) {
		
		m_indicecount = 54;
		Vertex vertices[] = new Vertex[16];
		m_VAO = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(m_VAO);

		float borderx = 0.125F;
		float bordery = 0.125F;
		m_VBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO);
		// build the vertex buffer here
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(16 * 6);
		IntBuffer indiceBuffer = BufferUtils.createIntBuffer(54);
		float offset=0;
		if (alternate)
		{
			offset=0.5F;
		}
		vertices[0] = new Vertex(0, 0, 0, 0, 0.5F+offset);
		vertices[1] = new Vertex(0.4F, 0, 0, borderx, 0.5F+offset);
		vertices[2] = new Vertex(width - 0.4F, 0, 0, 1 - borderx, 0.5F+offset);
		vertices[3] = new Vertex(width, 0, 0, 1, 0.5F+offset);
		// row 2
		vertices[4] = new Vertex(0, 0.4F, 0, 0, 0.5F - bordery+offset);
		vertices[5] = new Vertex(0.4F, 0.4F, 0, borderx, 0.5F - bordery+offset);
		vertices[6] = new Vertex(width - 0.4F, 0.4F, 0, 1 - borderx, 0.5F - bordery+offset);
		vertices[7] = new Vertex(width, 0.4F, 0, 1, 0.5F - bordery+offset);
		// row 3
		vertices[8] = new Vertex(0, height - 0.4F, 0, 0, bordery+offset);
		vertices[9] = new Vertex(0.4F, height - 0.4F, 0, borderx, bordery+offset);
		vertices[10] = new Vertex(width - 0.4F, height - 0.4F, 0, 1 - borderx, bordery+offset);
		vertices[11] = new Vertex(width, height - 0.4F, 0, 1, bordery+offset);
		// row 4
		vertices[12] = new Vertex(0, height, 0, 0, 0+offset);
		vertices[13] = new Vertex(0.4F, height, 0, borderx, 0+offset);
		vertices[14] = new Vertex(width - 0.4F, height, 0, 1 - borderx, 0+offset);
		vertices[15] = new Vertex(width, height, 0, 1, 0+offset);

		for (int i = 0; i < 16; i++) {
			verticesBuffer.put(vertices[i].pos);
			verticesBuffer.put(vertices[i].tex);
		}
		verticesBuffer.flip();

		int[] buffer = { 0, 1, 4, 1, 5, 4, 1, 2, 5, 2, 6, 5, 2, 3, 6, 3, 7, 6, 4, 5, 8, 5, 8, 9, 5, 6, 9, 6, 9, 10, 6,
				7, 10, 7, 10, 11, 8, 9, 12, 9, 12, 13, 9, 13, 14, 14, 9, 10, 15, 11, 10, 15, 14, 10 };

		indiceBuffer.put(buffer);
		indiceBuffer.flip();

		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_DYNAMIC_DRAW);

		GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, Vertex.size, 0);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, Vertex.size, 4 * 4);

		m_VIO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m_VIO);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indiceBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);		
	}
	
	public void toggle()
	{
		alternate=!alternate;
		reGen(width,height);
	}
	
	public void reGen(float width, float height) {

		float borderx = 0.125F;
		float bordery = 0.125F;
		Vertex vertices[] = new Vertex[16];
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(16 * 6);
		// build using a lawn mower style, left to right, top to bottom row by
		// row
		float offset=0;
		if (alternate)
		{
			offset=0.5F;
		}
		vertices[0] = new Vertex(0, 0, 0, 0, 0.5F+offset);
		vertices[1] = new Vertex(0.4F, 0, 0, borderx, 0.5F+offset);
		vertices[2] = new Vertex(width - 0.4F, 0, 0, 1 - borderx, 0.5F+offset);
		vertices[3] = new Vertex(width, 0, 0, 1, 0.5F+offset);
		// row 2
		vertices[4] = new Vertex(0, 0.4F, 0, 0, 0.5F - bordery+offset);
		vertices[5] = new Vertex(0.4F, 0.4F, 0, borderx, 0.5F - bordery+offset);
		vertices[6] = new Vertex(width - 0.4F, 0.4F, 0, 1 - borderx, 0.5F - bordery+offset);
		vertices[7] = new Vertex(width, 0.4F, 0, 1, 0.5F - bordery+offset);
		// row 3
		vertices[8] = new Vertex(0, height - 0.4F, 0, 0, bordery+offset);
		vertices[9] = new Vertex(0.4F, height - 0.4F, 0, borderx, bordery+offset);
		vertices[10] = new Vertex(width - 0.4F, height - 0.4F, 0, 1 - borderx, bordery+offset);
		vertices[11] = new Vertex(width, height - 0.4F, 0, 1, bordery+offset);
		// row 4
		vertices[12] = new Vertex(0, height, 0, 0, 0+offset);
		vertices[13] = new Vertex(0.4F, height, 0, borderx, 0+offset);
		vertices[14] = new Vertex(width - 0.4F, height, 0, 1 - borderx, 0+offset);
		vertices[15] = new Vertex(width, height, 0, 1, 0+offset);

		for (int i = 0; i < 16; i++) {
			verticesBuffer.put(vertices[i].pos);
			verticesBuffer.put(vertices[i].tex);
		}
		verticesBuffer.flip();

		GL30.glBindVertexArray(m_VAO);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, verticesBuffer);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}	
}
