package shared;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

public class ConcertinaPatch {

	private int m_VBO, m_VAO, m_VIO, m_indicecount, m_textureID;
	private Matrix4f m_matrix;
	private float interval;
	private Vec2f m_pos;
	
	public ConcertinaPatch(Vec2f p, float width, float height, float interval, int textureID) {
		this.m_textureID = textureID;
		this.interval=interval;
		this.m_pos = p;
		// setup the ninepatch
		Generate(width, height);

		// setup matrix
		m_matrix = new Matrix4f();
		m_matrix.m00 = 1;
		m_matrix.m11 = 1;
		m_matrix.m22 = 1;
		m_matrix.m33 = 1;
		m_matrix.m30 = p.x;
		m_matrix.m31 = p.y;

	}
	
	void Generate(float width, float height) {
		int rows=(int)(height/interval);
		m_VAO = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(m_VAO);

		float borderx=0.25F;
		Vertex vertices[] = new Vertex[(rows+2)*4];
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer((rows+2)* 4 * 6);
		int ind=0;
		//first row
		
		vertices[ind++] = new Vertex(0, 0, 0, 0, 1);
		vertices[ind++] = new Vertex(0.5F, 0, 0, borderx, 1);
		vertices[ind++] = new Vertex(width - 0.5F, 0, 0, 1 - borderx, 1);
		vertices[ind++] = new Vertex(width, 0, 0, 1, 1);		
		
		boolean flip=false;
		for (int i=0;i<rows;i++)
		{

			//repeat rows
			float y=0.25F;
			if (flip)
			{
				y=0.75F;
			}
			vertices[ind++] = new Vertex(0, 0+0.5F+(interval*i), 0, 0, y);
			vertices[ind++] = new Vertex(0.5F, 0+0.5F+(interval*i), 0, borderx, y);
			vertices[ind++] = new Vertex(width - 0.5F, 0+0.5F+(interval*i), 0, 1 - borderx, y);
			vertices[ind++] = new Vertex(width, 0+0.5F+(interval*i),0 , 1, y);		
			flip=!flip;
		}
		
		//last row
		vertices[ind++] = new Vertex(0, height, 0, 0, 0);
		vertices[ind++] = new Vertex(0.5F, height, 0, borderx, 0);
		vertices[ind++] = new Vertex(width - 0.5F, height, 0, 1 - borderx, 0);
		vertices[ind++] = new Vertex(width, height, 0, 1, 0);
		for (int i = 0; i < vertices.length; i++) {
			verticesBuffer.put(vertices[i].pos);
			verticesBuffer.put(vertices[i].tex);
		}
		verticesBuffer.flip();
		
	//	int []buffer=new int[3*2*4*1];
		int []buffer=new int[18*(rows+1)];
		int lineDex=0;
		int addDex=0;
		for (int i=0;i<rows+1;i++)
		{
			buffer[addDex++]=lineDex+0;
			buffer[addDex++]=lineDex+1;
			buffer[addDex++]=lineDex+4;
	
			buffer[addDex++]=lineDex+1;
			buffer[addDex++]=lineDex+5;
			buffer[addDex++]=lineDex+4;
			
			buffer[addDex++]=lineDex+1;
			buffer[addDex++]=lineDex+2;
			buffer[addDex++]=lineDex+5;
			
			buffer[addDex++]=lineDex+2;
			buffer[addDex++]=lineDex+6;
			buffer[addDex++]=lineDex+5;
			
			buffer[addDex++]=lineDex+3;
			buffer[addDex++]=lineDex+2;
			buffer[addDex++]=lineDex+6;
			
			buffer[addDex++]=lineDex+6;
			buffer[addDex++]=lineDex+3;
			buffer[addDex++]=lineDex+7;
			
			lineDex+=4;
		}
		IntBuffer indiceBuffer = BufferUtils.createIntBuffer(18*(rows+1));
		indiceBuffer.put(buffer);
		indiceBuffer.flip();
		m_indicecount=18*(rows+1);
		m_VBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m_VBO);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_DYNAMIC_DRAW);

		GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, Vertex.size, 0);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, Vertex.size, 4 * 4);

		m_VIO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, m_VIO);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indiceBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	public void AdjustPos(Vec2f p) {
		m_matrix.m30 = p.x;
		m_matrix.m31 = p.y;
	}

	public void setTexture(int tex) {
		m_textureID = tex;
	}

	public void Draw(FloatBuffer matrixbuffer, int matrixloc) {

		m_matrix.store(matrixbuffer);
		matrixbuffer.flip();
		GL20.glUniformMatrix4fv(matrixloc, false, matrixbuffer);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, m_textureID);

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
